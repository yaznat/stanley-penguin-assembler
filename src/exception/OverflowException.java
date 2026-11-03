package exception;

public class OverflowException extends RuntimeException {
    public OverflowException(String value, boolean signed, int bitLimit, int lineNumber, String lineContent) {
        super(
                new StringBuilder("Overflow Error: ")
                        .append((signed) ? "signed" : "unsigned")
                        .append(" value '")
                        .append(value)
                        .append("' exceeds ")
                        .append(bitLimit)
                        .append(" bits.\nIn line ")
                        .append(lineNumber)
                        .append(":\n\n")
                        .append(lineContent)
                        .append('\n')
                        .toString());
    }
}
