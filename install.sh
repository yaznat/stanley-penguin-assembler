
#!/bin/sh

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

INSTALL_DIR="$HOME/.local/lib/stanley"
BIN_DIR="$HOME/.local/bin"

echo "Building Stanley..."

cd "$PROJECT_DIR"
mvn clean package

echo "Installing Stanley..."

mkdir -p "$INSTALL_DIR"
mkdir -p "$BIN_DIR"

cp target/stanley-1.0.0.jar "$INSTALL_DIR/stanley.jar"

cat > "$BIN_DIR/stanley" <<'EOF'
#!/bin/sh
STANLEY_HOME="$HOME/.local/lib/stanley"
exec java -jar "$STANLEY_HOME/stanley.jar" "$@"
EOF

chmod +x "$BIN_DIR/stanley"

echo "Installation complete!"
echo "Run: stanley --help"
