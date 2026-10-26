# stanley-penguin-assembler

An assembler and simulator for the Stanley/Penguin machine defined in [CMSI 2210](https://bjohnson.lmu.build/cmsi2210web) at **Loyola Marymount University** and written in **Java**.

## Stanley/Penguin Specifications

The Stanley/Penguin is a simplified educational machine with **one accumulator** (`A`), **one instruction pointer** (`IP`), and **256 MiB words of memory** (`M`). `A` and `IP` each store a 32-bit word, while `M` is also split into 32-bit words.

In addition, the machine can have up to **255** ports. While not specified by the course, ports are determined as follows for the purposes of this simulation:

| Port # | Purpose         |
| :----- | :-------------- |
| 0x00   | terminal output |

_more ports coming soon_

The Stanley/Penguin follows the **Von Neumann** architecture and uses the **Big Endian** system.

## Stanley/Penguin Instructions

The Stanley/Penguin understands a total of **16 instructions**. Instructions are 32-bit words, with the first **four** bits reserved for the opcode and the remaining **28** bits available for values.

The following is a list of all instructions:

| opcode | Action                | Mnemonic | Meaning                                            |
| :----- | :-------------------- | :------- | :------------------------------------------------- |
| 0      | A := M[x]             | LOAD     | Load the Accumulator from memory address [x]       |
| 1      | M[x] := A             | STORE    | Store accumulator to memory                        |
| 2      | A := P[x]             | READ     | Read from a port into the accumulator              |
| 3      | P[x] := A             | WRITE    | Write accumulator out to a port                    |
| 4      | A := A + M[x]         | ADD      | Add into accumulator                               |
| 5      | A := A - M[x]         | SUB      | Subtract from accumulator                          |
| 6      | A := A $\times$ M[x]  | MUL      | Multiply into accumulator                          |
| 7      | A := A $\div$ M[x]    | DIV      | Divide accumulator                                 |
| 8      | A := A mod M[x]       | MOD      | Modulo                                             |
| 9      | A := A & M[x]         | AND      | Bitwise AND                                        |
| A      | A := A \| M[x]        | OR       | Bitwise OR                                         |
| B      | A := A ^ M[x]         | XOR      | Bitwise XOR                                        |
| C      | IP := x               | JMP      | Set IP to jump to new address for next instruction |
| D      | if A = 0 then IP := x | JZ       | Jump if accumulator is zero                        |
| E      | if A < 0 then IP := x | JLZ      | Jump if accumulator is less than zero              |
| F      | if A > 0 then IP := x | JGZ      | Jump if accumulator is greater than zero           |

## Program Example

The following example is from the CMSI 2210 [course website](https://bjohnson.lmu.build/cmsi2210web/week06.html):

```asm
   C0000003           JMP     start    ; begin by jumping over the data area
   00000001   pow:    1                ; store the current power value here
   000F4240   limit:  1000000          ; we'll be computing powers up to this amount
   00000001   start:  LOAD    [pow]    ; load value stored at location 'pow' into acc
   30000064           WRITE   100      ; output the current power to port 100
   40000001           ADD     [pow]    ; add value stored a location 'pow' to value in acc
   10000001           STORE   pow      ; store it back to location 'pow' (for next time)
   50000002           SUB     [limit]  ; compare with value stored at location 'limit'
   E0000003           JLZ     start    ; if result is less than zero, go again
   C0000009   end:    JMP     end      ; this "stops" the program!
```

Note that the machine language column is for **illustrative purposes only** and is not a component of the program.

## Assembly Syntax

As seen in the example:

- We use square brackets to denote dereferencing.
- We use subtraction to perform comparisons (employing `JZ`, `JLZ`, or `JGZ` for `==`, `<`, and `>`, respectively).
- Given the lack of a `HALT` instruction, we reserve `end: JMP end` to terminate the program.

## `.peng` File Extension

For the purposes of this simulation, Stanley/Penguin source files use the extension `.peng`.

## `.egg` File Extension

For the purposes of this simulation, compiled binaries use the extension `.egg`, with the following format:

| Component    | Size     | Description                                                                               |
| :----------- | :------- | :---------------------------------------------------------------------------------------- |
| Magic number | 4 bytes  | A unique identifer to verify the file is a Stanley/Penguin binary: `0x45474721` ("EGG!"). |
| Entry point  | 4 bytes  | The memory address where the simulator should begin execution.                            |
| Word count   | 4 bytes  | The total number of 32-bit words in the binary.                                           |
| Payload      | Variable | The actual sequence of 32-bit words in Big Endian format.                                 |

Thus, the file header is **12 bytes**.

## Java Project

This project offers two distinct features: **compilation** and **execution**. The first compiles a source `.peng` file into a `.egg` binary, while the second executes a `.egg` binary via the simulated Stanley/Penguin machine.

### Compilation

TODO: instructions

### Execution

TODO: instructions
