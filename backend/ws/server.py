import asyncio
import os
from uuid_extensions import uuid7str
from ws.event import Event
from ws.handler import Handler
from websockets.asyncio.server import serve, ServerConnection

DEFAULT_HOST = "localhost"
DEFAULT_PORT = 8765

connected_clients: dict[str, str | None] = {}
connections: dict[str, ServerConnection] = {}

async def send(connection_id: str, message: str) -> None:
    """Send a  message to a specific client by connection_id"""
    websocket = connections.get(connection_id)
    print("send")
    if websocket:
        print(message)
        await websocket.send(message)

async def handle(websocket: ServerConnection, handler: Handler) -> None:
    """
    Handle incoming messages from a single WebSocket connection.

    Each connection is assigned a UUID on connect. Actions other than
    ``connect`` are blocked until the client has registered a player_name.

    :param websocket: The active WebSocket connection.
    :param parser: The message parser to dispatch actions to.
    """
    connection_id = uuid7str()
    connected_clients[connection_id] = None
    connections[connection_id] = websocket

    try:
        async for raw in websocket:
            if not connected_clients[connection_id]:
                if not handler.is_connect_action(raw):
                    await websocket.send(handler.error("You must connect with a player name before sending actions."))
                    continue
                player_name = handler.extract_player_name(raw)
                if not player_name:
                    await websocket.send(handler.error("Connect action requires a 'playerName' in payload."))
                    continue

                connected_clients[connection_id] = player_name
                await websocket.send(handler.success(Event.CONNECT))
            
            else:
                await websocket.send(await handler.parse(raw, connection_id, connected_clients[connection_id], send))

    finally:
        print(handler.disconnect(connection_id))
        connected_clients.pop(connection_id, None)
        connections.pop(connection_id, None)
        print(f"{connection_id}: disconnected")

async def main() -> None:
    host = os.getenv("POKER_WS_HOST", DEFAULT_HOST)
    port = int(os.getenv("POKER_WS_PORT", str(DEFAULT_PORT)))
    handler = Handler()
    async with serve(
        lambda ws: handle(ws, handler),
        host,
        port
    ) as server:
        print(f"WebSocket server listening on ws://{host}:{port}")
        await server.serve_forever()


if __name__ == "__main__":
    asyncio.run(main())

