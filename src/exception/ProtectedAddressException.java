package exception;

public class ProtectedAddressException extends RuntimeException {
    public ProtectedAddressException(int index) {
        super(
                new StringBuilder("Error: tried to write to protected address '0x")
                        .append(String.format("%08X", index))
                        .append("'.")
                        .toString());
    }
}
