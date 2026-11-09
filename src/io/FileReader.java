package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads text files into a Stanley/Penguin binary format.
 */
public class FileReader {

    /**
     * Reads file bytes into an array of integers.
     * 
     * @param filepath Path to the file.
     * @return An array of integers. (Not packed -> one integer per byte).
     * @throws IOException If the file doesn't exist.
     */
    public static int[] readFile(String filepath) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(filepath));

        int[] data = new int[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            data[i] = Byte.toUnsignedInt(bytes[i]);
        }

        return data;
    }
}
