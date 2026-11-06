package machine;

import assembler.Format;

/**
 * An instruction decoded from machine code.
 */
public class DecodedInstruction {
    int opcode;
    int operand;
    boolean dereference;

    /**
     * Decodes an instruction.
     * 
     * @param instruction A program word.
     */
    public DecodedInstruction(int instruction) {
        opcode = instruction >>> (Format.OPERAND_SIZE_BITS + 1);
        operand = instruction & Format.OPERAND_MASK; // unsigned
        dereference = (instruction & (1 << Format.OPERAND_SIZE_BITS)) != 0;
    }

    /**
     * @return This instruction's opcode.
     */
    public int opcode() {
        return opcode;
    }

    /**
     * @return This instruction's operand value.
     */
    public int operand() {
        return operand;
    }

    /**
     * @return Whether this instruction's operand value is a dereference.
     */
    public boolean isDereference() {
        return dereference;
    }
}
