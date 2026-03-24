import constants
from card import Card
from deck import Deck

class Player:
    def __init__(self, name: str) -> None:
        self.__name = name
        self.__chips = constants.CHIPS
        self.__hand: list[Card] = []
        self.__is_active = True
        self.__is_dealer = False
        self.__is_small_blind = False
        self.__is_big_blind = False

    @property
    def name(self) -> str:
        return self.__name

    @property
    def chips(self) -> int:
        return self.__chips

    @property
    def hand(self) -> list[Card]:
        return self.__hand

    @property
    def is_active(self) -> bool:
        return self.__is_active

    @property
    def is_dealer(self) -> bool:
        return self.__is_dealer

    @property
    def is_small_blind(self) -> bool:
        return self.__is_small_blind

    @property
    def is_big_blind(self) -> bool:
        return self.__is_big_blind


    def draw(self, deck: Deck) -> None:
        self.__hand.append(deck.draw())

    def add_chips(self, number: int) -> None:
        self.__chips += number

    def bet(self, number: int) -> None:
        if number > self.__chips:
            raise ValueError("t'as pas assez mon fraté")

        self.__chips -= number

    def check_balance(self):
        if self.chips <= 0:
            self.__is_active = False







