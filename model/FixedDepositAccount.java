package model;

import java.io.Serializable;

public class FixedDepositAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String ownerName, long balance) {
        super(ownerName, balance);
    }

    public FixedDepositAccount(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false;
    }
}
