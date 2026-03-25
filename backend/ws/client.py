from websockets.sync.client import connect
import json

def send(message: str) -> str:
    with connect("ws://localhost:8765") as websocket:
        websocket.send(message)
        response = websocket.recv()
        print(response)
        
        return response

player_id = json.loads(send(json.dumps({
    "action": "connect",
    "payload": {
        "playerName": "feur"
        }
    })))["player_id"]

send(json.dumps({
    "action": "create",
    "player_id": player_id,
    "payload": {
        }
    }))
