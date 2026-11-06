package machine;

/**
 * An address resolved from an operand value.
 */
public class ResolvedAddress {
    private int direct;
    private int indirect;

    /**
     * Resolves an address into direct and indirect components.
     * 
     * @param memory
     * @param operand
     * @param dereference
     */
    public ResolvedAddress(VirtualMemory memory, int operand, boolean dereference) {
        int valueAtAddress = memory.read(operand);
        direct = (dereference) ? valueAtAddress : operand;
        indirect = (dereference) ? memory.read(valueAtAddress) : valueAtAddress;
    }

    /**
     * @return This address, or the value stored at this address if a dereference
     *         was specified.
     */
    public int asDirect() {
        return direct;
    }

    /**
     * @return The value stored at this address (X), or the value stored at the
     *         address X if a dereference was specified.
     */
    public int asIndirect() {
        return indirect;
    }
}
