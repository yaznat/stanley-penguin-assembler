# stanley-penguin-assembler

An assembler and simulator for the Stanley/Penguin machine defined in [CMSI 2210](https://bjohnson.lmu.build/cmsi2210web) at **Loyola Marymount University** and written in **Java**.

## Stanley/Penguin Specifications

The Stanley/Penguin is a simplified educational machine with **one accumulator** (`A`), **one instruction pointer** (`IP`), and **256 MiB words of memory** (`M`). `A` and `IP` each store a 32-bit word, while `M` is also split into 32-bit words.

The Stanley/Penguin follows the **Von Neumann** architecture and uses the **Big Endian** system.

### Ports

The machine can have up to **256** ports. Ports and their default IDs are specified below:

| Port # | Mnemonic       | Description                                                    |
| :----- | :------------- | -------------------------------------------------------------- |
| 0x00   | TERMINAL_RAW   | Prints accumulator value as an integer, then flushes the line. |
| 0x01   | TERMINAL_ASCII | Prints accumulator value as ASCII. Does not flush.             |

_more ports coming soon_

**Port Specification:** Ports can be configured to custom IDs upon startup.

## Stanley/Penguin Instructions

The Stanley/Penguin understands a total of **16 instructions**. Instructions are 32-bit words, with the first **four** bits reserved for the opcode, the **fifth** bit reserved as a dereference flag, and the remaining **27** bits available for values.

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

## Example Programs

```asm
; --- Powers of 2 ---
JMP     start            ; begin by jumping over the data area
pow:    1                ; store the current power value here
limit:  1000000          ; we'll be computing powers up to this amount
start:  LOAD    pow      ; load value stored at location 'pow' into acc
        WRITE   100      ; output the current power to port 100
        ADD     pow      ; add value stored at location 'pow' to value in acc
        STORE   pow      ; store it back to location 'pow' (for next time)
        SUB     limit    ; compare with value stored at location 'limit'
        JLZ     start    ; if result is less than zero, go again
end:    JMP     end      ; this "stops" the program!
```
Output:
```
1
2
4
8
16
32
64
128
256
512
1024
2048
4096
8192
16384
32768
65536
131072
262144
524288
```

```asm
; --- Array demo ---
         JMP loop          ; begin by jumping over the data area

    one: 1                 ; store for use in increment

  array: 4                 ; pointer to start of array
 length: 5                 ; number of items in the array

 item_0: 1                
 item_1: 2
 item_2: 3
 item_3: 4
 item_4: 5

pointer: 4                 ; used to iterate through array items 


   loop: LOAD [pointer]    ; load value at address stored in pointer
         WRITE 100         ; output current array value to port 100
         LOAD pointer      ; load pointer address
         ADD one           ; increment pointer
         STORE pointer     ; update pointer
         LOAD array        ; address of array start
         ADD length        ; add length of array
         SUB pointer       ; compare to pointer position
         JGZ loop          ; if result is greater than zero, go again

    end: JMP end           ;  this "stops" the program!
```
Output:
```
1
2
3
4
5
```

## Assembly Syntax

As seen in the examples:

- We use square brackets to denote dereferencing.
- We use subtraction to perform comparisons (employing `JZ`, `JLZ`, or `JGZ` for `==`, `<`, and `>`, respectively).
- Given the lack of a `HALT` instruction, we reserve `end: JMP end` to terminate the program. (More specifically, a line that unconditionally jumps to itself terminates the program).

## `.peng` File Extension

For the purposes of this simulation, Stanley/Penguin source files use the extension `.peng`.

## `.egg` File Extension

For the purposes of this simulation, assembled binaries use the extension `.egg`, with the following format:

| Component    | Size     | Description                                                                               |
| :----------- | :------- | :---------------------------------------------------------------------------------------- |
| Magic number | 4 bytes  | A unique identifer to verify the file is a Stanley/Penguin binary: `0x45474721` ("EGG!"). |
| Entry point  | 4 bytes  | The memory address where the simulator should begin execution.                            |
| Word count   | 4 bytes  | The total number of 32-bit words in the binary.                                           |
| Payload      | Variable | The actual sequence of 32-bit words in Big Endian format.                                 |

Thus, the file header is **12 bytes**.

## Java Project

This project offers two distinct features: **assembly** and **execution**. The first assembles a source `.peng` file into a `.egg` binary, while the second executes a `.egg` binary via the simulated Stanley/Penguin machine.

### Assembly

TODO: instructions

### Execution

TODO: instructions
