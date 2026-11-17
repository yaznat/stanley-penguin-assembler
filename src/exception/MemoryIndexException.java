package exception;

public class MemoryIndexException extends RuntimeException {
    public MemoryIndexException(int index) {
        super(
                new StringBuilder("Memory Index Error: address '0x")
                        .append(String.format("%08X", index))
                        .append("' out of bounds.")
                        .toString());
    }
}
