package machine;

/**
 * A Stanley/Penguin machine port.
 */
public enum Port {
    PRINT_RAW(0x00),
    PRINT_ASCII(0x01),
    FILE_REQUEST(0x0A);

    private final int defaultAddress;

    Port(int defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    /**
     * @return The default address for this port, between 0 and 255.
     */
    public int getDefaultAddress() {
        return defaultAddress;
    }
}
