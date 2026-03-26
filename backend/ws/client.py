from websockets.sync.client import connect, ClientConnection
import json
import threading
import queue

response_queue: queue.Queue = queue.Queue()


def send(ws: ClientConnection, action: str, payload: dict = None) -> dict:
    if payload is None:
        payload = {}

    message = {
        "action": action,
        "payload": payload
    }

    ws.send(json.dumps(message))
    return response_queue.get()


def listen(ws: ClientConnection) -> None:
    try:
        for raw in ws:
            message = json.loads(raw)

            if message.get("status") in ("success", "error"):
                response_queue.put(message)
            else:
                print(f"\n[SERVER PUSH] {message}\n> ", end="", flush=True)

    except Exception:
        print("Connection closed.")


def cli_loop(ws: ClientConnection):
    print("Simple Hold'em CLI")
    print("Commands:")
    print(" connect <name>")
    print(" create")
    print(" join <tableId>")
    print(" leave <tableId>")
    print(" ready")
    print(" fold <tableId>")
    print(" check <tableId>")
    print(" call <tableId>")
    print(" bet <tableId> <amount>")
    print(" raise <tableId> <amount>")
    print(" quit")

    while True:
        try:
            raw = input("> ").strip().split()

            if not raw:
                continue

            cmd = raw[0]

            if cmd == "quit":
                break

            elif cmd == "connect":
                name = raw[1]
                res = send(ws, "connect", {"playerName": name})

            elif cmd == "create":
                res = send(ws, "create")

            elif cmd == "join":
                table_id = raw[1]
                res = send(ws, "join", {"tableId": table_id})

            elif cmd == "leave":
                table_id = raw[1]
                res = send(ws, "leave", {"tableId": table_id})

            elif cmd == "ready":
                table_id = raw[1]
                res = send(ws, "ready", {"tableId": table_id})

            elif cmd in ("fold", "check", "call"):
                table_id = raw[1]
                res = send(ws, cmd, {"tableId": table_id})

            elif cmd in ("bet", "raise"):
                table_id = raw[1]
                amount = int(raw[2])
                res = send(ws, cmd, {
                    "tableId": table_id,
                    "amount": amount
                })

            else:
                print("Unknown command")
                continue

            print(f"[RESPONSE] {res}")

        except Exception as e:
            print(f"Error: {e}")


with connect("ws://localhost:8765") as websocket:
    thread = threading.Thread(target=listen, args=(websocket,), daemon=True)
    thread.start()

    cli_loop(websocket)
