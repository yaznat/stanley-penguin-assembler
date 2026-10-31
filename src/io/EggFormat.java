package io;

/**
 * Specifies the format of {@code .egg} files.
 */
public final class EggFormat {
    private EggFormat() {
        throw new UnsupportedOperationException();
    }

    /**
     * Indentifier used in {@code .egg} file header.
     */
    public static final int MAGIC_NUMBER = 0x45474721; // "EGG!"

    /**
     * Entry point for code execution (relative to payload start).
     */
    public static final int ENTRY_POINT = 0;
}
