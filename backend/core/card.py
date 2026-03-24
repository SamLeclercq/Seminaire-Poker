from card_types import Suit, Rank

class Card:
    def __init__(self, suit: Suit, rank: Rank) -> None:
        self.__suit = suit
        self.__rank = rank

    @property
    def suit(self) -> Suit:
        return self.__suit 

    @property
    def rank(self) -> Rank:
        return self.__rank

    def __repr__(self) -> str:
        return f"{self.__rank.name} of {self.__suit.name}"

