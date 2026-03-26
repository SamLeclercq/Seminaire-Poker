from core import phase
from core.action import Action
import core.constants as constants
from core.card import Card
from core.card_types import Rank, Suit
from core.deck import Deck
from core.player import Player
<<<<<<< HEAD
from core.state import State
from core.pot import calculate_pots, distribute_pots
from core.score import calculate_score
=======
from core.phase import Phase
>>>>>>> cdbdfa1 (wip: game flow)

class Table:
    """Initialize a poker table."""
    def __init__(self, table_id: str) -> None:
        self.__table_id = table_id
        self.__players: list[Player] = []
        self.__current_hand = 0
        self.__current_phase = Phase.WAITING
        self.__current_player: Player|None = None
        self.__current_bet = 0
        self.__pot = 0
        self.__deck = Deck()
        self.__community_cards: list[Card] = []

    @property
    def table_id(self) -> str:
        return self.__table_id

    @property
    def players(self) -> list[Player]:
        return list(self.__players)

    @property
    def current_hand(self) -> int:
        return self.__current_hand + 1

    @property
    def current_phase(self) -> str:
        return self.__current_phase.value

    @property
    def current_player(self) -> Player|None:
        return self.__current_player

    @property
    def deck(self) -> Deck:
        return self.__deck

    @property
    def pot(self) -> int:
        return self.__pot

    @property
    def community_cards(self) -> list[Card]:
        return self.__community_cards

    def add_player(self, player: Player) -> None:
        """
        Seat a player at the table.

        :param player: The player to add.
        :raises OverflowError: If the table already has :data:`constants.MAX_PLAYER_PER_ROOM` players.
        """
        if len(self.__players) >= constants.MAX_PLAYER_PER_ROOM:
            raise OverflowError("Table is full")
        self.__players.append(player)

    def del_player(self, player_id: str) -> bool:
        """
        Remove a plyer from the table  by their ID.
        
        :param id: the unique identifier of the player to remove.
        :return: True if player was found and successfully deleted.
        :rtype: bool
        """
        for player in self.__players:
            if self.__current_phase == Phase.WAITING:
                self.__players.remove(player)
                return True

            if player.player_id == player_id:
                player.set_connected(False)
                return True

        return False

    def __get_player(self, player_id: str) -> Player:
        """
        Retrieve a player by their ID.

        :param player_id: The ID of the player to retrieve.
        :return: The matching Player instance.
        :raises ValueError: If no player with the given ID is found.
        """
        for player in self.__players:
            if player.player_id == player_id:
                return player
        raise ValueError(f"Player with id {player_id} not found.")

    def __next_phase(self) -> None:
        match self.__current_phase:
            case Phase.PREFLOP:
                self.__current_phase = Phase.FLOP
                for _ in range(3):
                    self.__community_cards.append(self.__deck.draw())
                return
            case Phase.FLOP:
                self.__current_phase = Phase.TURN
                self.__community_cards.append(self.__deck.draw())
                return
            case Phase.TURN:
                self.__current_phase = Phase.RIVER
                self.__community_cards.append(self.__deck.draw())
                return
            case Phase.RIVER:
                self.__current_phase = Phase.SHOWDOWN
                return

    def __advance_turn(self) -> None:
        """
        Advance the turn to the next eligible player.
        A player is eligible if they are active, not folded, and not all-in.
        If all remaining players have acted and bets are equal, advance the phase.
        If only one player remains active and not folded, they win by default.
        """
        actives = [
            p for p in self.__players
            if p.is_active and not p.is_folded
        ]

        # if len(actives) == 1:
        #     self.__end_hand(actives[0])
        #     return
        
        can_act = [p for p in actives if not p.is_all_in]
        bets_settled = all(p.current_bet == self.__current_bet for p in can_act)
        all_acted = all(p.last_action != Action.NONE for p in can_act)

        if bets_settled and all_acted:
            self.__next_phase()
            return

        if not self.__current_player:
            return
        current_index = self.__players.index(self.__current_player)
        for i in range(1, len(self.__players) + 1):
            candidate = self.__players[(current_index + i) % len(self.__players)]
            if not candidate.is_folded and not candidate.is_all_in and candidate.is_active:
                self.__current_player = candidate
                return

        self.__next_phase()


    def start(self) -> None:
        """Switch to PREFLOP if every player is ready"""
        if self.__current_phase != Phase.WAITING or len(self.__players) < 2:
            return

        is_all_players_ready = True
        for player in self.__players:
            if not player.is_ready:
                is_all_players_ready = False

        if is_all_players_ready:
            self.__current_phase = Phase.PREFLOP
            self.assign_positions()
            self.deal_cards()
            self.blind_bet()
            self.__current_player = self.__players[self.__current_hand + 4 % len(self.__players)]

    def assign_positions(self) -> None:
        """
        Assign dealer, small blind, and big blind positions for the current hand.
        """
        for player in self.__players:
            player.reset()

        active_players = [player for player in self.__players if player.is_active]

        active_players[self.__current_hand % len(self.__players)].is_dealer=True
        active_players[(self.__current_hand+1) % len(self.__players)].is_small_blind=True
        active_players[(self.__current_hand+2) % len(self.__players)].is_big_blind=True

    def deal_cards(self) -> None:
        """
        Deal two pocket cards to each active player.

        :raises IndexError: If the deck runs out of cards mid-deal.
        """
        for _ in range(2):
            for player in self.__players:
                if player.is_active:
                    player.draw(self.__deck)

    def blind_bet(self) -> None:
        for player in self.__players:
            if player.is_small_blind:
                self.__pot += player.bet(constants.SMALL_BLIND)
            if player.is_big_blind:
                self.__pot += player.bet(constants.BIG_BLIND)

        self.__current_bet = constants.BIG_BLIND

    def get_legal_actions(self, player: Player) -> list[str]:
        """
        Return the list of legal actions for a player on their turn.
        Returns an empty list if it is not the player's turn or they cannot act.

        :param player: The player to check.
        :return: List of legal action values.
        :rtype: list[str]
        """
        if (
            player != self.__current_player or
            not player.is_active or
            player.is_all_in or
            player.is_folded
        ):
            return []

        others = [p for p in self.__players if p != player]
        if all(p.is_all_in or p.is_folded for p in others):
            return []

        # --- Showdown ---
        if self.__current_phase == Phase.SHOWDOWN:
            # return [Action.SHOW.value, Action.MUCK.value]
            return []
    
        # --- Preflop ---
        if self.__current_phase == Phase.PREFLOP:
            return self.__get_preflop_actions(player)

        # --- Flop / Turn / River ---
        return self.__get_postflop_actions(player)

    def __get_preflop_actions(self, player: Player) -> list[str]:
        """
        Return legal actions during the preflop phase.

        Preflop action matrix (from spec):
        - Speaking order: Others → Dealer → SB → BB (BB speaks last)
        - Check and Bet are forbidden for all except BB with no raise
        - BB with no raise: can CHECK (free option), cannot RAISE, FOLD is unusual
        - BB facing a raise: loses the option, standard call/raise/fold

        :param player: The player to check.
        :return: List of legal action values.
        """
        if player.is_big_blind and self.__current_bet == player.current_bet:
            actions = [Action.CHECK.value, Action.RAISE.value, Action.FOLD.value]
            return actions

        actions = [Action.CALL.value, Action.FOLD.value]
        if player.balance > (self.__current_bet - player.current_bet):
            actions.append(Action.RAISE.value)
        return actions

    def __get_postflop_actions(self, player: Player) -> list[str]:
        """
        Return legal actions during flop, turn, and river phases.

        Post-flop action matrix (from spec):
        - Speaking order: SB → BB → Others → Dealer
        - No bet open: all roles can CHECK or BET
        - Bet open: all roles can CALL, RAISE or FOLD (no check, no bet)
        - FOLD when CHECK is available is legal but unusual (marked ✓* in spec)

        :param player: The player to check.
        :return: List of legal action values.
        """
        if self.__current_bet == 0:
            return [Action.CHECK.value, Action.FOLD.value, Action.BET.value]
        
        actions = [Action.CALL.value, Action.FOLD.value]
        if player.balance > (self.__current_bet - player.current_bet):
            actions.append(Action.RAISE.value)

        return actions

    def fold(self, player_id: str) -> str|None:
        """
        Process a fold action for the given player.
        Marks the player as folded and advances to the next player.

        :param player_id: The ID of the player folding.
        :raises ValueError: If the player is not found or the action is not legal.
        """
        player = self.__get_player(player_id)
        if Action.FOLD.value not in self.get_legal_actions(player):
            return f"Player {player.name} cannot fold right now."

        player.fold()
        self.__advance_turn()

    def check(self, player_id: str) -> str|None:
        """
        Process a check action for the given player.

        :param player_id: The ID of the player checking.
        :raises ValueError: If the player is not found or the action is not legal.
        """
        player = self.__get_player(player_id)
        if Action.CHECK.value not in self.get_legal_actions(player):
            return "Player {player.name} cannot check right now."

        self.__advance_turn()

    def bet(self, player_id: str, amount: int) -> str|None:
        """
        Process a bet action for the given player.

        :param player_id: The ID of the player betting.
        :param amount: The amount to bet. Must be at least the big blind.
        :raises ValueError: If the player is not found, the action is not legal, or the amount is invalid.
        """
        player = self.__get_player(player_id)
        if Action.BET.value not in self.get_legal_actions(player):
            return f"Player {player.name} cannot bet right now."

        if amount < constants.BIG_BLIND:
            return f"Bet must be at least the big blind ({constants.BIG_BLIND})."

        real_amount = player.bet(amount)
        self.__current_bet = real_amount
        self.__pot += real_amount
        self.__advance_turn()

    def call(self, player_id: str) -> str|None:
        """
        Process a call action for the given player.
        If the player cannot cover the full amount, they are automatically put all-in.

        :param player_id: The ID of the player calling.
        :raises ValueError: If the player is not found or the action is not legal.
        """
        player = self.__get_player(player_id)
        if Action.CALL.value not in self.get_legal_actions(player):
            return f"Player {player.name} cannot call right now."

        amount = min(self.__current_bet - player.current_bet, player.balance)
        player.bet(amount)
        self.__pot += amount
        self.__advance_turn()

    def raise_bet(self, player_id: str, amount: int) -> str|None:
        """
        Process a raise action for the given player.
        Amount is the total new bet, not the increment.
        Must be at least double the current bet.

        :param player_id: The ID of the player raising.
        :param amount: The total bet amount after the raise.
        :raises ValueError: If the player is not found, the action is not legal, or the amount is below minimum.
        """
        player = self.__get_player(player_id)
        if Action.RAISE.value not in self.get_legal_actions(player):
            return f"Player {player.name} cannot raise right now."

        min_raise = self.__current_bet + 1
        if amount < min_raise:
            return f"Raise must be at least {min_raise} (double the current bet)."

        to_pay = amount - player.current_bet
        player.bet(to_pay)
        self.__pot += to_pay
        self.__current_bet = amount
        self.__advance_turn()

<<<<<<< HEAD
    def bet(self, player_id: str) -> int:
        ...

    def distribute_pot(self) -> dict[str, int]:
        """
        Calculate pots and side pots from player contributions, then distribute winnings.
        :return: A dict mapping player_id to amount won.
        :rtype: dict[str, int]
        """
        contributions = {
            player.player_id: player.total_bet
            for player in self.__players
            if player.is_active
        }
        players_scores = {
            player.player_id: (
                calculate_score(player.pocket, self.__community_cards)
                if not player.is_folded else None
            )
            for player in self.__players
            if player.is_active
        }
        pots = calculate_pots(contributions)
        winnings = distribute_pots(pots, players_scores)
        for player in self.__players:
            if player.player_id in winnings:
                player.add_balance(winnings[player.player_id])
        return winnings
=======
>>>>>>> cdbdfa1 (wip: game flow)
