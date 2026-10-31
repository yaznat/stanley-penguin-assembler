package io;

import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EggWriter {
    public EggWriter() {
    }

    /**
     * Saves a Stanley/Penguin program to binary in the {@code .egg} format.
     * 
     * @param programWords Machine code words in integer format.
     * @param filename     Name of the output file.
     */
    public void saveBinary(List<Integer> programWords, String outpath) throws IOException {
        // Allocate buffer
        int headerSize = 12;
        int payloadSize = programWords.size() * 4;
        ByteBuffer buffer = ByteBuffer.allocate(headerSize + payloadSize);
        buffer.order(ByteOrder.BIG_ENDIAN); // Stanley/Penguin uses Big Endian

        // Write header
        buffer.putInt(EggFormat.MAGIC_NUMBER);
        buffer.putInt(EggFormat.ENTRY_POINT);
        buffer.putInt(programWords.size()); // word count

        // Write payload
        for (int word : programWords) {
            buffer.putInt(word);
        }

        // Write to disk
        try {
            Path outfile = Paths.get(outpath);
            if (outfile.getParent() != null) {
                Files.createDirectories(outfile.getParent());
            }

            try (FileOutputStream fos = new FileOutputStream(outfile.toFile())) {
                fos.write(buffer.array());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
