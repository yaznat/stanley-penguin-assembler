package exception;

public class CyclicDereferenceException extends RuntimeException {
    public CyclicDereferenceException(int lineNumber, String lineContent) {
        super(
                new StringBuilder("Cyclic Dereference Error: ")
                        .append("Cyclic dereference detected.\nIn line ")
                        .append(lineNumber)
                        .append(":\n\n")
                        .append(lineContent)
                        .append('\n')
                        .toString());
    }
}
