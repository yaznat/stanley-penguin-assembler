package assembler;

/**
 * Statically defined Stanley/Penguin format information.
 */
public final class Format {
    private Format() {
        throw new UnsupportedOperationException();
    }

    /**
     * Maps a raw string value to an {@code Instruction}.
     * 
     * @param s The string representation of an instruction (case sensitive).
     * @return The matched instruction, or {@code null}.
     */
    public static Instruction parseInstruction(String s) {
        return switch (s) {
            case "LOAD" -> Instruction.LOAD;
            case "STORE" -> Instruction.STORE;
            case "READ" -> Instruction.READ;
            case "WRITE" -> Instruction.WRITE;
            case "ADD" -> Instruction.ADD;
            case "SUB" -> Instruction.SUB;
            case "MUL" -> Instruction.MUL;
            case "DIV" -> Instruction.DIV;
            case "MOD" -> Instruction.MOD;
            case "AND" -> Instruction.AND;
            case "OR" -> Instruction.OR;
            case "XOR" -> Instruction.XOR;
            case "JMP" -> Instruction.JMP;
            case "JZ" -> Instruction.JZ;
            case "JLZ" -> Instruction.JLZ;
            case "JGZ" -> Instruction.JGZ;
            default -> null;
        };
    }

    private static final boolean[] IS_ALLOWED = new boolean[123];

    static {
        // Bitmask for O(1) lookup
        for (char c : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_:[]".toCharArray()) {
            IS_ALLOWED[c] = true;
        }
    }

    /**
     * O(1) character lookup.
     * 
     * @param c The character to validate.
     * @return {@code true} if the character is allowed, else {@code false}.
     */
    public static boolean isAllowed(char c) {
        return c < 123 && IS_ALLOWED[c];
    }
}
