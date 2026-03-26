from enum import Enum

class Action(Enum):
    NONE = ""
    CHECK = "check"
    BET = "bet"
    CALL = "call"
    RAISE = "raise"
    FOLD = "fold"
    # MUCK = "muck"
    # SHOW = "show"

