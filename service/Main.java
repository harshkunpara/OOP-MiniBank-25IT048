package service;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.BankInfo;
import model.Command;
import model.CurrentAccount;
import model.Customer;
import model.FixedDepositAccount;
import model.Premium;
import model.SavingsAccount;
import model.TransactionType;
import model.WithdrawRule;
import util.AnnotationValidator;
import util.BankSession;
import util.CommandParser;
import util.StatementFormatter;
import static util.Validator.isValidEmail;
import static util.Validator.isValidIfsc;
import static util.Validator.isValidMobile;
import static util.Validator.isValidPan;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   MINIBANK PRACTICAL SUITE (PRACTICAL 1 - 10)   ");
        System.out.println("==================================================");

        // --- PRACTICAL 1 - 5 RECAP ---
        System.out.println("\n>>> [PRACTICAL 1 - 5] Basic Model, Accounts & Validation");
        BankInfo bank = new BankInfo("MiniBank", "Dhrangadhra");
        System.out.println("Bank Header : " + bank);

        Customer c1 = new Customer("Riya", "riya@gmail.com", "9876543210");
        c1.setAddress(new Customer.Address("123 Station Rd", "Rajkot", "360001"));
        System.out.println("Customer    : " + c1);

        Account sa = new SavingsAccount("Riya", 5000, 1000);
        Account ca = new CurrentAccount("Harsh", 10000, 5000);
        Account fd = new FixedDepositAccount("Amit", 20000);

        Account[] accounts = { sa, ca, fd };
        for (Account acc : accounts) {
            System.out.println("Account: " + acc + " | Interest Rate: " + acc.interestRate() + "%");
        }

        System.out.println("Validator Mobile ('9876543210'): " + isValidMobile("9876543210"));
        System.out.println("Validator Email ('riya@gmail.com'): " + isValidEmail("riya@gmail.com"));
        System.out.println("Validator PAN ('ABCDE1234F'): " + isValidPan("ABCDE1234F"));
        System.out.println("Validator IFSC ('SBIN0001234'): " + isValidIfsc("SBIN0001234"));

        Command cmd = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("Parsed Command: Type=" + cmd.type() + ", Acc=" + cmd.accountNumber() + ", Amount=" + cmd.amount());
        System.out.println(StatementFormatter.buildStatement(sa));

        // --- PRACTICAL 6: INTERFACES AND PACKAGES ---
        System.out.println("\n>>> [PRACTICAL 6] Interfaces, Default Methods & Lambdas");
        System.out.println("Transactable & InterestBearing: " + sa.getOwnerName() + " Yearly Interest = " + sa.yearlyInterest());
        System.out.println("InterestBearing Monthly Interest = " + sa.monthlyInterest());

        // WithdrawRule with Anonymous Class
        WithdrawRule ruleAnonymous = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.getBalance() >= amount;
            }
        };

        // WithdrawRule with Lambda Expression
        WithdrawRule ruleLambda = (account, amount) -> account.getBalance() >= amount;

        System.out.println("WithdrawRule (Anonymous) for 2000 on balance " + sa.getBalance() + ": " + ruleAnonymous.allow(sa, 2000));
        System.out.println("WithdrawRule (Lambda) for 2000 on balance " + sa.getBalance() + ": " + ruleLambda.allow(sa, 2000));
        System.out.println("Is SavingsAccount marked Premium? " + (sa instanceof Premium));

        // --- PRACTICAL 7: ANNOTATIONS AND REFLECTION ---
        System.out.println("\n>>> [PRACTICAL 7] Annotations & Reflection Validator");
        Account validAcc = new SavingsAccount("ValidUser", 3000, 500);
        String[] validErrors = AnnotationValidator.validate(validAcc);
        System.out.println("Validation on valid account error count: " + validErrors.length);

        Account invalidAcc = new SavingsAccount("InvalidUser", -100, 500);
        String[] invalidErrors = AnnotationValidator.validate(invalidAcc);
        System.out.println("Validation on account with negative balance (-100):");
        for (String err : invalidErrors) {
            System.out.println("  Error: " + err);
        }

        // --- PRACTICAL 8: EXCEPTION HANDLING ---
        System.out.println("\n>>> [PRACTICAL 8] Exception Hierarchy & Try-With-Resources");
        Account testAcc = new SavingsAccount("TestUser", 1000, 500);

        // Try-with-resources with AutoCloseable BankSession
        try (BankSession session = new BankSession("SES-001")) {
            System.out.println("Current balance: " + testAcc.getBalance());
            System.out.println("Attempting withdrawal of 5000 (exceeds balance 1000)...");
            testAcc.withdraw(5000);
        } catch (InsufficientFundsException e) {
            System.out.println("Caught InsufficientFundsException: " + e.getMessage() + " (Shortfall: " + e.getShortfall() + ")");
        } catch (InvalidAmountException e) {
            System.out.println("Caught InvalidAmountException: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed. Balance remains: " + testAcc.getBalance());
        }

        // Negative deposit exception test
        try {
            testAcc.deposit(-200);
        } catch (InvalidAmountException e) {
            System.out.println("Caught expected exception on negative deposit: " + e.getMessage());
        }

        // --- PRACTICAL 9: MULTITHREADING ---
        System.out.println("\n>>> [PRACTICAL 9] Multithreading, Thread Lifecycle & Synchronization");
        Account sharedAcc = new SavingsAccount("SharedUser", 0, 0);
        int threadCount = 10;
        int depositsPerThread = 1000;
        long depositAmount = 1;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(sharedAcc, depositsPerThread, depositAmount), "Worker-" + (i + 1));
            System.out.println(threads[i].getName() + " initial state: " + threads[i].getState());
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
                System.out.println(t.getName() + " completed with state: " + t.getState());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Final balance after 10 threads * 1000 deposits of 1: " + sharedAcc.getBalance() + " (Expected: 10000)");

        // --- PRACTICAL 10: TRANSACTION PROCESSING ---
        System.out.println("\n>>> [PRACTICAL 10] Thread Pool, Producer-Consumer & Deadlock Resolution");

        // 1. Thread Pool
        System.out.println("--- 10.1 Thread Pool with TransactionProcessor ---");
        TransactionProcessor processor = new TransactionProcessor();
        Account poolAcc = new SavingsAccount("PoolUser", 5000, 0);
        for (int i = 0; i < 8; i++) {
            final int taskId = i + 1;
            processor.submit(() -> {
                try {
                    poolAcc.deposit(100);
                    System.out.println("Pool task " + taskId + " deposited 100 by " + Thread.currentThread().getName());
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            });
        }
        processor.stop();
        System.out.println("Pool account balance after tasks: " + poolAcc.getBalance() + " (Expected: 5800)");

        // 2. Producer-Consumer Coordination
        System.out.println("\n--- 10.2 Producer-Consumer with wait() / notify() ---");
        TransactionBuffer buffer = new TransactionBuffer(3);
        int totalItems = 5;

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= totalItems; i++) {
                try {
                    Command item = new Command(TransactionType.DEPOSIT, "AC0001", i * 100);
                    buffer.produce(item);
                    System.out.println("Produced: " + item.type() + " " + item.amount());
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ProducerThread");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= totalItems; i++) {
                try {
                    Command item = buffer.consume();
                    System.out.println("Consumed: " + item.type() + " " + item.amount());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ConsumerThread");

        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. Deadlock Demonstration & Prevention
        System.out.println("\n--- 10.3 Deadlock Prevention (Consistent Lock Ordering) ---");
        Account accA = new SavingsAccount("Alice", 2000, 0);
        Account accB = new SavingsAccount("Bob", 2000, 0);
        System.out.println("Initial: Alice balance = " + accA.getBalance() + ", Bob balance = " + accB.getBalance());

        Thread transfer1 = new Thread(() -> {
            try {
                TransactionProcessor.safeTransfer(accA, accB, 500);
                System.out.println("Transfer Alice -> Bob (500) completed successfully.");
            } catch (BankException e) {
                System.err.println("Transfer failed: " + e.getMessage());
            }
        }, "Transfer-A-to-B");

        Thread transfer2 = new Thread(() -> {
            try {
                TransactionProcessor.safeTransfer(accB, accA, 300);
                System.out.println("Transfer Bob -> Alice (300) completed successfully.");
            } catch (BankException e) {
                System.err.println("Transfer failed: " + e.getMessage());
            }
        }, "Transfer-B-to-A");

        transfer1.start();
        transfer2.start();
        try {
            transfer1.join();
            transfer2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final: Alice balance = " + accA.getBalance() + ", Bob balance = " + accB.getBalance());
        System.out.println("\n==================================================");
        System.out.println("   ALL PRACTICAL 6 - 10 REQUIREMENTS COMPLETED!   ");
        System.out.println("==================================================");
    }
}
