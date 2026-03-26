from enum import Enum

class Event(Enum):
    CONNECT = "connect"
    DISCONNECT = "disconnect"
    CREATE = "create"
    JOIN = "join"
    LEAVE = "leave"
    READY = "ready"
    FOLD = "fold"
    CHECK = "check"
    CALL = "call"
    BET = "bet"
    RAISE = "raise"

