# Backend Python

Serveur WebSocket du projet Seminaire Poker.

## Prerequis

- Python 3.10+

## Installation

Depuis la racine du repo :

```bash
python3 -m venv backend/.venv
source backend/.venv/bin/activate
pip install -r backend/requirements.txt
```

## Lancement

```bash
python3 -m backend.ws.server
```

Variables optionnelles :

- `POKER_WS_HOST` (defaut: `127.0.0.1`)
- `POKER_WS_PORT` (defaut: `8765`)

