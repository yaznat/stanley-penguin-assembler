package machine;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;

import exception.PortInUseException;

/**
 * Abstract controller for Stanley/Penguin ports.
 */
public class PortController {
    private HashMap<Integer, Port> portLookup;

    /**
     * Constructs the controller.
     * 
     * @param portMapping A custom, optionally incomplete mapping between ports and
     *                    their IDs.
     * @throws PortInUseException If multiple ports are assigned to the same ID.
     */
    public PortController(HashMap<Port, Integer> portMapping) throws PortInUseException {
        buildPortLookup(portMapping);
    }

    /**
     * Reads a value from a port.
     * 
     * @param id The port's ID.
     * @return An integer value read from the port.
     */
    public int read(int id) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    /**
     * Writes a value to a port.
     * 
     * @param id    The port's ID.
     * @param value The value to write.
     */
    public void write(int id, int value) {
        switch (portLookup.get(id)) {
            case Port.TERMINAL_RAW:
                System.out.println(value);
                break;
            case Port.TERMINAL_ASCII:
                System.out.print((char) value);
                break;

            // Default: do nothing
        }
    }

    /**
     * Builds this controller's lookup between ID's and ports.
     * 
     * @param portMappings A custom, optionally incomplete mapping between ports and
     *                     their IDs.
     * @throws PortInUseException If multiple ports are assigned to the same ID.
     */
    private void buildPortLookup(HashMap<Port, Integer> portMappings) throws PortInUseException {
        portLookup = new HashMap<>();

        BitSet used = new BitSet(256);

        // First pass for custom mappings
        for (Entry<Port, Integer> pair : portMappings.entrySet()) {
            if (portLookup.containsKey(pair.getValue())) {
                throw new PortInUseException(pair.getValue());
            }
            portLookup.put(pair.getValue(), pair.getKey());
            used.set(pair.getValue());
        }

        // Fill in unspecified ports
        for (Port p : Port.values()) {
            if (!portMappings.containsKey(p)) {
                // Try default first
                if (!used.get(p.getDefaultAddress())) {
                    portLookup.put(p.getDefaultAddress(), p);
                    used.set(p.getDefaultAddress());
                } else {
                    // Fallback to available
                    int id = used.nextClearBit(0);
                    portLookup.put(id, p);
                    used.set(id);
                }
            }
        }
    }
}
