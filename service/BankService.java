package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.annotation.Audit;
import util.AnnotationValidator;
import util.AuditLogger;
import util.ReportGenerator;
import util.StatePersister;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;

/**
 * Practical 14: Central Service Layer assembling MiniBank into an enterprise architecture:
 * wiring generic repository, domain validation, custom exceptions, thread pool, transaction logging,
 * and reflective audit tracking.
 */
public class BankService {
    private final Repository<Account> accountRepository;
    private final TransactionProcessor transactionProcessor;
    private final Path logFilePath;

    public BankService() {
        this(new Repository<>(), new TransactionProcessor(), Path.of("logs", "transactions.log"));
    }

    public BankService(Repository<Account> accountRepository, TransactionProcessor processor, Path logFilePath) {
        this.accountRepository = accountRepository;
        this.transactionProcessor = processor;
        this.logFilePath = logFilePath;
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(String type, String ownerName, long initialBalance, long extraParam) throws BankException {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new InvalidAmountException("Owner name cannot be empty");
        }
        if (initialBalance < 0) {
            throw new InvalidAmountException("Initial balance cannot be negative: " + initialBalance);
        }

        String normalizedType = type != null ? type.trim().toUpperCase() : "SAVINGS";
        Account account;
        switch (normalizedType) {
            case "SAVINGS" -> account = new SavingsAccount(ownerName.trim(), initialBalance, extraParam > 0 ? extraParam : 1000);
            case "CURRENT" -> account = new CurrentAccount(ownerName.trim(), initialBalance, extraParam > 0 ? extraParam : 5000);
            case "FIXED_DEPOSIT", "FD" -> account = new FixedDepositAccount(ownerName.trim(), initialBalance);
            default -> throw new BankException("Unsupported account type: " + type);
        }

        // Validate entity constraints via reflection (Practical 7)
        String[] validationErrors = AnnotationValidator.validate(account);
        if (validationErrors.length > 0) {
            throw new BankException("Account entity validation failed: " + String.join(", ", validationErrors));
        }

        accountRepository.save(account);
        transactionProcessor.registerAccount(account);

        logTransaction("OPEN_ACCOUNT " + account.getAccountNumber() + " " + initialBalance);
        AuditLogger.logIfAudited(BankService.class, "openAccount", account.getAccountNumber(), ownerName, initialBalance);
        return account;
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive: " + amount);
        }
        Account account = getExistingAccount(accountNumber);
        account.deposit(amount);

        logTransaction("DEPOSIT " + accountNumber + " " + amount);
        AuditLogger.logIfAudited(BankService.class, "deposit", accountNumber, amount);
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive: " + amount);
        }
        Account account = getExistingAccount(accountNumber);
        account.withdraw(amount);

        logTransaction("WITHDRAW " + accountNumber + " " + amount);
        AuditLogger.logIfAudited(BankService.class, "withdraw", accountNumber, amount);
    }

    @Audit(action = "TRANSFER")
    public void transfer(String fromAccountNumber, String toAccountNumber, long amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive: " + amount);
        }
        Account from = getExistingAccount(fromAccountNumber);
        Account to = getExistingAccount(toAccountNumber);

        // Safe transfer ensuring deadlock prevention with consistent lock ordering (Practical 10 & 14)
        TransactionProcessor.safeTransfer(from, to, amount);

        logTransaction("TRANSFER " + fromAccountNumber + "->" + toAccountNumber + " " + amount);
        AuditLogger.logIfAudited(BankService.class, "transfer", fromAccountNumber, toAccountNumber, amount);
    }

    public String statement(String accountNumber) throws BankException {
        Account account = getExistingAccount(accountNumber);
        return StatementFormatter.buildStatement(account);
    }

    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> searchByOwnerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return listAccounts();
        }
        String query = name.trim().toLowerCase();
        List<Account> matches = new ArrayList<>();
        for (Account acc : accountRepository.findAll()) {
            if (acc.getOwnerName().toLowerCase().contains(query)) {
                matches.add(acc);
            }
        }
        return matches;
    }

    public void processBatch(List<model.Command> commands) throws InterruptedException {
        transactionProcessor.processBatch(commands);
    }

    public String generateReport(Path logsDir, Path reportFile) throws IOException {
        return ReportGenerator.generateReport(logsDir, reportFile);
    }

    public void saveState(Path file) throws IOException {
        List<Account> all = accountRepository.findAll();
        StatePersister.save(all.toArray(new Account[0]), file);
    }

    public void loadState(Path file) throws IOException, ClassNotFoundException {
        Account[] loaded = StatePersister.load(file);
        for (Account acc : loaded) {
            accountRepository.save(acc);
            transactionProcessor.registerAccount(acc);
        }
    }

    public Repository<Account> getRepository() {
        return accountRepository;
    }

    public TransactionProcessor getTransactionProcessor() {
        return transactionProcessor;
    }

    private Account getExistingAccount(String accountNumber) throws AccountNotFoundException {
        if (accountNumber == null) {
            throw new AccountNotFoundException("Account number cannot be null");
        }
        Account acc = accountRepository.findById(accountNumber.trim());
        if (acc == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return acc;
    }

    private void logTransaction(String entry) {
        if (logFilePath != null) {
            try {
                TransactionLog.append(logFilePath, entry);
            } catch (IOException e) {
                System.err.println("Failed to write to transaction log: " + e.getMessage());
            }
        }
    }
}
