package model;

import java.io.Serializable;

public record Command(TransactionType type, String accountNumber, long amount) implements Serializable {
    private static final long serialVersionUID = 1L;
}
