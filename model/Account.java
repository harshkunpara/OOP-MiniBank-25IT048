package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.io.Serializable;
import java.util.Objects;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    @MaxLength(50)
    private String ownerName;

    @Positive
    private long balance;

    private boolean active;
    private static int accountCounter = 1;

    // Practical 11 Supplementary: Transient field that is not saved during object serialization
    private transient String transientSessionTag;

    private static synchronized String generateAccountNumber() {
        return String.format("AC%04d", accountCounter++);
    }

    public static synchronized void syncAccountCounter(String accountNumber) {
        if (accountNumber != null && accountNumber.matches("^AC\\d+$")) {
            try {
                int num = Integer.parseInt(accountNumber.substring(2));
                if (num >= accountCounter) {
                    accountCounter = num + 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public Account(String ownerName, long balance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public String getTransientSessionTag() {
        return transientSessionTag;
    }

    public void setTransientSessionTag(String transientSessionTag) {
        this.transientSessionTag = transientSessionTag;
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
            long shortfall;
            if (amount > balance) {
                shortfall = amount - balance;
            } else if (this instanceof SavingsAccount sa) {
                shortfall = amount - Math.max(0, balance - sa.getMinBalance());
            } else {
                shortfall = amount;
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

    // Practical 8 Requirement 3: uses try, catch and finally and re-throws BankException when needed
    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Target account not found");
        }
        if (this.equals(to)) {
            throw new InvalidAmountException("Cannot transfer to the same account");
        }
        boolean debited = false;
        try {
            this.withdraw(amount);
            debited = true;
            to.deposit(amount);
        } catch (BankException e) {
            if (debited) {
                try {
                    // Rollback withdrawal to maintain balance consistency
                    this.deposit(amount);
                } catch (InvalidAmountException ignored) {
                }
            }
            throw e;
        } finally {
            // Auditing or session completion hook as required by Practical 8
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
    public int compareTo(Account other) {
        if (other == null) {
            return 1;
        }
        return this.accountNumber.compareTo(other.accountNumber);
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
