import constants
from card import Card
from deck import Deck

class Player:
    """
    Initialise a player with a name, starting balance, and no cards.

    :param id: Unique identifier for the player.
    :param name: Display name of the player.
    """
    def __init__(self, id: int, name: str) -> None:
        self.__id = id
        self.__name = name
        self.__balance = constants.STARTING_CHIPS
        self.__pocket: list[Card] = []
        self.__is_active = True
        self.__is_dealer = False
        self.__is_small_blind = False
        self.__is_big_blind = False

    @property
    def id(self) -> int:
        return self.__id

    @property
    def name(self) -> str:
        return self.__name

    @property
    def balance(self) -> int:
        return self.__balance

    @property
    def pocket(self) -> list[Card]:
        return self.__pocket

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
        """
        Draw the top card from the deck into the player's pocket.

        :param deck: The deck to draw from.
        :raises IndexError: If the deck is empty.
        """
        self.__pocket.append(deck.draw())

    def add_balance(self, amount: int) -> None:
        """
        Add chips the the player's balance.

        :param amount: Number of chips to add. Must be positive.
        :raises ValueError: If amount is not positive.
        """
        if amount <= 0:
            raise ValueError("Amount to add must be positive.")
        self.__balance += amount

    def bet(self, amount: int) -> None:
        """
        Deduct chips from the player's balance as a bet.

        :param amount: Number of chips to bet. Must be positive and no greater than the player's current balance.
        :raises ValueError: If amount is not positive or exceeds the balance.
        """
        if amount <= 0:
            raise ValueError("Amount to bet must be positive.")
        if amount > self.__balance:
            raise ValueError("Cannot bet more than your current balance")

        self.__balance -= amount

    def update_active_status(self):
        """
        Mark the player as inactive if their balance reached zero.
        Should be called at the end of each hand.
        """
        if self.balance <= 0:
            self.__is_active = False

    def set_roles(self, *, dealer: bool = False, small_blind: bool = False, big_blind: bool = False) -> None:
        """
        Assign positional roles to the player for the current hand.

        Roles are keyword-only and independant.

        :param dealer: Wether the player holds the dealer button.
        :param small_blind: Wether the player is the small blind.
        :param big_blind: Wether the player is the big blind.
        """
        self.__is_dealer = dealer
        self.__is_small_blind = small_blind
        self.__is_big_blind = big_blind
