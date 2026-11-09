
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import assembler.Assembler;
import exception.MemoryIndexException;
import io.EggReader;
import machine.Machine;
import machine.Port;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0 || args[0].equals("--help")) {
                printHelp();
                return;
            }

            switch (args[0]) {
                case "nest" -> nest(args);
                case "hatch" -> hatch(args);
                case "--ports" -> printPorts();
                default -> {
                    throw new IllegalArgumentException(
                            "Unknown command: " + args[0]);
                }
            }

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (MemoryIndexException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void nest(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: stanley nest <file.peng> [-o output.egg]");
        }

        String input = args[1];
        String output = input.replaceFirst("\\.peng$", ".egg");

        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                output = args[++i];
            } else {
                throw new IllegalArgumentException(
                        "Unknown argument: " + args[i]);
            }
        }

        Assembler assembler = new Assembler();

        if (!assembler.assemble(input, output)) {
            throw new IllegalArgumentException("Assembly failed.");
        }
    }

    private static void hatch(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: stanley hatch <file.egg> [--map_ports PORT=ADDRESS ...]");
        }

        String input = args[1];

        // Initialize port mappings with default addresses
        HashMap<Port, Integer> portMapping = new HashMap<>();

        for (Port port : Port.values()) {
            portMapping.put(port, port.getDefaultAddress());
        }

        // Parse optional arguments
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("--map_ports")) {
                i = mapPorts(args, i + 1, portMapping);
            } else {
                throw new IllegalArgumentException(
                        "Unknown argument: " + args[i]);
            }
        }

        EggReader reader = new EggReader();
        int[] programWords = reader.readPayload(input);

        Machine machine = new Machine(portMapping);
        machine.execute(programWords);
    }

    /**
     * Parses PORT=ADDRESS assignments and updates the port mapping.
     *
     * @param args        command-line arguments
     * @param start       starting index after --map_ports
     * @param portMapping mapping to update
     * @return index of the last consumed argument
     */
    private static int mapPorts(
            String[] args,
            int start,
            HashMap<Port, Integer> portMapping) {
        int i = start;

        if (i >= args.length || args[i].startsWith("--")) {
            throw new IllegalArgumentException(
                    "--map_ports requires at least one PORT=ADDRESS assignment.");
        }

        while (i < args.length && !args[i].startsWith("--")) {
            String[] assignment = args[i].split("=", -1);

            if (assignment.length != 2
                    || assignment[0].isBlank()
                    || assignment[1].isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid port mapping: " + args[i]
                                + ". Expected PORT=ADDRESS.");
            }

            // Resolve port name (case-insensitive)
            Port port;

            try {
                port = Port.valueOf(
                        assignment[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Unknown port: " + assignment[0]
                                + ". Use 'stanley --ports' to see available ports.");
            }

            // Parse address.
            int address = parseAddress(assignment[1]);

            portMapping.put(port, address);

            i++;
        }

        return i - 1;
    }

    /**
     * Parses a decimal or hexadecimal address.
     */
    private static int parseAddress(String value) {
        try {
            int address;

            if (value.startsWith("0x") || value.startsWith("0X")) {
                address = Integer.parseInt(value.substring(2), 16);
            } else {
                address = Integer.parseInt(value, 10);
            }

            if (address < 0) {
                throw new IllegalArgumentException(
                        "Port addresses cannot be negative: " + value);
            }

            if (address > 255) {
                throw new IllegalArgumentException(
                        "Port address exceeds 8 bits: " + address);
            }

            return address;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid port address: " + value
                            + ". Expected a nonnegative decimal or hexadecimal integer.");
        }
    }

    private static void printPorts() {
        System.out.println("Available I/O ports:");

        for (Port port : Port.values()) {
            String hex = String.format(
                    "%02X", port.getDefaultAddress());

            System.out.println(
                    "0x" + hex + ": " + port.name());
        }
    }

    private static void printHelp() {
        System.out.println("""
                Stanley/Penguin Assembler

                Usage:
                  stanley nest <file.peng> [-o output.egg]
                  stanley hatch <file.egg> [--map_ports PORT=ADDRESS ...]
                  stanley --ports
                  stanley --help

                Commands:
                  nest         Assemble a .peng source file
                  hatch        Execute a compiled .egg program

                Options:
                  -o           Specify output file
                  --map_ports  Override port addresses
                  --ports      List available I/O ports
                  --help       Display this help message

                Port mapping examples:
                  --map_ports PRINT_RAW=1
                  --map_ports PRINT_RAW=1 FILE_REQUEST=0xFF
                """);
    }
}
