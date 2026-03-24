# Backend Python

Serveur WebSocket du projet Seminaire Poker.

## Prerequis

- Python 3.10+

## Installation

Depuis la racine du repo :

```bash
python3 -m venv backend/server/.venv
source backend/server/.venv/bin/activate
pip install -r backend/server/requirements.txt
```

## Lancement

```bash
python3 -m backend.server.ws.server
```

Variables optionnelles :

- `POKER_WS_HOST` (defaut: `127.0.0.1`)
- `POKER_WS_PORT` (defaut: `8765`)

