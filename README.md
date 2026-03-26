# Seminaire-Poker

Jeu de poker multiplayer en temps reel avec backend WebSocket Python et frontend JavaFX.

## Prerequis

- Python 3.10+
- Java 21+

## Installation

```bash
python3 -m pip install -r backend/requirements.txt
```

## Lancement

Backend seul :

```bash
scripts/dev.sh backend
```

Frontend seul :

```bash
scripts/dev.sh frontend
```

Backend + Frontend :

```bash
scripts/dev.sh all
```

Le frontend peut etre lance sans backend pour avancer sur le visuel.

## Configuration backend (optionnel)

- `POKER_WS_HOST` (defaut `127.0.0.1`)
- `POKER_WS_PORT` (defaut `8765`)

Exemple :

```bash
POKER_WS_PORT=9000 scripts/dev.sh backend
```

## License

[MIT](LICENSE)
