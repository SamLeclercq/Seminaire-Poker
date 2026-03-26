import core.constants as constants
from core.card import Card
from core.card_types import Rank, Suit
from core.deck import Deck
from core.player import Player
from core.state import State

class Table:
    """Initialize a poker table."""
    def __init__(self, table_id: str) -> None:
        self.__table_id = table_id
        self.__players: list[Player] = []
        self.__current_hand = 0
        self.__current_state = State.WAITING
        self.__current_player: Player|None = None
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
            if player.player_id == player_id:
                self.__players.remove(player)
                return True

        return False

    def assign_positions(self) -> None:
        """
        Assign dealer, small blind, and big blind positions for the current hand.
        """
        for player in self.__players:
            player.reset_positions()

        active = [player for player in self.__players if player.is_active]

        active[self.__current_hand % len(self.__players)].is_dealer=True
        active[(self.__current_hand+1) % len(self.__players)].is_small_blind=True
        active[(self.__current_hand+2) % len(self.__players)].is_big_blind=True

    def deal_cards(self) -> None:
        """
        Deal two pocket cards to each active player.

        :raises IndexError: If the deck runs out of cards mid-deal.
        """
        for _ in range(2):
            for player in self.__players:
                if player.is_active:
                    player.draw(self.__deck)

