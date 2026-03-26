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

  pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)"
  pids="$(echo "$pids" | tr '\n' ' ' | sed 's/[[:space:]]*$//')"
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

resolve_backend_python() {
  if [[ -x "$BACKEND_DIR/.venv/bin/python" ]]; then
    echo "$BACKEND_DIR/.venv/bin/python"
    return 0
  fi

  if [[ -x "$ROOT_DIR/.venv/bin/python" ]]; then
    echo "$ROOT_DIR/.venv/bin/python"
    return 0
  fi

  echo "Aucun venv detecte, creation de $BACKEND_DIR/.venv"
  python3 -m venv "$BACKEND_DIR/.venv"
  echo "$BACKEND_DIR/.venv/bin/python"
}

ensure_backend_dependencies() {
  local py_bin="$1"

  if "$py_bin" -c "import websockets, uuid_extensions" >/dev/null 2>&1; then
    return 0
  fi

  echo "Dependances backend manquantes, installation depuis backend/requirements.txt"
  "$py_bin" -m pip install -r "$BACKEND_DIR/requirements.txt"
}

backend_cmd() {
  local port="${POKER_WS_PORT:-8765}"
  local py_bin=""

  kill_processes_on_port "$port"

  py_bin="$(resolve_backend_python)"
  ensure_backend_dependencies "$py_bin"

  (
    cd "$ROOT_DIR"
    # Ajoute backend au PYTHONPATH pour les imports internes de type `from ws...`.
    PYTHONPATH="$BACKEND_DIR:$ROOT_DIR${PYTHONPATH:+:$PYTHONPATH}" "$py_bin" -m backend.ws.server
  )
}

front_cmd() {
  # Supprime le warning Java "restricted method" lie a JavaFX sur JDK recents.
  JDK_JAVA_OPTIONS="--enable-native-access=javafx.graphics ${JDK_JAVA_OPTIONS:-}" \
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

