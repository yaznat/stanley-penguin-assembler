# stanley-penguin-assembler

An assembler and interpreter for the Stanley/Penguin machine defined in [CMSI 2210](https://bjohnson.lmu.build/cmsi2210web) at **Loyola Marymount University** and written in **Java**.

## Installation

### Prerequisites

- Java 21 or later
- [Apache Maven](https://maven.apache.org/) 3.8+

### Install

Clone the repository and run the installation script for your operating system.

**macOS / Linux**

```bash
git clone https://github.com/yaznat/stanley-penguin-assembler.git
cd stanley-penguin-assembler

chmod +x install.sh
./install.sh
```

By default, Stanley/Penguin is installed to:

- `~/.local/lib/stanley/` — Application JAR
- `~/.local/bin/stanley` — Command-line launcher

Ensure `~/.local/bin` is included in your `PATH`. On macOS (Zsh), add the following to `~/.zshrc` if necessary:

```bash
export PATH="$HOME/.local/bin:$PATH"
```

Reload your shell configuration:

```bash
source ~/.zshrc
```

**Windows (PowerShell)**

```powershell
git clone https://github.com/yaznat/stanley-penguin-assembler.git
cd stanley-penguin-assembler

.\install.ps1
```

By default, Stanley/Penguin is installed to:

- `%LOCALAPPDATA%\Stanley\` — Application JAR
- `%LOCALAPPDATA%\Programs\Stanley\` — Command-line launcher

Ensure `%LOCALAPPDATA%\Programs\Stanley` is included in your `PATH`. To add it permanently:

1. Open Windows Settings and search for **Environment Variables**.
2. Select **Edit environment variables for your account**.
3. Under **User variables**, select `Path` and click **Edit**.
4. Click **New** and add:

   ```text
   %LOCALAPPDATA%\Programs\Stanley
   ```

5. Save the changes and restart your terminal.

If PowerShell prevents the installation script from running due to its execution policy, you can run:

```powershell
powershell -ExecutionPolicy Bypass -File .\install.ps1
```

### Verify Installation

Open a new terminal and run:

```bash
stanley --help
```

## Getting Started

### Assembler

To assemble a penguin source file:

```bash
stanley nest program.peng
```

Specify an output path:

```bash
stanley nest program.peng -o bin/output.egg
```

### Interpreter

To execute an egg binary:

```bash
stanley hatch program.egg
```

### Docs

See the [docs](docs/START_HERE.md) to learn more.
