package machine;

import java.util.HashMap;

import assembler.Instruction;
import exception.MemoryIndexException;
import exception.OutOfMemoryException;
import exception.ProgramIndexException;
import exception.ProtectedAddressException;

/**
 * Top-level simulated Stanley/Penguin machine.
 */
public class Machine {
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
            OutOfMemoryException, ProgramIndexException, ProtectedAddressException {

        // Restart IP
        memory.write(SystemConfig.IP_ADDR, 0, false);

        // Load the program into memory
        int nextFreeAddress = memory.loadProgram(programWords);

        while (true) {
            int instructionPointer = memory.read(SystemConfig.IP_ADDR);

            if (instructionPointer >= nextFreeAddress) {
                return;
            }

            // Fetch
            int instructionWord = memory.read(instructionPointer);

            // Decode
            DecodedInstruction instruction = new DecodedInstruction(instructionWord);

            // Increment IP
            instructionPointer++;
            setIP(instructionPointer);

            // Resolve operand
            ResolvedAddress address = new ResolvedAddress(memory, instruction.operand(), instruction.isDereference());

            // Execute
            Instruction mnemonic = Instruction.fromOpcode(instruction.opcode());
            switch (mnemonic) {
                case LOAD -> accumulator = address.asIndirect();
                case STORE -> memory.write(address.asDirect(), accumulator);
                case READ -> portController.read(address.asDirect());
                case WRITE -> portController.write(address.asDirect(), accumulator);
                case ADD -> accumulator += address.asIndirect();
                case SUB -> accumulator -= address.asIndirect();
                case MUL -> accumulator *= address.asIndirect();
                case DIV -> accumulator /= address.asIndirect();
                case MOD -> accumulator %= address.asIndirect();
                case AND -> accumulator = accumulator & address.asIndirect();
                case OR -> accumulator = accumulator | address.asIndirect();
                case XOR -> accumulator = accumulator ^ address.asIndirect();
                case JMP -> {
                    // Halt case: unconditional jump to current line
                    if (address.asDirect() == instructionPointer - 1)
                        return;
                    conditionalJump(address.asDirect(), nextFreeAddress, true);
                }
                case JZ -> conditionalJump(address.asDirect(), nextFreeAddress, accumulator == 0);
                case JLZ -> conditionalJump(address.asDirect(), nextFreeAddress, accumulator < 0);
                case JGZ -> conditionalJump(address.asDirect(), nextFreeAddress, accumulator > 0);
                default -> throw new IllegalArgumentException("Unexpected value: " + mnemonic);
            }
        }
    }

    /**
     * Update the instruction pointer in virtual memory.
     * 
     * @param value The value to set.
     */
    private void setIP(int value) {
        boolean restricted = false;
        memory.write(SystemConfig.IP_ADDR, value, restricted);
    }

    private void conditionalJump(int address, int nextFreeAddress, boolean shouldJump) {
        if (shouldJump) {
            validateJump(address, nextFreeAddress);
            setIP(address);
        }
    }

    /**
     * Bound checks a jump.
     * 
     * @param address         The address to jump to.
     * @param nextFreeAddress The next address following the program.
     * @throws ProgramIndexException If the jump is out of bounds.
     */
    private void validateJump(int address, int nextFreeAddress) throws ProgramIndexException {
        if (address < 0 || address >= nextFreeAddress) {
            throw new ProgramIndexException(address);
        }
    }
}
