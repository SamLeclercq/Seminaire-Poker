import random

from core.values.card_types import Rank, Suit
from core.entities.card import Card

class Deck:
    """
    Initialize a poker card deck.
    """
    def __init__(self) -> None:
        self.__cards: list[Card] = []
        self.populate()

    def populate(self) -> None:
        """
        Fill the deck with all 52 cards (13 ranks x 4 suits).

        Any card currently in the deck are discarded. Cards are added in suit-major order.
        """
        self.__cards = []

        for suit in Suit:
            for rank in Rank:
                self.__cards.append(Card(suit, rank))

        self.shuffle()

    def shuffle(self) -> None:
        """
        Randomly shuffle the deck.

        :raise ValueError: If the deck is empty.
        """
        if not self.__cards:
            return
            
        random.shuffle(self.__cards)

    def draw(self) -> Card:
        """
        Remove and return the top card from the deck.

        :return: the top :class:`Card`.
        :rtype: Card
        :raise ValueError: If the deck is empty.
        """
        if not self.__cards:
            return
        return self.__cards.pop()

