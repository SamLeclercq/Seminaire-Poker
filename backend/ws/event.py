from enum import Enum

class Event(Enum):
    CREATE = "create"
    JOIN = "join"
    LEAVE = "leave"
    START = "start"
    BET = "bet"
    CHECK = "check"
    FOLD = "fold"

