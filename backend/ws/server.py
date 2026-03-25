import asyncio
import os
from parser import Parser
from websockets.asyncio.server import serve

DEFAULT_HOST = "localhost"
DEFAULT_PORT = 8765


async def parse(websocket, parser: Parser):
    async for message in websocket:
        print(message)
        
        response = parser.parser(message)
        websocket.send(response)


async def main() -> None:
    host = os.getenv("POKER_WS_HOST", DEFAULT_HOST)
    port = int(os.getenv("POKER_WS_PORT", str(DEFAULT_PORT)))
    parser = Parser
    async with serve(parse, host, port) as server:
        print(f"WebSocket server listening on ws://{host}:{port}")
        await server.serve_forever()


if __name__ == "__main__":
    asyncio.run(main())

