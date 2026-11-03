package exception;

public class PortInUseException extends RuntimeException {
    public PortInUseException(int id) {
        super(
                new StringBuilder("Error: duplicate assignment for port ")
                        .append(id)
                        .append('.')
                        .toString());
    }
}
