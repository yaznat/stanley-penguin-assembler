package machine;

/**
 * Configuration file for the Stanley/Penguin machine.
 */
public class SystemConfig {
    private SystemConfig() {
    }

    /**
     * The quantity of protected words stored at the beginning of virtual memory.
     */
    public static final int PROTECTED_WORD_COUNT = 16;

    /**
     * The number of words in memory. Equivalent to 256 MiB minus 256 words for
     * ports.
     */
    public static final int MEMORY_WORDS = 63_000_744;

    /**
     * The address that begins the protected memory region.
     */
    public static final int BEGIN_PROTECTED_ADDR = MEMORY_WORDS - PROTECTED_WORD_COUNT - 1;

    /**
     * The location of the instruction pointer in virtual memory.
     */
    public static final int IP_ADDR = BEGIN_PROTECTED_ADDR + 0x0;

    /**
     * Maps a raw string value to a reserved integer address.
     * 
     * @param s The string representation of an address name.
     * @return The matched address, or {@code null}.
     */
    public static Integer getAddress(String s) {
        return switch (s) {
            case "IP_ADDR" -> IP_ADDR;
            default -> null;
        };
    }
}
