package machine;

import exception.MemoryIndexException;
import exception.OutOfMemoryException;

/**
 * Allocates and manages virtual memory for the Stanley/Penguin machine.
 * <p>
 * Each addressable unit is <b>32 bits wide</b>.
 * To store a total of 256 MiB, the virtual memory offers <b>63,000,744</b>
 * addressable units. (The remaining 256 words are reserved for ports).
 */
public class VirtualMemory {
    private static final int MEMORY_WORDS = 63_000_744;

    private int[] words;

    /**
     * Allocates the virtual memory.
     */
    public VirtualMemory() {
        words = new int[MEMORY_WORDS];
    }

    /**
     * Loads a program at the start of virtual memory.
     * 
     * @param programWords The sequence of words that makes up the program.
     * @throws OutOfMemoryException If the program length exceeds the virtual
     *                              memory.
     */
    public void loadProgram(int[] programWords) throws OutOfMemoryException {
        if (programWords.length > words.length) {
            throw new OutOfMemoryException("Program too long.");
        }
        System.arraycopy(programWords, 0, words, 0, programWords.length);
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
     * @param address The location to store the value at.
     * @param value   The value to store.
     * @throws MemoryIndexException If the provided index is invalid.
     */
    public void write(int address, int value) throws MemoryIndexException {
        if (address < 0 || address >= words.length) {
            throw new MemoryIndexException(address);
        }
        words[address] = value;
    }
}
