import core.constants as constants
from core.card import Card
from core.deck import Deck
from core.action import Action

class Player:
    """
    Initialize a player with a name, starting balance, and no cards.

    :param id: Unique identifier for the player.
    :param name: Display name of the player.
    """
    def __init__(self, player_id: str, name: str) -> None:
        self.__player_id = player_id
        self.__name = name
        self.__balance = constants.STARTING_CHIPS
        self.__current_bet = 0
        self.__total_bet = 0
        self.__last_action = Action.NONE
        self.__pocket: list[Card] = []
        self.__is_ready = False
        self.__is_connected = True
        self.__is_active = True
        self.__is_dealer = False
        self.__is_small_blind = False
        self.__is_big_blind = False
        self.__is_folded = False
        self.__is_all_in = False


    @property
    def player_id(self) -> str:
        return self.__player_id

    @property
    def name(self) -> str:
        return self.__name

    @property
    def balance(self) -> int:
        return self.__balance

    @property
    def current_bet(self) -> int:
        return self.__current_bet

    @property
    def last_action(self) -> str:
        return self.__last_action.value

    @last_action.setter
    def last_action(self, action: Action) -> None:
        self.__last_action = action

    @property
    def pocket(self) -> list[Card]:
        return list(self.__pocket)

    @property
    def total_bet(self) -> int:
        return self.__total_bet

    @property
    def is_ready(self) -> bool:
        return self.__is_ready

    @property
    def is_connected(self) -> bool:
        return self.__is_connected

    def set_connected(self, value: bool) -> None:
        self.__is_connected = value

    @property
    def is_active(self) -> bool:
        return self.__is_active
    
    @property
    def is_folded(self) -> bool:
        return self.__is_folded

    @property
    def is_all_in(self) -> bool:
        return self.__is_all_in

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

    def toggle_ready(self) -> None:
        self.__is_ready = not self.__is_ready

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
        self.__is_all_in = self.__balance <= 0

    def fold(self) -> None:
        """Mark the player as folded."""
        self.__is_folded = True

    def bet(self, amount: int) -> int:
        """
        Deduct chips from the player's balance as a bet.

        :param amount: Number of chips to bet. Must be positive and no greater than the player's current balance.
        :return: Real amount bet.
        :rtype: int
        :raises ValueError: If amount is not positive.
        """
        if amount <= 0:
            raise ValueError("Amount to bet must be positive.")
        if amount > self.__balance:
            amount = self.__balance
        self.__current_bet += amount
        self.__total_bet += amount
        self.__balance -= amount
        self.__is_all_in = self.__balance <= 0
        return amount

    def __update_active_status(self):
        """
        Mark the player as inactive if their balance is zero, active otherwise.
        Should be called at the end of each hand.
        """
        self.__is_active = self.__balance > 0

    def reset_turn(self) -> None:
        self.__last_action = Action.NONE
        self.__current_bet = 0

    def reset(self) -> None:
        """
        Reset positions of the player for the current hand.
        """
        self.__is_dealer = False
        self.__is_small_blind = False
        self.__is_big_blind = False
        self.__current_bet = 0
        self.__total_bet = 0
        self.__last_action = Action.NONE
        self.__total_bet = 0
        # self.__is_ready = False
        self.__update_active_status()
        self.__is_folded = False
        self.__is_all_in = False

