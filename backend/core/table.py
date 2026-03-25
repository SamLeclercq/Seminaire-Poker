import core.constants as constants
from core.card import Card
from core.card_types import Rank, Suit
from core.deck import Deck
from core.player import Player
from core.state import State

class Table:
    """
    Initialize a poker table.
    """
    def __init__(self, table_id: str, player_id: str) -> None:
        self.__table_id = table_id
        self.__players: list[Player] = [Player(player_id)]
        self.__current_hand = 1
        self.__current_state = State.PREFLOP
        self.__deck = Deck()
        self.__community_cards: list[Card] = []

    @property
    def table_id(self) -> str:
        return self.__table_id

    @property
    def deck(self) -> Deck:
        return self.__deck

    def add_player(self, player: Player) -> None:
        """
        Seat a player at the table.

        :param player: The player to add.
        :raises OverflowError: If the table already has :data:`constants.MAX_PLAYER_PER_ROOM` players.
        """
        if len(self.__players) >= constants.MAX_PLAYER_PER_ROOM:
            raise OverflowError("Table is full")
        self.__players.append(player)

    def del_player(self, id: int) -> None:
        """
        Remove a plyer from the table  by their ID.
        
        :param id: the unique identifier of the player to remove.
        :raises KeyError: If no player with the given ID is found.
        """
        for player in self.__players:
            if player.id == id:
                self.__players.remove(player)
                return

        raise KeyError("No player found with this ID.")

    def assign_positions(self) -> None:
        """
        Assign dealer, small blind, and big blind positions for the current hand.
        """
        for player in self.__players:
            player.reset_positions()

        active = [player for player in self.__players if player.is_active]

        active[(self.__current_hand-1) % len(self.__players)].is_dealer=True
        active[self.__current_hand % len(self.__players)].is_small_blind=True
        active[(self.__current_hand+1) % len(self.__players)].is_big_blind=True

    def deal_cards(self) -> None:
        """
        Deal two pocket cards to each active player.

        :raises IndexError: If the deck runs out of cards mid-deal.
        """
        for _ in range(2):
            for player in self.__players:
                if player.is_active:
                    player.draw(self.__deck)



#
# p1 = Player(1, 'Margaux')
# p2 = Player(2, 'Michel')
# p3 = Player(3, 'Ferdinand')
# p4 = Player(4, 'Jean Claude')
#
# table = Table("DG2TG")
#
# table.add_player(p1)
# table.add_player(p2)
# table.add_player(p3)
# table.add_player(p4)
#
# table.assign_positions()
# table.deal_cards()
#
# print(p1.positions)
# print(p2.positions)
# print(p3.positions)
# print(p4.positions)
#
