package model;

import java.io.Serializable;

public class SavingsAccount extends Account implements Premium, Serializable {
    private static final long serialVersionUID = 1L;

    private long minBalance;

    public SavingsAccount(String ownerName, long balance, long minBalance) {
        super(ownerName, balance);
        this.minBalance = minBalance;
    }

    public SavingsAccount(String ownerName, long balance) {
        this(ownerName, balance, 1000);
    }

    public SavingsAccount(String ownerName) {
        this(ownerName, 0, 1000);
    }

    public long getMinBalance() {
        return minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }
}
