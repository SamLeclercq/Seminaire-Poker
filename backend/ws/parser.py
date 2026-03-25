import json

from core.player import Player
from ws.event import Event
from ws.table_manager import table_manager

class Parser:
    """
    Initialize a parser.
    Parses incoming WebSocket JSON messages and dispatches them to the appropriate game handler.
    """
    def __init__(self) -> None:
        self.__handlers = {
                # Event.CONNECT: self.__handle_connect,
                # Event.DISCONNECT: self.__handle_disconnect,
                Event.CREATE: self.__handle_create,
                Event.JOIN: self.__handle_join,
                Event.LEAVE: self.__handle_leave,
                Event.START: self.__handle_start,
                Event.BET: self.__handle_bet,
                Event.CHECK: self.__handle_check,
                Event.FOLD: self.__handle_fold
        }

    def parse(self, raw: str) -> str:
        """
        Parse a raw JSON string and dispatch to the current handler.

        Expected message format::
            {
                "action": "bet",
                "player_id": 1,
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
        if action not in Event:
            return self.error("unknown action: `{action}`")

        player_id = message.get("player_id")
        payload = message.get("payload", {})
        return self.__handlers[action](player_id, payload)


     # ----- Helpers -----
    def success(self, player_id: str, action: Event, data: dict = {}) -> str:
        """
        Send an error message back to the client.

        :param action: The handled action
        :param data: Optional additional data to include in the response
        :return: JSON-formated string.
        """
        return json.dumps({
            "status": "success",
            "player_id": player_id,
            "action": action.name,
            **data
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
    def __handle_create(self, player_id: str, payload: dict) -> str:
        """
        Handle a player creating a table
        """
        player = Player(player_id)
        table = table_manager.create()
        print(table.table_id)
        return self.success(player_id, Event.CREATE)
        

    def __handle_join(self, player_id: str, payload: dict) -> str:
        """
        Handle a player joining the table
        """
        ...

    def __handle_leave(self, player_id: str, payload: dict) -> str:
        """
        Handle a player leaving the table
        """
        ...

    def __handle_start(self, player_id: str, payload: dict) -> str:
        """
        Handle a player staring the game
        """
        ...

    def __handle_bet(self, player_id: str, payload: dict) -> str:
        """
        Handle a player's bet action
        """
        ...

    def __handle_check(self, player_id: str, payload: dict) -> str:
        """
        Handle a player's check action
        """
        ...

    def __handle_fold(self, player_id: str, payload: dict) -> str:
        """
        Handle a player' fold action
        """
        ...

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
