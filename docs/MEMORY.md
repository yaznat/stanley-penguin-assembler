# Stanley/Penguin Memory

Information about the Stanley/Penguin's addressable memory.

## Word Count

The Stanley/Penguin allocates a total of **256 MiB** of memory, divided into **4-byte** words. **256** of these words are (conceptually) reserved for ports, and follow a separate addressing scheme.

Thus, the Stanley/Penguin has **63,000,744** addressable words of memory.

## Addressing

- Addresses are always interpreted as **unsigned integers**. Given the **27 bits** reserved for the operand, this allows the machine to theoretically address up to **134,217,728** words.
- The machine loads the program starting at address **0**, so addresses are relative to program start.
- Words outside of the program can still be read and written to, but attempting to **jump** outside of the program results in a **Program Index Error**.
- Attempting to access a word outside of virtual memory results in a **Memory Index Error**.

## Protected Addresses

The machine reserves the last **16** words as important read-only values. Given the limited instruction set, these values enable features that otherwise wouldn't be possible.

Attempting to write to a protected address results in a **Protected Address Error**.

### System Symbols

Each protected address is mapped to a symbol:

| Symbol    | Description                                                                           |
| :-------- | :------------------------------------------------------------------------------------ |
| IP_ADDR   | Stores the current value of the instruction pointer. **Increments before execution.** |
| RAND_ADDR | Stores a random integer. **Updates per tick.**                                        |
| ...       | Future versions will bring more symbols.                                              |
