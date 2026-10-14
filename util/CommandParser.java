package util;

import model.Command;
import model.TransactionType;

public class CommandParser {

    public static Command parse(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        String[] parts = line.trim().split(" ");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Command: expected 3 parts");
        }

        TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
        String accountNumber = parts[1];
        long amount = Long.parseLong(parts[2]);

        return new Command(type, accountNumber, amount);
    }
}
