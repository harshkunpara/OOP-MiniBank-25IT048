package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * Practical 11: Append-only transaction logger using Java NIO Files.write.
 */
public class TransactionLog {

    public static void append(Path file, String line) throws IOException {
        if (file == null || line == null) {
            throw new IllegalArgumentException("File path and line cannot be null");
        }
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(
            file,
            Collections.singletonList(line),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }
}
