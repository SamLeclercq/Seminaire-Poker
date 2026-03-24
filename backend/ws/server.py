import asyncio
import os
from websockets.asyncio.server import serve

DEFAULT_HOST = "127.0.0.1"
DEFAULT_PORT = 8765


async def echo(websocket):
    async for message in websocket:
        await websocket.send(message)


async def main() -> None:
    host = os.getenv("POKER_WS_HOST", DEFAULT_HOST)
    port = int(os.getenv("POKER_WS_PORT", str(DEFAULT_PORT)))
    async with serve(echo, host, port) as server:
        print(f"WebSocket server listening on ws://{host}:{port}")
        await server.serve_forever()


if __name__ == "__main__":
    asyncio.run(main())
