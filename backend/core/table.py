from os import error
import random
import string

import constants
from card import Card
from card_types import Rank, Suit
from deck import Deck
from player import Player

class Table:
    """
    Initialize a poker table.
    """
    def __init__(self, id: str) -> None:
        self.__id = id
        self.__players: list[Player] = []
        self.__current_hand = 1
        self.__deck = Deck()
        self.__pot = 0
        self.__side_pot = 0
        self.__community_cards: list[Card] = []
        self.generate_id()

    @property
    def id(self) -> str:
        return self.__id

    @property
    def deck(self) -> Deck:
        return self.__deck

    def generate_id(self) -> None:
        """
        Generate an id for the table.
        id is an string of 5 uppercase alphanumeric characters.
        """
        self.__id = ''.join(random.choices(string.ascii_uppercase + string.digits, k=constants.ROOM_ID_LENGTH))

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

        self.__players[(self.__current_hand-1) % len(self.__players)].is_dealer=True
        self.__players[self.__current_hand % len(self.__players)].is_small_blind=True
        self.__players[(self.__current_hand+1) % len(self.__players)].is_big_blind=True

    def deal_cards(self) -> None:
        """
        Deal two pocket cards to each active player.

        :raises IndexError: If the deck runs out of cards mid-deal.
        """
        for _ in range(2):
            for player in self.__players:
                player.draw(self.__deck)


p1 = Player(1, 'Margaux')
p2 = Player(2, 'Michel')
p3 = Player(3, 'Ferdinand')
p4 = Player(4, 'Jean Claude')

table = Table(1)

table.add_player(p1)
table.add_player(p2)
table.add_player(p3)
table.add_player(p4)

table.assign_positions()
table.deal_cards()

print(p1.positions)
print(p2.positions)
print(p3.positions)
print(p4.positions)
