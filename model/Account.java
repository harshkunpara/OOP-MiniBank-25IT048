package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.util.Objects;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing {

    @Id
    private final String accountNumber;

    @MaxLength(50)
    private String ownerName;

    @Positive
    private long balance;

    private boolean active;
    private static int accountcounter = 1;

    private static String genrateaccountnumber() {
        return String.format("AC%04d", accountcounter++);
    }

    public Account(String ownerName, long balance) {
        this.accountNumber = genrateaccountnumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive: " + amount);
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive: " + amount);
        }
        if (!canWithdraw(amount)) {
            long shortfall = amount - balance;
            if (shortfall < 0) {
                shortfall = 0;
            }
            throw new InsufficientFundsException("Insufficient funds: short by " + shortfall, shortfall);
        }
        balance -= amount;
        return true;
    }

    // Preserved for backward compatibility with Practicals 1-5
    public void DEPOSIT(long amount) {
        try {
            deposit(amount);
        } catch (InvalidAmountException e) {
            // keep old behavior of ignoring negative deposit
        }
    }

    // Preserved for backward compatibility with Practicals 1-5
    public boolean WITHDRAW(long amount) {
        try {
            return withdraw(amount);
        } catch (BankException e) {
            return false;
        }
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Target account not found");
        }
        try {
            this.withdraw(amount);
            to.deposit(amount);
        } catch (BankException e) {
            throw e;
        } finally {
            // Execution log or cleanup if necessary
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Account a = (Account) obj;
        return accountNumber.equals(a.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);
}
