from enum import Enum

class Event(Enum):
    CONNECT = "connect"
    DISCONNECT = "disconnect"
    CREATE = "create"
    JOIN = "join"
    LEAVE = "leave"
    START = "start"
    BET = "bet"
    CHECK = "check"
    FOLD = "fold"

