package machine;

import java.util.concurrent.ThreadLocalRandom;

import exception.MemoryIndexException;
import exception.OutOfMemoryException;
import exception.ProtectedAddressException;

/**
 * Allocates and manages virtual memory for the Stanley/Penguin machine.
 * <p>
 * Each addressable unit is <b>32 bits wide</b>.
 * 
 */
public class VirtualMemory {
    private int[] words;

    /**
     * Allocates the virtual memory.
     */
    public VirtualMemory() {
        words = new int[SystemConfig.MEMORY_WORDS];
    }

    /**
     * Loads a program at the start of virtual memory.
     * 
     * @param programWords The sequence of words that makes up the program.
     * @return The next memory address following the program.
     * @throws OutOfMemoryException If the program length exceeds the virtual
     *                              memory.
     */
    public int loadProgram(int[] programWords) throws OutOfMemoryException {
        if (programWords.length > words.length - SystemConfig.PROTECTED_WORD_COUNT) {
            throw new OutOfMemoryException("Program too long.");
        }
        System.arraycopy(programWords, 0, words, 0, programWords.length);
        return programWords.length;
    }

    /**
     * Updates all values that change per-tick.
     */
    public void tick() {
        words[SystemConfig.RAND_ADDR] = ThreadLocalRandom.current().nextInt();
    }

    /**
     * Retrieves a value from memory.
     * 
     * @param address The location to retrieve.
     * @return The value stored at the address.
     * @throws MemoryIndexException If the provided index is invalid.
     */
    public int read(int address) throws MemoryIndexException {
        if (address < 0 || address >= words.length) {
            throw new MemoryIndexException(address);
        }
        return words[address];
    }

    /**
     * Stores a value in memory.
     * 
     * @param address    The location to store the value at.
     * @param value      The value to store.
     * @param restricted Whether to disallow writes to protected memory.
     * @throws MemoryIndexException      If the provided index is invalid.
     * @throws ProtectedAddressException If writing to a protected address in
     *                                   restricted mode.
     */
    public void write(int address, int value, boolean restricted)
            throws MemoryIndexException, ProtectedAddressException {
        if (address < 0 || address >= words.length) {
            throw new MemoryIndexException(address);
        }
        if (restricted && address >= SystemConfig.BEGIN_PROTECTED_ADDR) {
            throw new ProtectedAddressException(address);
        }
        words[address] = value;
    }

    /**
     * Stores a value in memory.
     * 
     * @param address The location to store the value at.
     * @param value   The value to store.
     * @throws MemoryIndexException      If the provided index is invalid.
     * @throws ProtectedAddressException If writing to a protected address.
     */
    public void write(int address, int value)
            throws MemoryIndexException, ProtectedAddressException {
        write(address, value, true);
    }
}
