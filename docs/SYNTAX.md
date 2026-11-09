# Stanley/Penguin Syntax

Comprehensive syntax guide for Stanley/Penguin assembly.

## Line Structure

A non-empty line must either contain a **value** or an **instruction**:

### Values

Values **must** be prefaced by a label:

```asm
MASK: 0x7FFFFFFF
```

The assembler stores the value of `MASK` as a raw, signed **32-bit** integer.

### Instructions

Instructions can **optionally** be prefaced by a label:

```asm
loop: LOAD   MASK
```

OR

```asm
      LOAD   MASK
```

This line is assembled as a **32-bit** integer, with the first **four** bits reserved for the opcode, the **fifth** bit reserved as a dereference flag, and the remaining **27 bits** used to store the address of `MASK` as an unsigned integer.

## Labels

Labels are used to attribute values or instructions with an address. Forward references **are** supported.

Example (with corresponding machine code in hexadecimal):

```asm
C0000002             JMP    start

00000002        TW0: 2

00000001      start: LOAD   TW0
30000000             WRITE  0x00        ; prints "2"
```

Note that the assembler replaces `TWO` with `1`, which is the label's address. Similarly, `start` is replaced with `2`.

### Format Clarification

While it might seem intuitive to use the following format, Stanley/Penguin doesn't support lines with empty labels:

```asm
start:
       LOAD x
       ...
```

## Dereferencing

Stanley/Penguin uses **square brackets** to denote dereferencing:

```asm
C0000002             JMP    start

00000002        TW0: 2
00000001    TWO_ptr: TWO

08000002      start: LOAD   [TW0_ptr]   ; loads M[M[TW0_ptr]] into A
30000000             WRITE  0x00        ; prints "2"
```

Note that the second hexadecimal digit being **8** or higher in the program word indicates that the assembler has turned on the dereference bit.

## HALT Replacement

Given the lack of a `HALT` instruction, Stanley/Penguin establishes the following convention to exit a program:

```asm
end: JMP end
```

Specifically, executing a line that jumps to itself unconditionally exits the program.

## Comments

Text that trails a semicolon is considered a comment:

```asm
A: 0          ; etc
```

Lines may be entirely commented out:

```asm
; --- implementation ---
X: 0
...
```