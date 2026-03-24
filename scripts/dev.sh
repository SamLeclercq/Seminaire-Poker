#!/usr/bin/env zsh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

kill_processes_on_port() {
  local port="$1"
  local pids=""

  if ! command -v lsof >/dev/null 2>&1; then
    return 0
  fi

  pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null | tr '\n' ' ' | sed 's/[[:space:]]*$//')"
  if [[ -z "$pids" ]]; then
    return 0
  fi

  echo "Port $port occupe, arret des process: $pids"
  kill $pids 2>/dev/null || true
  sleep 1

  if lsof -nP -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1; then
    echo "Forcage de l'arret des process sur le port $port"
    kill -9 $pids 2>/dev/null || true
    sleep 1
  fi
}

backend_cmd() {
  local port="${POKER_WS_PORT:-8765}"

  kill_processes_on_port "$port"

  if [[ -x "$BACKEND_DIR/.venv/bin/python" ]]; then
    "$BACKEND_DIR/.venv/bin/python" -m backend.ws.server
  elif [[ -x "$ROOT_DIR/.venv/bin/python" ]]; then
    "$ROOT_DIR/.venv/bin/python" -m backend.ws.server
  else
    python3 -m backend.ws.server
  fi
}

front_cmd() {
  "$FRONTEND_DIR/gradlew" -p "$FRONTEND_DIR" run
}

usage() {
  echo "Usage: scripts/dev.sh {backend|frontend|all}"
}

case "${1:-}" in
  backend)
    backend_cmd
    ;;
  frontend)
    front_cmd
    ;;
  all)
    backend_cmd &
    BACK_PID=$!
    trap 'kill "$BACK_PID" 2>/dev/null || true' EXIT INT TERM
    front_cmd
    ;;
  *)
    usage
    exit 1
    ;;
esac

