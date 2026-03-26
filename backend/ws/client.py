from websockets.sync.client import connect, ClientConnection
import json
import threading
import queue


response_queue: queue.Queue = queue.Queue()


def send(websocket: ClientConnection, message: dict) -> dict:
    """
    Send a message and block until the listener puts a response in the queue.

    :param websocket: The active WebSocket connection.
    :param message: The message dict to send.
    :return: The parsed JSON response.
    """
    websocket.send(json.dumps(message))
    return response_queue.get()


def listen(websocket: ClientConnection) -> None:
    """
    Continuously listen for all incoming messages.
    Direct responses are put in the queue; server pushes are printed.

    :param websocket: The active WebSocket connection.
    """
    try:
        for raw in websocket:
            message = json.loads(raw)
            if message.get("status") in ("success", "error"):
                response_queue.put(message)
            else:
                print(f"[server push] {message}")
    except Exception:
        print("Connection closed.")


with connect("ws://localhost:8765") as websocket:
    thread = threading.Thread(target=listen, args=(websocket,), daemon=True)
    thread.start()

    response = send(websocket, {
        "action": "connect",
        "payload": {"playerName": "feur"}
    })
    print(f"[connect] {response}")

    response = send(websocket, {
        "action": "create",
        "payload": {}
    })
    print(f"[create] {response}")

    response = send(websocket, {
        "action": "leave",
        "payload": {}
    })
    print(f"[create] {response}")

    thread.join()
    while True: pass
