package io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Read utility for {@code .egg} files.
 */
public class EggReader {
    public EggReader() {
    }

    /**
     * Reads the payload from an {@code .egg} file into an array of integers.
     * 
     * @param filepath Path to a {@code .egg} file.
     * @throws IOException If the file doesn't exist.
     */
    public int[] readPayload(String filepath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Path.of(filepath));
        int headerSize = 12;

        ByteBuffer buffer = ByteBuffer.wrap(fileBytes, headerSize, fileBytes.length - headerSize);
        buffer.order(ByteOrder.BIG_ENDIAN);

        int[] programWords = new int[(int) (fileBytes.length - headerSize) / 4];

        for (int i = 0; i < programWords.length; i++) {
            programWords[i] = buffer.getInt();
        }

        return programWords;
    }
}
