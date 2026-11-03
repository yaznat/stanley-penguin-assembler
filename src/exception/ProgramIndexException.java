package exception;

public class ProgramIndexException extends RuntimeException {
    public ProgramIndexException(int index) {
        super(
                new StringBuilder("Error: address '0x")
                        .append(String.format("%08X", index))
                        .append("' exceeds program bounds.")
                        .toString());
    }
}
