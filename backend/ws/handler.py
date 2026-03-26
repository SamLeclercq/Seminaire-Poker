import json
from typing import Callable, Awaitable, TypeAlias

from core.phase import Phase
from core.player import Player
from core.table import Table
from ws.event import Event
from ws.table_manager import table_manager

SendFn: TypeAlias = Callable[[str, str], Awaitable[None]]

def game_state(table: Table, player: Player) -> dict:
    return {
        "tableId": table.table_id,
        "currentHand": table.current_hand,
        "pot": table.pot,
        "communityCards": [str(card) for card in table.community_cards],
        "legalActions": table.get_legal_actions(player),
        "currentState": table.current_state,
        "players": [
            {
                "isCurrentPlayer": player.player_id == p.player_id,
                "isInTurn": table.current_player and table.current_player.player_id == p.player_id,
                "isConnected": p.is_connected, 
                "isActive": p.is_active,
                "isReady": p.is_ready,
                "isDealer": p.is_dealer,
                "isSmallBlind": p.is_small_blind,
                "isBigBlind": p.is_big_blind,
                "balance": p.balance,
                "pocket": [str(card) for card in p.pocket] if (table.current_state == Phase.SHOWDOWN.value or player.player_id == p.player_id) else [],
                "lastAction": p.last_action,
                "currentBet": p.current_bet,
                "playerName": p.name,
            } for p in table.players
        ]
    }


class Handler:
    """
    Initialize an Handler.
    Parses incoming WebSocket JSON messages and dispatches them to the appropriate game handler.
    """
    def __init__(self) -> None:
        self.__handlers = {
                Event.CREATE.value: self.__handle_create,
                Event.JOIN.value: self.__handle_join,
                Event.LEAVE.value: self.__handle_leave,
                Event.READY.value: self.__handle_ready,
                Event.FOLD.value: self.__handle_fold,
                Event.CHECK.value: self.__handle_check,
                Event.CALL.value: self.__handle_call,
                Event.BET.value: self.__handle_bet,
                Event.RAISE.value: self.__handle_raise,
        }

    async def parse(self, raw: str, player_id: str, player_name: str, send: SendFn) -> str:
        """
        Parse a raw JSON string and dispatch to the current handler.

        Expected message format::
            {
                "action": "bet",
                "payload" : {
                    "room_id": "RH5S4H",
                    "amount": 100
                }
            }

        :param raw: The raw JSON string received from the WebSocket.
        :return: JSON-formated string.
        :rtype: str
        """
        try:
            message = json.loads(raw)
        except json.JSONDecodeError:
            return self.error("Malformed JSON.")

        action = message.get("action")
        if action not in [event.value for event in Event]:
            return self.error(f"unknown action: `{action}`")

        if action not in self.__handlers:
            return self.error(f"unsupported action: `{action}`")

        payload = message.get("payload", {})
        return await self.__handlers[action](player_id, player_name, payload, send)


     # ----- Helpers -----
    def success(self, action: Event, data: dict = {}) -> str:
        """
        Send an error message back to the client.

        :param action: The handled action
        :param data: Optional additional data to include in the response
        :return: JSON-formated string.
        """
        return json.dumps({
            "status": "success",
            "action": action.value,
            "data": data
            })

    def error(self, message: str) -> str:
        """
        Send an error message back to the client.

        :param message: The error message.
        :return: JSON-formated string.
        """
        return json.dumps({
            "status": "error",
            "message": message,
            })   


    # ----- Handlers -----
    async def __handle_create(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player creating a table"""
        player = Player(player_id, player_name)
        table = table_manager.create()
        table.add_player(player)
        print(table.table_id)
        return self.success(
            Event.CREATE,
            game_state(table, player)
        )

    async def __handle_join(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player joining the table"""
        table_id:str|None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")

        player = Player(player_id, player_name)
        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")

        table.add_player(player)
        
        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.JOIN, game_state(table, player))

    async def __handle_leave(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player leaving the table"""
        player = Player(player_id, player_name)

        for table in table_manager.tables.values():
            if table.del_player(player_id):
                if not table.players:
                    table_manager.remove(table.table_id)

                for p in table.players:
                    if p.player_id != player_id:
                        await send(p.player_id, json.dumps(game_state(table, p)))

                return self.success(Event.LEAVE, game_state(table, player))

        return self.error("Player not found in any table.")

    async def __handle_ready(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player staring the game"""
        table_id:str|None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")

        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")

        player = next((p for p in table.players if p.player_id == player_id), None)
        if not player:
            return self.error("Player not found in table.")

        player.toggle_ready()
        table.start()
        
        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.READY, game_state(table, player))
        
    async def __handle_fold(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player's fold action"""
        table_id: str | None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")
 
        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")
 
        player = next((p for p in table.players if p.player_id == player_id), None)
        if not player:
            return self.error("Player not found in table.")
 
        if not table.current_player or table.current_player.player_id != player_id:
            return self.error("It is not your turn.")
 
        response = table.fold(player_id)
        if response:
            return self.error(response)
 
        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.FOLD, game_state(table, player))

    async def __handle_check(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player's fold action"""
        table_id: str | None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")
 
        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")
 
        player = next((p for p in table.players if p.player_id == player_id), None)
        if not player:
            return self.error("Player not found in table.")
 
        if not table.current_player or table.current_player.player_id != player_id:
            return self.error("It is not your turn.")
 
        response = table.check(player_id)
        if response:
            return self.error(response)
 
        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.FOLD, game_state(table, player))

    async def __handle_call(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player's fold action"""
        table_id: str | None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")
 
        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")
 
        player = next((p for p in table.players if p.player_id == player_id), None)
        if not player:
            return self.error("Player not found in table.")
 
        if not table.current_player or table.current_player.player_id != player_id:
            return self.error("It is not your turn.")
 
        response = table.call(player_id)
        if response:
            return self.error(response)
 
        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.FOLD, game_state(table, player))

    async def __handle_bet(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player's fold action"""
        table_id: str | None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")

        amount: int | None = payload.get("amount")
        if not amount:
            return self.error("property `amount` must be specified in payload.")

        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")
 
        player = next((p for p in table.players if p.player_id == player_id), None)
        if not player:
            return self.error("Player not found in table.")
 
        if not table.current_player or table.current_player.player_id != player_id:
            return self.error("It is not your turn.")
 
        response = table.bet(player_id, amount)
        if response:
            return self.error(response)

        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.FOLD, game_state(table, player))
                
    async def __handle_raise(self, player_id: str, player_name: str, payload: dict, send: SendFn) -> str:
        """Handle a player's fold action"""
        table_id: str | None = payload.get("tableId")
        if not table_id:
            return self.error("property `tableId` must be specified in payload.")

        amount: int | None = payload.get("amount")
        if not amount:
            return self.error("property `amount` must be specified in payload.")

        table = table_manager.get(table_id)
        if not table:
            return self.error(f"Table `{table_id}` not found.")
 
        player = next((p for p in table.players if p.player_id == player_id), None)
        if not player:
            return self.error("Player not found in table.")
 
        if not table.current_player or table.current_player.player_id != player_id:
            return self.error("It is not your turn.")
 
        response = table.raise_bet(player_id, amount)
        if response:
            return self.error(response)
 
        for p in table.players:
            if p.player_id != player_id:
                await send(p.player_id, json.dumps(game_state(table, p)))

        return self.success(Event.FOLD, game_state(table, player))

    def is_connect_action(self, raw: str) -> bool:
        """
        Return whether the raw message is a ``connect`` action.

        :param raw: The raw JSON string.
        :rtype: bool
        """
        try:
            return json.loads(raw).get("action") == "connect"
        except json.JSONDecodeError:
            return False

    def extract_player_name(self, raw: str) -> str | None:
        """
        Extract the player_name from a ``connect`` message payload.

        :param raw: The raw JSON string.
        :return: The player_name string, or ``None`` if absent or malformed.
        :rtype: str | None
        """
        try:
            return json.loads(raw).get("payload", {}).get("playerName")
        except json.JSONDecodeError:
            return None

    def disconnect(self, player_id: str) -> str:
        """Handle a player creating a table"""
        for table in table_manager.tables.values():
            if table.del_player(player_id):
                if not table.players:
                    table_manager.remove(table.table_id)
                break

        return self.success(Event.DISCONNECT)

