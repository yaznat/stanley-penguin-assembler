# Stanley/Penguin File Formats

Information about `.peng` and `.egg` file formats.

## `.peng` Files

Stanley/Penguin source files use the extension `.peng` and are interpreted as raw text.

## `.egg` File Extension

Assembled binaries use the extension `.egg`, with the following format:

| Component    | Size     | Description                                                                                |
| :----------- | :------- | :----------------------------------------------------------------------------------------- |
| Magic number | 4 bytes  | A unique identifer to verify the file is a Stanley/Penguin binary: `0x45474721` ("EGG!").  |
| Entry point  | 4 bytes  | The memory address where the simulator should begin execution (relative to payload start). |
| Word count   | 4 bytes  | The total number of 32-bit words in the binary.                                            |
| Payload      | Variable | The actual sequence of 32-bit words in Big Endian format.                                  |

Thus, the file header is **12 bytes**.
