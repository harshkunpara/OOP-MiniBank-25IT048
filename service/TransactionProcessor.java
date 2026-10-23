package service;

import exception.AccountNotFoundException;
import exception.BankException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import model.Account;
import model.Command;

/**
 * Practicals 10, 12, 14: Transaction processor with a managed thread pool,
 * ConcurrentHashMap for thread-safe account access, and deadlock-free transfers.
 */
public class TransactionProcessor {
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> threadTaskCount = new ConcurrentHashMap<>();

    public TransactionProcessor() {
        this(4);
    }

    public TransactionProcessor(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public void registerAccount(Account account) {
        if (account != null) {
            sharedAccounts.put(account.getAccountNumber(), account);
        }
    }

    public Account getAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    public Map<String, Account> getSharedAccounts() {
        return Collections.unmodifiableMap(sharedAccounts);
    }

    public void submit(Runnable task) {
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            threadTaskCount.computeIfAbsent(threadName, k -> new AtomicInteger(0)).incrementAndGet();
            task.run();
        });
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, AtomicInteger> getThreadTaskCount() {
        return Collections.unmodifiableMap(threadTaskCount);
    }

    /**
     * Executes a batch of commands concurrently using the managed thread pool and the thread-safe store.
     */
    public void processBatch(List<Command> commands) throws InterruptedException {
        if (commands == null || commands.isEmpty()) {
            return;
        }
        CountDownLatch latch = new CountDownLatch(commands.size());
        for (Command cmd : commands) {
            submit(() -> {
                try {
                    Account acc = sharedAccounts.get(cmd.accountNumber());
                    if (acc != null) {
                        switch (cmd.type()) {
                            case DEPOSIT -> acc.deposit(cmd.amount());
                            case WITHDRAW -> acc.withdraw(cmd.amount());
                            case TRANSFER -> {
                                // Default self-contained transfer if needed
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[Batch Task Error] Account: " + cmd.accountNumber() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);
    }

    // Deadlock-prone transfer locking in parameter order (for educational demonstration in Practical 10)
    public static void unsafeTransfer(Account from, Account to, long amount) throws BankException {
        if (from == null || to == null) {
            throw new AccountNotFoundException("One or both accounts not found");
        }
        synchronized (from) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (to) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    // Deadlock-free transfer locking smaller accountNumber first (Practical 10 Requirement 4)
    public static void safeTransfer(Account from, Account to, long amount) throws BankException {
        if (from == null || to == null) {
            throw new AccountNotFoundException("One or both accounts not found");
        }
        if (from.equals(to)) {
            throw new BankException("Cannot transfer funds to the same account");
        }

        // Consistent lock acquisition order by lexicographical account number
        Account first = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account second = (first == from) ? to : from;

        synchronized (first) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (second) {
                from.withdraw(amount);
                try {
                    to.deposit(amount);
                } catch (BankException e) {
                    // Rollback to prevent loss of funds
                    from.deposit(amount);
                    throw e;
                }
            }
        }
    }
}
