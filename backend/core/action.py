from enum import Enum

class Action(Enum):
    NONE = ""
    CHECK = "check"
    BET = "bet"
    CALL = "call"
    RAISE = "raise"
    FOLD = "fold"
    ALL_IN = "all_in"

