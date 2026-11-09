# The Stanley/Penguin Machine

Information about the Stanley/Penguin processor.

## Origin

The Stanley/Penguin is an educational CPU engine theorized in 2021 in the course [CMSI 2210](https://bjohnson.lmu.build/cmsi2210web) at **Loyola Marymount University**.

The machine is designed to acquaint students with the workings of modern processors at an extremely simplified level. Even so, it possesses great capability.

## Components

- The **Accumulator** (`A`): a single 32-bit register that stores values.
- The **Instruction Pointer** (`IP`): another 32-bit register which holds the address of the next instruction to execute. (See **Protected Addresses** in [MEMORY.md](./MEMORY.md) for a slight amendment to this architecture).
- **256 MiB of Memory** (`M`): composed of 64 million words, each 32 bits wide.
- **256 Ports** (`P`): to which we can read and write information. Ports are mapped to default 8-bit IDs but can be customized upon startup. (Read more about this in [PORTS.md](./PORTS.md)).

## Architecture

The machine follows the **Von Neumann** architecture (Fetch, Decode, Execute). Since this implementation is a virtual interpreter, steps are implicitly synchronized—there's no concern for a clock.

The Stanley/Penguin is **Big Endian**.

## Links

- [INSTRUCTIONS.md](./INSTRUCTIONS.md) — read about how instructions are formatted and see a list of all instructions
- [MEMORY.md](./MEMORY.md) — read about how memory is accessed and partioned
- [PORTS.md](./PORTS.md) — see a table of ports and their functionalities
- [SYNTAX.md](./SYNTAX.md) - see a comprehensive syntax guide
- [FILE_FORMATS.MD](./FILE_FORMATS.md) - read about the formats of `.peng` and `.egg` files
