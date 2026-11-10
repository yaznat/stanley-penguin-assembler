$ErrorActionPreference = "Stop"

$PROJECT_DIR = $PSScriptRoot

$INSTALL_DIR = "$env:LOCALAPPDATA\Stanley"
$BIN_DIR = "$env:LOCALAPPDATA\Programs\Stanley"

Write-Host "Building Stanley..."

Set-Location $PROJECT_DIR
mvn clean package

Write-Host "Installing Stanley..."

New-Item -ItemType Directory -Force -Path $INSTALL_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $BIN_DIR | Out-Null

Copy-Item "target/stanley-1.0.1.jar" "$INSTALL_DIR/stanley.jar"

@"
@echo off
java -jar "$INSTALL_DIR\stanley.jar" %*
"@ | Set-Content "$BIN_DIR\stanley.cmd"

Write-Host "Installation complete!"
Write-Host "Add $BIN_DIR to your PATH if necessary."
Write-Host "Run: stanley --help"