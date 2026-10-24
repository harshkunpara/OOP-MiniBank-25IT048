package model;

import java.io.Serializable;
import java.time.LocalDate;

public class FixedDepositAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate maturityDate;

    public FixedDepositAccount(String ownerName, long balance, LocalDate maturityDate) {
        super(ownerName, balance);
        this.maturityDate = maturityDate;
    }

    public FixedDepositAccount(String ownerName, long balance) {
        this(ownerName, balance, null);
    }

    public FixedDepositAccount(String ownerName) {
        this(ownerName, 0, null);
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public boolean isMatured() {
        return maturityDate != null && !LocalDate.now().isBefore(maturityDate);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        // Supplementary Problem: allow withdrawal only after maturity date
        if (maturityDate != null && !LocalDate.now().isBefore(maturityDate)) {
            return (getBalance() - amount) >= 0;
        }
        return false; // Deposit is locked
    }
}
