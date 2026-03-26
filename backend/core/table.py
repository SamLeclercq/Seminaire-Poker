from core import state
from core.action import Action
import core.constants as constants
from core.card import Card
from core.card_types import Rank, Suit
from core.deck import Deck
from core.player import Player
from core.state import State
from core.pot import calculate_pots, distribute_pots
from core.score import calculate_score

class Table:
    """Initialize a poker table."""
    def __init__(self, table_id: str) -> None:
        self.__table_id = table_id
        self.__players: list[Player] = []
        self.__current_hand = 0
        self.__current_state = State.WAITING
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
    def current_state(self) -> str:
        return self.__current_state.value

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
            if self.__current_state == State.WAITING:
                self.__players.remove(player)
                return True

            if player.player_id == player_id:
                player.set_connected(False)
                return True

        return False

    def start(self) -> None:
        """Switch to PREFLOP if every player is ready"""
        if self.__current_state != State.WAITING or len(self.__players) < 2:
            return

        is_all_players_ready = True
        for player in self.__players:
            if not player.is_ready:
                is_all_players_ready = False

        if is_all_players_ready:
            self.__current_state = State.PREFLOP
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
        if self.__current_state == State.SHOWDOWN:
            # return [Action.SHOW.value, Action.MUCK.value]
            return []
    
        # --- Preflop ---
        if self.__current_state == State.PREFLOP:
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

    def fold(self, player_id: str) -> None:
        ...

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