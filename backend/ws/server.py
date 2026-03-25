import asyncio
import os
from parser import parser
from websockets.asyncio.server import serve

DEFAULT_HOST = "localhost"
DEFAULT_PORT = 8765


async def parse(websocket):
    async for message in websocket:
        print(message)

        response = "pong" if "ping" in message else "blabla"
        await websocket.send(response)


async def main() -> None:
    host = os.getenv("POKER_WS_HOST", DEFAULT_HOST)
    port = int(os.getenv("POKER_WS_PORT", str(DEFAULT_PORT)))
    async with serve(parse, host, port) as server:
        print(f"WebSocket server listening on ws://{host}:{port}")
        await server.serve_forever()


if __name__ == "__main__":
    asyncio.run(main())

