package machine;

/**
 * Defines the ports of the Stanley/Penguin machine.
 */
public enum Port {
    TERMINAL_RAW(0x00),
    TERMINAL_ASCII(0x01);

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
