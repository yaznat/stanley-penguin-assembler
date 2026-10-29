package exception;

public class OverflowException extends RuntimeException {
    public OverflowException(String value, boolean signed, int lineNumber, String lineContent) {
        super(
                new StringBuilder("Overflow Error: ")
                        .append((signed) ? "signed" : "unsigned")
                        .append(" value '")
                        .append(value)
                        .append("' exceeds the operand limit.\nIn line ")
                        .append(lineNumber)
                        .append(":\n\n")
                        .append(lineContent)
                        .append('\n')
                        .toString());
    }
}
