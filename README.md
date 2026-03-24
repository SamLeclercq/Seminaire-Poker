# Seminaire-Poker

Jeu de poker multiplayer en temps reel avec backend WebSocket Python et frontend JavaFX.

## Demarrage rapide

Depuis la racine du repo :

```bash
cd /Users/{user}/Seminaire-Poker
python3 -m pip install -r backend/requirements.txt
```

## Lancer le backend (seul)

```bash
cd /Users/{user}/Seminaire-Poker
scripts/dev.sh backend
```

## Lancer le frontend (seul)

```bash
cd /Users/{user}/Seminaire-Poker
scripts/dev.sh frontend
```

Le frontend peut etre lance sans backend (utile pour avancer sur le visuel).

## Lancer backend + frontend ensemble

```bash
cd /Users/{user}/Seminaire-Poker
scripts/dev.sh all
```

## Config backend optionnelle

- `POKER_WS_HOST` (defaut `127.0.0.1`)
- `POKER_WS_PORT` (defaut `8765`)

Exemple :

```bash
cd /Users/{user}/Seminaire-Poker
POKER_WS_PORT=9000 scripts/dev.sh backend
```

## License

[MIT](LICENSE)
