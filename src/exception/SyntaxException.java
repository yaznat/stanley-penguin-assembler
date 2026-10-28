package exception;

public class SyntaxException extends RuntimeException {
    public SyntaxException(String message, int lineNumber, String lineContent) {
        super(
                new StringBuilder("Syntax Error: ")
                        .append(message)
                        .append("\nIn line ")
                        .append(lineNumber)
                        .append(":\n\n")
                        .append(lineContent)
                        .append('\n')
                        .toString());
    }
}
