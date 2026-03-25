import asyncio
import os
import uuid
from ws.event import Event
from ws.parser import Parser
from websockets.asyncio.server import serve

DEFAULT_HOST = "localhost"
DEFAULT_PORT = 8765

connected_clients: dict[str, str | None] = {}

async def handle(websocket: ServerConnection, parser: Parser) -> None:
    """
    Handle incoming messages from a single WebSocket connection.

    Each connection is assigned a UUID on connect. Actions other than
    ``connect`` are blocked until the client has registered a player_name.

    :param websocket: The active WebSocket connection.
    :param parser: The message parser to dispatch actions to.
    """
    connection_id = str(uuid.uuid7())
    connected_clients[connection_id] = None

    try:
        async for raw in websocket:
            if not connected_clients[connection_id]:
                if not parser.is_connect_action(raw):
                    await websocket.send(parser.error("You must connect with a player name before sending actions."))
                    continue
                player_name = parser.extract_player_name(raw)
                if not player_name:
                    await websocket.send(parser.error("Connect action requires a 'playerName' in payload."))
                    continue

                connected_clients[connection_id] = player_name
                print(connected_clients)
                await websocket.send(parser.success(connection_id, Event.CONNECT))
            
            else:
                await websocket.send(parser.parse(raw))

    finally:
        connected_clients.pop(connection_id, None)


async def parse(websocket: ServerConnection) -> None:
    async for message in websocket:
        print(message)

        parser = Parser()
        response = parser.parse(message)
        await websocket.send(response)


async def main() -> None:
    host = os.getenv("POKER_WS_HOST", DEFAULT_HOST)
    port = int(os.getenv("POKER_WS_PORT", str(DEFAULT_PORT)))
    parser = Parser()
    async with serve(
        lambda ws: handle(ws, parser),
        host,
        port
    ) as server:
        print(f"WebSocket server listening on ws://{host}:{port}")
        await server.serve_forever()


if __name__ == "__main__":
    asyncio.run(main())

