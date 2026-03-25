import constants
from card import Card
from deck import Deck

class Player:
    """
    Initialize a player with a name, starting balance, and no cards.

    :param id: Unique identifier for the player.
    :param name: Display name of the player.
    """
    def __init__(self, player_id: int, name: str) -> None:
        self.__player_id = player_id
        self.__name = name
        self.__balance = constants.STARTING_CHIPS
        self.__pocket: list[Card] = []
        self.__is_active = True
        self.__is_dealer = False
        self.__is_small_blind = False
        self.__is_big_blind = False

    @property
    def id(self) -> int:
        return self.__player_id

    @property
    def name(self) -> str:
        return self.__name

    @property
    def balance(self) -> int:
        return self.__balance

    @property
    def pocket(self) -> list[Card]:
        return list(self.__pocket)

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

    @is_dealer.setter
    def is_dealer(self, value: bool) -> None:
        """Set whether the player holds the dealer button."""
        self.__is_dealer = value

    @is_small_blind.setter
    def is_small_blind(self, value: bool) -> None:
        """Set whether the player is the small blind."""
        self.__is_small_blind = value

    @is_big_blind.setter
    def is_big_blind(self, value: bool) -> None:
        """Set whether the player is the big blind."""
        self.__is_big_blind = value

    @property
    def positions(self) -> str:
        """Current positional roles held by this player, as a human-readable string."""
        return ", ".join(
            label for label, held in [
                ("dealer", self.__is_dealer),
                ("small blind", self.__is_small_blind),
                ("big blind", self.__is_big_blind),
            ] if held
        ) or "no positions"

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

    def reset_positions(self) -> None:
        """
        Reset positions of the player for the current hand.
        """
        self.__is_dealer = False
        self.__is_small_blind = False
        self.__is_big_blind = False

