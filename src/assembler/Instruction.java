package assembler;

/**
 * Defines all Stanley/Penguin instructions.
 */
public enum Instruction {
    LOAD(0x0, 1),
    STORE(0x1, 1),
    READ(0x2, 1),
    WRITE(0x3, 1),
    ADD(0x4, 1),
    SUB(0x5, 1),
    MUL(0x6, 1),
    DIV(0x7, 1),
    MOD(0x8, 1),
    AND(0x9, 1),
    OR(0xA, 1),
    XOR(0xB, 1),
    JMP(0xC, 1),
    JZ(0xD, 1),
    JLZ(0xE, 1),
    JGZ(0xF, 1);

    private final int opcode;
    private final int operandCount;

    Instruction(int opcode, int operandCount) {
        this.opcode = opcode;
        this.operandCount = operandCount;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getOperandCount() {
        return operandCount;
    }
}