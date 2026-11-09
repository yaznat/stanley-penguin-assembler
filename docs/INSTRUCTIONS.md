# Stanley/Penguin Instructions

The Stanley/Penguin machine has a total of **16** instructions.

## Instruction Assembly

Instructions are assembled into **32-bit** words:

| Bits (inclusive, 1-indexed) | Purpose                                                             |
| :-------------------------- | :------------------------------------------------------------------ |
| 1-4                         | Opcode                                                              |
| 5                           | Dereference flag — see **Dereferences** in [SYNTAX.md](./SYNTAX.md) |
| 6-32                        | Operand **(27 bits)**                                               |

## Instruction List

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
