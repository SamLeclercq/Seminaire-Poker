import random
import string

import constants
from core.table import Table

tables: dict[str, Table] = {}

def generate_id() -> str:
    """
    Generate an id for the table.
    id is an string of 5 uppercase alphanumeric characters.
    """
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=constants.ROOM_ID_LENGTH))



def add_table(tables: dict[str, Table]) -> Table|None:
    """
    Create a new table and generate an unique table id.

    :param tables: Universal list of existing tables.
    :return: The new table.
    :rtype: Table
    """
    while True:
        table_id = generate_id()
        if table_id not in tables.keys():
            break

    tables[table_id] = Table(table_id)
    return tables.get(table_id)

