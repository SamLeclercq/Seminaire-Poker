from websockets.sync.client import connect
import json

def send(message: int) -> None:
    with connect("ws://localhost:8765") as websocket:
        websocket.send(message)
        response = websocket.recv()
        print(response)

send(json.dumps({
    
    }))
