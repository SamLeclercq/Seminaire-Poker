import random
import string

import ws.constants as constants
from core.table import Table


def generate_id() -> str:
    """
    Generate an id for the table.
    id is an string of 5 uppercase alphanumeric characters, excluding letters 'I', 'O' and 'Z' for readability.
    """
    return ''.join(
        random.choices(
            "0123456789ABCDEFGHJKLMNPQRSTUVWXY",
            k=constants.ROOM_ID_LENGTH
        )
    )

class TableManager:
    """
    Manages all active poker tables.

    Provides methods to create, retrieve, and remove tables.
    """

    def __init__(self) -> None:
        self.__tables: dict[str, Table] = {}

    @property
    def tables(self) -> dict[str, Table]:
        return dict(self.__tables)

    def get(self, table_id: str) -> Table | None:
        """
        Retrieve a table by its ID.

        :param table_id: The table ID to look up.
        :return: The matching :class:`Table`, or ``None`` if not found.
        :rtype: Table | None
        """
        return self.__tables.get(table_id)

    def create(self) -> Table:
        """
        Create a new table and register it.

        :return: The newly created :class:`Table`.
        :rtype: Table
        """
        while True:
            table_id = generate_id()
            if table_id not in self.__tables.keys():
                break

        table = Table(table_id)
        self.__tables[table_id] = table
        return table

    def remove(self, table_id: str) -> None:
        """
        Remove a table by its ID.

        If no table with the given ID exists, the call is a no-op.

        :param table_id: The ID of the table to remove.
        """
        self.__tables.pop(table_id, None)

    def __len__(self) -> int:
        return len(self.__tables)

    def __contains__(self, table_id: str) -> bool:
        return table_id in self.__tables

table_manager = TableManager()

