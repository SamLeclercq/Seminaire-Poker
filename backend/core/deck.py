import random

from card import Card, Rank, Suit

class Deck:
    def __init__(self) -> None:
        self.__cards = []
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

    def shuffle(self) -> None:
        """
        Randomly shuffle the deck.

        :raise ValueError: If the deck is empty.
        """
        if not self.__cards:
            raise ValueError("Cannot shuffle an empty deck")
        random.shuffle(self.__cards)

    def draw(self) -> Card:
        """
        Remove and return the top card from the deck.

        :return: the top :class:`Card`.
        :rtype: Card
        :raise ValueError: If the deck is empty.
        """
        if not self.__cards:
            raise ValueError("cannot draw from an empty deck")
        return self.__cards.pop()
