package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import model.Account;

/**
 * Practical 11: Object Serialization and Deserialization for MiniBank state persistence.
 */
public class StatePersister {

    public static void save(Account[] accounts, Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Path file cannot be null");
        }
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(accounts);
        }
    }

    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        if (file == null || !Files.exists(file)) {
            return new Account[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Account[] accArray) {
                return accArray;
            }
            throw new IOException("Unexpected data format in state file: " + file);
        }
    }
}
