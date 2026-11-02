import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import assembler.Assembler;

/**
 * A demonstration of the Stanley/Penguin compiler.
 * 
 * <p>
 * Compiles Stanley/Penguin assembly into machine code and prints the results.
 */
public class Main {
    private static final String SOURCE_PATH = "./scripts/demo.peng";
    private static final String OUT_PATH = "./out/demo.egg";

    public static void main(String[] args) {
        System.out.println("=== Stanley/Penguin Compilation Demo ===");

        Assembler a = new Assembler();

        try {
            a.assemble(SOURCE_PATH, OUT_PATH);

            // Read and print results
            System.out.println("Program words:");
            printEggPayload(OUT_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the program words stored in a {@code .egg} file, line by line.
     * @param filepath Path to a {@code .egg} file.
     * @throws IOException If the file doesn't exist.
     */
    private static void printEggPayload(String filepath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Path.of(filepath));
        int headerSize = 12;

        for (int i = 0; i < fileBytes.length - headerSize; i++) {
            String hex = String.format("%02X", fileBytes[headerSize + i]);
            System.out.print("0".repeat(Math.max(0, 2 - hex.length())) + hex);

            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
    }
}
