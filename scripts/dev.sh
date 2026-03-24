#!/usr/bin/env zsh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend/server"
FRONTEND_DIR="$ROOT_DIR/frontend"

backend_cmd() {
  if [[ -x "$BACKEND_DIR/.venv/bin/python" ]]; then
    "$BACKEND_DIR/.venv/bin/python" -m backend.server.ws.server
  else
    python3 -m backend.server.ws.server
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

