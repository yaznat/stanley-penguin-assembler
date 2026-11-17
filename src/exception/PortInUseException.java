package exception;

public class PortInUseException extends RuntimeException {
    public PortInUseException(int id) {
        super(
                new StringBuilder("Port In Use Error: duplicate assignment for port ")
                        .append(id)
                        .append('.')
                        .toString());
    }
}
