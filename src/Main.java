import java.io.IOException;

import java.util.HashMap;

import assembler.Assembler;
import io.EggReader;
import machine.Machine;
import machine.Port;

/**
 * A demonstration of the Stanley/Penguin assembler and interpreter.
 * 
 * <p>
 * Assembles Stanley/Penguin assembly into machine code, prints the results,
 * then executes the program.
 */
public class Main {
    private static final String SOURCE_PATH = "./scripts/demo.peng";
    private static final String OUT_PATH = "./out/demo.egg";

    public static void main(String[] args) {
        System.out.println("=== Stanley/Penguin Compilation Demo ===");

        Assembler a = new Assembler();

        try {
            if (a.assemble(SOURCE_PATH, OUT_PATH)) {
                // Read results
                System.out.println("Program words:");

                EggReader e = new EggReader();
                int[] programWords = e.readPayload(OUT_PATH);

                // Print machine code
                for (int i = 0; i < programWords.length; i++) {
                    String hex = String.format("%08X", programWords[i]);
                    System.out.println("0".repeat(Math.max(0, 8 - hex.length())) + hex);
                }

                System.out.println();

                // Run program
                System.out.println("Running program:");

                HashMap<Port, Integer> portMapping = new HashMap<>();
                portMapping.put(Port.TERMINAL_RAW, 100); // matches the demo

                Machine m = new Machine(portMapping);
                m.execute(programWords);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
