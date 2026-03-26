from enum import Enum

class Event(Enum):
    CONNECT = "connect"
    DISCONNECT = "disconnect"
    CREATE = "create"
    JOIN = "join"
    LEAVE = "leave"
    READY = "ready"
    BET = "bet"
    CHECK = "check"
    FOLD = "fold"

