from enum import Enum

class Suit(Enum):
    CLUBS    = "clubs"
    DIAMONDS = "diamonds"
    HEARTS   = "hearts"
    SPADES   = "spades"


class Rank(Enum):
    TWO   = 2
    THREE = 3
    FOUR  = 4
    FIVE  = 5
    SIX   = 6
    SEVEN = 7 # SIX SEVEEENNN
    EIGHT = 8
    NINE  = 9
    TEN   = 10
    JACK  = 11
    QUEEN = 12
    KING  = 13
    ACE   = 14


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

