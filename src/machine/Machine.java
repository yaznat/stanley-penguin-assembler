package machine;

import java.util.HashMap;

import assembler.Format;
import assembler.Instruction;
import exception.MemoryIndexException;
import exception.OutOfMemoryException;
import exception.ProgramIndexException;

/**
 * Top-level simulated Stanley/Penguin machine.
 */
public class Machine {
    private int instructionPointer;
    private int accumulator;

    private VirtualMemory memory;
    private PortController portController;

    /**
     * Constructs and allocates the machine.
     */
    public Machine() {
        this(null);
    }

    /**
     * Constructs and allocates the machine.
     * 
     * @param portMapping An optional custom mapping of ports to specific IDs.
     */
    public Machine(HashMap<Port, Integer> portMapping) {
        instructionPointer = 0;
        accumulator = 0;
        memory = new VirtualMemory();
        portController = new PortController((portMapping == null) ? new HashMap<>() : portMapping);
    }

    /**
     * Executes a Stanley/Penguin program.
     * 
     * @param programWords A sequence of program words as integers.
     */
    public void execute(int[] programWords) throws MemoryIndexException,
            OutOfMemoryException, ProgramIndexException {
        // Load the program into memory
        memory.loadProgram(programWords);

        int programCutoff = programWords.length - 1;

        instructionPointer = 0;

        while (true) {
            if (instructionPointer > programCutoff) {
                return;
            }

            // Unpack values
            int instruction = memory.read(instructionPointer);
            int opcode = instruction >>> (Format.OPERAND_SIZE_BITS + 1);
            int operand = instruction & Format.OPERAND_MASK; // unsigned

            boolean dereference = (instruction & (1 << Format.OPERAND_SIZE_BITS)) != 0;
            int operationalValue = (dereference) ? memory.read(memory.read(operand)) : memory.read(operand);
            int jumpValue = (dereference) ? memory.read(operand) : operand;

            // Halt case: unconditional jump to current line
            if (opcode == Instruction.JMP.getOpcode() && jumpValue == instructionPointer) {
                return;
            }

            // Increment unless a jump occurs
            instructionPointer++;

            // Execute instruction
            if (opcode == Instruction.LOAD.getOpcode()) {
                accumulator = operationalValue;
            } else if (opcode == Instruction.STORE.getOpcode()) {
                memory.write(jumpValue, accumulator);
            } else if (opcode == Instruction.READ.getOpcode()) {
                accumulator = portController.read(jumpValue);
            } else if (opcode == Instruction.WRITE.getOpcode()) {
                portController.write(jumpValue, accumulator);
            } else if (opcode == Instruction.ADD.getOpcode()) {
                accumulator += operationalValue;
            } else if (opcode == Instruction.SUB.getOpcode()) {
                accumulator -= operationalValue;
            } else if (opcode == Instruction.MUL.getOpcode()) {
                accumulator *= operationalValue;
            } else if (opcode == Instruction.DIV.getOpcode()) {
                accumulator /= operationalValue;
            } else if (opcode == Instruction.MOD.getOpcode()) {
                accumulator %= operationalValue;
            } else if (opcode == Instruction.AND.getOpcode()) {
                accumulator = accumulator & operationalValue;
            } else if (opcode == Instruction.OR.getOpcode()) {
                accumulator = accumulator | operationalValue;
            } else if (opcode == Instruction.XOR.getOpcode()) {
                accumulator = accumulator ^ operationalValue;
            } else if (opcode == Instruction.JMP.getOpcode()) {
                validateJump(jumpValue, programCutoff);
                instructionPointer = jumpValue;
            } else if (opcode == Instruction.JZ.getOpcode()) {
                if (accumulator == 0) {
                    validateJump(jumpValue, programCutoff);
                    instructionPointer = jumpValue;
                }
            } else if (opcode == Instruction.JLZ.getOpcode()) {
                if (accumulator < 0) {
                    validateJump(jumpValue, programCutoff);
                    instructionPointer = jumpValue;
                }
            } else if (opcode == Instruction.JGZ.getOpcode()) {
                if (accumulator > 0) {
                    validateJump(jumpValue, programCutoff);
                    instructionPointer = jumpValue;
                }
            }
        }
    }

    /**
     * Bound checks a jump.
     * 
     * @param address       The address to jump to.
     * @param programLength The number of lines in the program.
     * @throws ProgramIndexException If the jump is out of bounds.
     */
    private void validateJump(int address, int programLength) throws ProgramIndexException {
        if (address < 0 || address > programLength) {
            throw new ProgramIndexException(address);
        }
    }
}
