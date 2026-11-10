package machine;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;

import exception.PortInUseException;
import io.FileReader;

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
        return 0;
    }

    /**
     * Writes a value to a port.
     * 
     * @param id    The port's ID.
     * @param value The value to write.
     */
    public void write(int id, int value, VirtualMemory memory) {
        Port lookup = portLookup.get(id);
        if (lookup == null)
            return;
        switch (lookup) {
            case Port.PRINT_RAW -> System.out.println(value);
            case Port.PRINT_ASCII -> System.out.print((char) value);
            case Port.FILE_REQUEST -> handleFileRequest(value, memory);

            // Default: do nothing
        }
    }

    private void handleFileRequest(int address, VirtualMemory memory) {
        int strPtr = memory.read(memory.read(address));
        String filepath = parseNullTerminatedString(strPtr, memory);
        int destAddr = memory.read(address + 1);

        try {
            int[] unpackedBytes = FileReader.readFile(filepath);

            memory.write(address + 2, 0); // status ok

            for (int i = 0; i < unpackedBytes.length; i++) {
                memory.write(destAddr++, unpackedBytes[i]);
            }

            memory.write(address + 3, unpackedBytes.length);
        } catch (IOException e) {
            memory.write(address + 2, 1); // error status
        }
    }

    private String parseNullTerminatedString(int address, VirtualMemory memory) {
        StringBuilder sb = new StringBuilder();

        while (true) {
            int value = memory.read(address++);
            if (value == 0) {
                return sb.toString();
            }

            sb.append((char) value);
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
