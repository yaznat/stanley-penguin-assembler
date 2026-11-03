package exception;

public class OutOfMemoryException extends RuntimeException {
    public OutOfMemoryException(String message) {
        super(
                new StringBuilder("Error: Exceeded system memory of 256 MiB")
                        .append((message == null) ? "." : ":\n" + message)
                        .toString());
    }
}
