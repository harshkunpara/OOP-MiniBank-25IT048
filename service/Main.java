package service;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.BankInfo;
import model.Command;
import model.CurrentAccount;
import model.Customer;
import model.FixedDepositAccount;
import model.MenuOption;
import model.Premium;
import model.SavingsAccount;
import model.TransactionType;
import model.WithdrawRule;
import test.TestRunner;
import util.AnnotationValidator;
import util.AuditLogger;
import util.BankSession;
import util.CommandParser;
import util.ReportGenerator;
import util.StatePersister;
import util.StatementFormatter;
import util.TransactionLog;
import static util.Validator.isValidAmount;
import static util.Validator.isValidEmail;
import static util.Validator.isValidIfsc;
import static util.Validator.isValidMobile;
import static util.Validator.isValidPan;

/**
 * MASTER DEMONSTRATION SUITE FOR PART B MINIBANK (PRACTICALS 1 - 14)
 * Evaluates, exercises, and verifies every required concept, class, and method
 * defined across all university laboratory sessions.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        MINIBANK COMPREHENSIVE LABORATORY SUITE (PRACTICALS 1 - 14)             ");
        System.out.println("              Faculty of Engineering & Technology | CEUE203 OOP                ");
        System.out.println("================================================================================\n");

        demoPractical1();
        demoPractical2();
        demoPractical3();
        demoPractical4();
        demoPractical5();
        demoPractical6();
        demoPractical7();
        demoPractical8();
        demoPractical9();
        demoPractical10();
        demoPractical11();
        demoPractical12();
        demoPractical13();
        demoPractical14();

        System.out.println("\n================================================================================");
        System.out.println("   ALL 14 PART B PRACTICALS COMPLETED, INTEGRATED AND VERIFIED SUCCESSFULLY!    ");
        System.out.println("================================================================================");
    }

    // =========================================================================
    // PRACTICAL 1: Tools Setup, Menu Shell, Record & Enum
    // =========================================================================
    private static void demoPractical1() {
        printHeader(1, "Java Program Structure, Record, Enum & Switch Expression");
        BankInfo bankInfo = new BankInfo("MiniBank", "Dhrangadhra");
        System.out.println("Application Header Record : " + bankInfo);
        System.out.println("Record Component Accessors: Bank Name = '" + bankInfo.name() + "', Branch = '" + bankInfo.branch() + "'");

        System.out.println("Enumerated Menu Options   : ");
        for (MenuOption opt : MenuOption.values()) {
            System.out.println("  " + opt.getOptionNumber() + ". " + opt.name() + " -> " + opt.getDisplayName());
        }

        // Switch expression mapping menu choice to placeholder description
        MenuOption sampleOption = MenuOption.DEPOSIT;
        String message = switch (sampleOption) {
            case OPEN_ACCOUNT -> "Open Account — initializes account profile";
            case DEPOSIT -> "Deposit — adds funds to balance";
            case WITHDRAW -> "Withdraw — deducts funds from balance";
            case TRANSFER -> "Transfer — moves funds between accounts";
            case STATEMENT -> "Statement — prints formatted account summary";
            case LIST_ACCOUNTS -> "List Accounts — views all active accounts";
            case BATCH_TRANSACTIONS -> "Batch Transactions — concurrent execution";
            case REPORT -> "Report — end-of-day transaction audit";
            case EXIT -> "Exit — persists state and terminates";
        };
        System.out.println("Switch Expression Dispatch: " + sampleOption + " => \"" + message + "\"");
    }

    // =========================================================================
    // PRACTICAL 2: Encapsulation, Customer & Account Classes
    // =========================================================================
    private static void demoPractical2() {
        printHeader(2, "Classes, Encapsulation, State Control & Static ID Generation");
        Customer c1 = new Customer("Riya Patel", "riya@example.com", "9876543210");
        Customer c2 = new Customer("Harsh Shah", "harsh@example.com", "9898989898");
        System.out.println("Auto-generated Customer 1: ID = " + c1.getCustomerId() + " | Name = " + c1.getName());
        System.out.println("Auto-generated Customer 2: ID = " + c2.getCustomerId() + " | Name = " + c2.getName());

        Account a1 = new SavingsAccount("Riya Patel", 5000, 1000);
        Account a2 = new SavingsAccount("Harsh Shah", 3000, 1000);
        Account a3 = new SavingsAccount("Amit Dave", 2000, 1000);
        Account[] accounts = { a1, a2, a3 };

        System.out.println("\nTesting Encapsulated Deposit & Withdraw on Account: " + a1.getAccountNumber());
        System.out.println("Initial Balance: Rs. " + a1.getBalance());
        a1.DEPOSIT(2000);
        System.out.println("After deposit(2000): Rs. " + a1.getBalance() + " [Expected: 7000]");
        boolean w1 = a1.WITHDRAW(3000);
        System.out.println("Withdraw(3000) result: " + w1 + " | New Balance: Rs. " + a1.getBalance() + " [Expected: 4000]");
        boolean w2 = a1.WITHDRAW(10000);
        System.out.println("Withdraw(10000) result: " + w2 + " (rejected) | Balance remains: Rs. " + a1.getBalance() + " [Expected: 4000]");
    }

    // =========================================================================
    // PRACTICAL 3: Object Methods (toString, equals, hashCode, clone) & Nested Class
    // =========================================================================
    private static void demoPractical3() {
        printHeader(3, "Object Methods, Contracts, Static Nested Address & Cloning");
        Customer customer = new Customer("Riya Patel", "riya@gmail.com", "9876543210");
        Customer.Address addr = new Customer.Address("123 Station Rd", "Rajkot", "360001");
        customer.setAddress(addr);

        System.out.println("Customer with Static Nested Address: " + customer);

        // Cloning
        Customer cloned = customer.clone();
        System.out.println("Cloned Customer Profile           : " + cloned);
        System.out.println("Are customer references distinct? : " + (customer != cloned) + " [Expected: true]");
        System.out.println("Do customer IDs match?            : " + (customer.getCustomerId().equals(cloned.getCustomerId())));

        // toString, equals and hashCode contract on Account
        Account acc1 = new SavingsAccount("Riya Patel", 4000, 1000);
        System.out.println("Account toString() output        : \"" + acc1 + "\"");
        System.out.println("Self equality acc1.equals(acc1)   : " + acc1.equals(acc1));
        System.out.println("instanceof Account check          : " + (acc1 instanceof Account) + " [Expected: true]");
    }

    // =========================================================================
    // PRACTICAL 4: Regex Validation, Command Parsing & Statement Formatter
    // =========================================================================
    private static void demoPractical4() {
        printHeader(4, "Regex Validation Layer, Command Record Parsing & StatementFormatter");
        System.out.println("Mobile '9876543210' valid: " + isValidMobile("9876543210") + " | '12345' valid: " + isValidMobile("12345"));
        System.out.println("Email 'riya@gmail.com' valid: " + isValidEmail("riya@gmail.com") + " | 'bad-email' valid: " + isValidEmail("bad-email"));
        System.out.println("PAN 'ABCDE1234F' valid: " + isValidPan("ABCDE1234F") + " | '12345' valid: " + isValidPan("12345"));
        System.out.println("IFSC 'SBIN0001234' valid: " + isValidIfsc("SBIN0001234") + " | '0000' valid: " + isValidIfsc("0000"));
        System.out.println("Amount '500' valid: " + isValidAmount("500") + " | '-50' valid: " + isValidAmount("-50"));

        Command parsed = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.printf("Parsed Command: Type=%s, AccountNumber=%s, Amount=%d\n",
                parsed.type(), parsed.accountNumber(), parsed.amount());

        Account statementAcc = new SavingsAccount("Riya Patel", 4000, 1000);
        System.out.println("\nGenerated Multi-line Statement (StringBuilder):");
        System.out.println(StatementFormatter.buildStatement(statementAcc));
    }

    // =========================================================================
    // PRACTICAL 5: Polymorphism, Abstract Account & Subclasses
    // =========================================================================
    private static void demoPractical5() {
        printHeader(5, "Inheritance, Abstract Classes & Dynamic Polymorphism");
        Account savings = new SavingsAccount("Riya", 5000, 1000);
        Account current = new CurrentAccount("Harsh", 10000, 5000);
        Account fixedDep = new FixedDepositAccount("Amit", 20000);

        Account[] portfolio = { savings, current, fixedDep };
        System.out.println("Polymorphic Dispatch for interestRate():");
        for (Account a : portfolio) {
            System.out.printf(" - %-20s (%-19s) | Rate: %4.1f%% | canWithdraw(2000): %b\n",
                    a.getOwnerName(), a.getClass().getSimpleName(), a.interestRate(), a.canWithdraw(2000));
        }

        System.out.println("\nPattern Matching with instanceof:");
        for (Account a : portfolio) {
            if (a instanceof SavingsAccount sa) {
                System.out.println("   [Pattern Match] Found SavingsAccount with MinBalance = Rs. " + sa.getMinBalance());
            } else if (a instanceof CurrentAccount ca) {
                System.out.println("   [Pattern Match] Found CurrentAccount with OverdraftLimit = Rs. " + ca.getOverdraftLimit());
            } else if (a instanceof FixedDepositAccount fda) {
                System.out.println("   [Pattern Match] Found FixedDepositAccount (Locked Funds)");
            }
        }
    }

    // =========================================================================
    // PRACTICAL 6: Interfaces, Default Methods, Lambdas & Runnable JAR
    // =========================================================================
    private static void demoPractical6() {
        printHeader(6, "Interface Contracts, Default Methods, Lambdas & Marker Interface");
        Account acc = new SavingsAccount("Riya", 5000, 1000);

        System.out.println("Default Method yearlyInterest(): Rs. " + acc.yearlyInterest());
        System.out.println("Default Method monthlyInterest(): Rs. " + acc.monthlyInterest());

        // Anonymous Class vs Lambda for functional interface WithdrawRule
        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.getBalance() >= amount;
            }
        };
        WithdrawRule lambdaRule = (account, amount) -> account.getBalance() >= amount;

        System.out.println("WithdrawRule via Anonymous Class allow(acc, 2000): " + anonymousRule.allow(acc, 2000));
        System.out.println("WithdrawRule via Lambda Expression allow(acc, 2000): " + lambdaRule.allow(acc, 2000));
        System.out.println("Marker Interface Check (acc instanceof Premium): " + (acc instanceof Premium));
    }

    // =========================================================================
    // PRACTICAL 7: Custom Annotations & Reflection Framework
    // =========================================================================
    private static void demoPractical7() {
        printHeader(7, "Custom Annotations (@Id, @Positive, @MaxLength) & Reflection");
        Account validAcc = new SavingsAccount("ValidUser", 3000, 500);
        String[] validErrors = AnnotationValidator.validate(validAcc);
        System.out.println("Validation on compliant account errors: " + validErrors.length + " (Expected: 0)");

        Account invalidAcc = new SavingsAccount("InvalidUser", -100, 500);
        String[] invalidErrors = AnnotationValidator.validate(invalidAcc);
        System.out.println("Validation on non-compliant account (balance = -100):");
        for (String err : invalidErrors) {
            System.out.println("  [Validation Error via Reflection] " + err);
        }
    }

    // =========================================================================
    // PRACTICAL 8: Custom Exception Hierarchy & Try-With-Resources
    // =========================================================================
    private static void demoPractical8() {
        printHeader(8, "Checked Exception Hierarchy, Shortfall Propagation & AutoCloseable");
        Account acc = new SavingsAccount("Riya", 1000, 500);

        // Try-with-resources with AutoCloseable BankSession
        try (BankSession session = new BankSession("SES-P8-001")) {
            System.out.println("Attempting withdrawal of Rs. 5000 from balance Rs. " + acc.getBalance());
            acc.withdraw(5000);
        } catch (InsufficientFundsException e) {
            System.out.println("Caught InsufficientFundsException: " + e.getMessage());
            System.out.println("Exact Monitored Shortfall: Rs. " + e.getShortfall() + " (Expected: 4000)");
        } catch (InvalidAmountException e) {
            System.out.println("Caught InvalidAmountException: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed. Account balance confirmed safe: Rs. " + acc.getBalance());
        }

        // Test InvalidAmountException on negative deposit
        try {
            acc.deposit(-150);
        } catch (InvalidAmountException e) {
            System.out.println("Caught expected InvalidAmountException on negative deposit: " + e.getMessage());
        }
    }

    // =========================================================================
    // PRACTICAL 9: Multithreading, Thread Lifecycle & Synchronization
    // =========================================================================
    private static void demoPractical9() {
        printHeader(9, "Multithreading, Thread Lifecycle States & Synchronized Concurrency");
        Account sharedAcc = new SavingsAccount("SharedUser", 0, 0);
        int threadCount = 10;
        int depositsPerThread = 1000;
        long amountPerDeposit = 1;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(sharedAcc, depositsPerThread, amountPerDeposit), "Worker-" + (i + 1));
            System.out.printf("Thread %-10s initial state: %s\n", threads[i].getName(), threads[i].getState());
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
                System.out.printf("Thread %-10s finished state: %s\n", t.getName(), t.getState());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        long finalBalance = sharedAcc.getBalance();
        System.out.println("Final Account Balance after 10,000 synchronized deposits: Rs. " + finalBalance + " [Expected: 10000]");
    }

    // =========================================================================
    // PRACTICAL 10: Transaction Engine, Thread Pool, Producer-Consumer & Deadlock Fix
    // =========================================================================
    private static void demoPractical10() {
        printHeader(10, "Managed Thread Pool, wait()/notify() Buffer & Deadlock Resolution");

        // 1. Thread Pool
        System.out.println("--- 10.1 ExecutorService Fixed Thread Pool ---");
        TransactionProcessor processor = new TransactionProcessor(4);
        Account poolAcc = new SavingsAccount("PoolUser", 5000, 0);
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            processor.submit(() -> {
                try {
                    poolAcc.deposit(100);
                    System.out.println(" Task " + taskId + " executed deposit(100) by " + Thread.currentThread().getName());
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            });
        }
        processor.stop();
        System.out.println("Final Pool Balance: Rs. " + poolAcc.getBalance() + " [Expected: 5600]");

        // 2. Producer-Consumer
        System.out.println("\n--- 10.2 Producer-Consumer with wait() / notify() ---");
        TransactionBuffer buffer = new TransactionBuffer(2);
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    Command cmd = new Command(TransactionType.DEPOSIT, "AC0001", i * 100);
                    buffer.produce(cmd);
                    System.out.println(" [Producer] Produced: " + cmd.type() + " Rs. " + cmd.amount());
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "BufferProducer");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    Command cmd = buffer.consume();
                    System.out.println(" [Consumer] Consumed: " + cmd.type() + " Rs. " + cmd.amount());
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "BufferConsumer");

        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. Deadlock Prevention
        System.out.println("\n--- 10.3 Deadlock Resolution (Consistent Lock Ordering) ---");
        Account accA = new SavingsAccount("Alice", 3000, 0);
        Account accB = new SavingsAccount("Bob", 3000, 0);
        System.out.println("Initial Balances: Alice = " + accA.getBalance() + ", Bob = " + accB.getBalance());

        Thread t1 = new Thread(() -> {
            try {
                TransactionProcessor.safeTransfer(accA, accB, 500);
                System.out.println(" Transfer Alice -> Bob (500) completed.");
            } catch (BankException e) {
                System.err.println("Transfer failed: " + e.getMessage());
            }
        }, "Transfer-A-to-B");

        Thread t2 = new Thread(() -> {
            try {
                TransactionProcessor.safeTransfer(accB, accA, 200);
                System.out.println(" Transfer Bob -> Alice (200) completed.");
            } catch (BankException e) {
                System.err.println("Transfer failed: " + e.getMessage());
            }
        }, "Transfer-B-to-A");

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Final Balances: Alice = " + accA.getBalance() + " [Expected: 2700], Bob = " + accB.getBalance() + " [Expected: 3300]");
    }

    // =========================================================================
    // PRACTICAL 11: Persistence, Serialization, Append Log & NIO Report Generator
    // =========================================================================
    private static void demoPractical11() {
        printHeader(11, "Persistence, Serialization, Transaction Logging & NIO Reporting");
        Path stateFile = Path.of("scratch", "accounts.dat");
        Path logFile = Path.of("scratch", "daily_transactions.log");
        Path reportFile = Path.of("scratch", "end_of_day_summary.txt");

        try {
            Account acc1 = new SavingsAccount("Riya", 5000, 1000);
            Account acc2 = new CurrentAccount("Harsh", 10000, 5000);
            Account acc3 = new FixedDepositAccount("Amit", 25000);
            Account[] originalAccounts = { acc1, acc2, acc3 };

            System.out.println("Saving Account[] array to file using ObjectOutputStream...");
            StatePersister.save(originalAccounts, stateFile);
            System.out.println("Loading Account[] back from file using ObjectInputStream...");
            Account[] loadedAccounts = StatePersister.load(stateFile);

            System.out.println("Deserialized Accounts Verification (" + loadedAccounts.length + " accounts):");
            for (Account a : loadedAccounts) {
                System.out.println("  -> Loaded: " + a);
            }

            // Transaction log append
            System.out.println("\nAppending transaction log entries using Files.write (CREATE, APPEND)...");
            TransactionLog.append(logFile, "DEPOSIT " + acc1.getAccountNumber() + " 500");
            TransactionLog.append(logFile, "WITHDRAW " + acc2.getAccountNumber() + " 200");
            TransactionLog.append(logFile, "DEPOSIT " + acc3.getAccountNumber() + " 1000");

            // Report generation via NIO DirectoryStream
            System.out.println("\nGenerating End-of-Day Audit Report via NIO DirectoryStream & BufferedReader:");
            String report = ReportGenerator.generateReport(logFile.getParent(), reportFile);
            System.out.println(report);

            // Cleanup scratch demonstration files
            Files.deleteIfExists(stateFile);
            Files.deleteIfExists(logFile);
            Files.deleteIfExists(reportFile);
        } catch (Exception e) {
            System.err.println("Practical 11 execution error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    // PRACTICAL 12: Collections Framework, Comparable, Comparator & Concurrent Map
    // =========================================================================
    private static void demoPractical12() {
        printHeader(12, "Collections Framework: HashMap, ArrayList, Comparator & Comparable");
        AccountStore store = new AccountStore();
        Account a1 = new SavingsAccount("User1", 5000, 1000);
        Account a2 = new SavingsAccount("User2", 2000, 1000);
        Account a3 = new SavingsAccount("User3", 9000, 1000);

        store.add(a1);
        store.add(a2);
        store.add(a3);

        System.out.println("Lookup by Key get(\"" + a2.getAccountNumber() + "\"): " + store.get(a2.getAccountNumber()));

        // Sorting by balance descending with Comparator
        System.out.println("\nAccounts Sorted by Balance (Descending via Comparator):");
        ArrayList<Account> sorted = store.sortedByBalance();
        for (Account a : sorted) {
            System.out.printf("  %s | %-10s | Balance: Rs. %d\n", a.getAccountNumber(), a.getOwnerName(), a.getBalance());
        }
        System.out.println("Order Verified: " + sorted.get(0).getBalance() + " >= " + sorted.get(1).getBalance() + " >= " + sorted.get(2).getBalance());

        // Natural ordering via Comparable<Account>.compareTo()
        System.out.println("\nNatural Ordering Comparison via Comparable<Account>:");
        int cmp = a1.compareTo(a2);
        System.out.printf("  Comparing %s to %s => result: %d (Order by Account Number)\n",
                a1.getAccountNumber(), a2.getAccountNumber(), cmp);
    }

    // =========================================================================
    // PRACTICAL 13: Generic Repository<T>, Dynamic @Id Reflection & Generics
    // =========================================================================
    private static void demoPractical13() {
        printHeader(13, "Generic Repository<T>, Dynamic @Id Reflection & Bounded Generics");
        Repository<Account> accountRepo = new Repository<>();
        Account a1 = new SavingsAccount("Alpha", 1500, 500);
        Account a2 = new CurrentAccount("Beta", 3500, 1000);
        Account a3 = new FixedDepositAccount("Gamma", 8000);

        System.out.println("Saving accounts using @SafeVarargs saveAll(Account... items)...");
        accountRepo.saveAll(a1, a2, a3);

        System.out.println("findById(\"" + a2.getAccountNumber() + "\") result: " + accountRepo.findById(a2.getAccountNumber()));
        System.out.println("Total stored entities in repository: " + accountRepo.count());
        System.out.println("findAll() entities: " + accountRepo.findAll());

        // Demonstrating reusability with Customer
        System.out.println("\nReusing same Generic Repository<Customer> for Customer entities:");
        Repository<Customer> customerRepo = new Repository<>();
        Customer c1 = new Customer("John Smith", "john@gmail.com", "9876543211");
        Customer c2 = new Customer("Emma Watson", "emma@gmail.com", "9876543212");
        customerRepo.saveAll(c1, c2);
        System.out.println("Found Customer by ID: " + customerRepo.findById(c1.getCustomerId()));
        System.out.println("Total Customers stored: " + customerRepo.count());

        // Bounded Generic Method: <N extends Number> double average(N[] values)
        Integer[] intScores = { 10, 20, 30, 40, 50 };
        Double[] dblRates = { 3.5, 4.0, 7.5 };
        System.out.println("\nBounded Generic Method Repository.average():");
        System.out.println("  Average of Integer[] {10, 20, 30, 40, 50}: " + Repository.average(intScores));
        System.out.println("  Average of Double[]  {3.5, 4.0, 7.5}     : " + Repository.average(dblRates));
    }

    // =========================================================================
    // PRACTICAL 14: BankService Layer, Reflective Audit, Batch Execution & Unit Tests
    // =========================================================================
    private static void demoPractical14() {
        printHeader(14, "Layered Architecture, BankService, Reflective Audit & Automated Tests");
        BankService service = new BankService();

        try {
            System.out.println("--- 14.1 Executing High-Level BankService Operations ---");
            Account acc1 = service.openAccount("SAVINGS", "Riya Patel", 6000, 1000);
            Account acc2 = service.openAccount("CURRENT", "Harsh Shah", 4000, 2000);

            service.deposit(acc1.getAccountNumber(), 1000);
            service.withdraw(acc2.getAccountNumber(), 500);
            service.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 1500);

            System.out.println("\n--- 14.2 Account Statement via Service ---");
            System.out.println(service.statement(acc1.getAccountNumber()));

            System.out.println("\n--- 14.3 Reflective Audit Logging (@Audit Records) ---");
            List<String> auditLogs = AuditLogger.getAuditLogs();
            System.out.println("Total reflective audit entries recorded: " + auditLogs.size());
            for (String log : auditLogs) {
                System.out.println(" " + log);
            }

            System.out.println("\n--- 14.4 Running Automated Unit Test Suite ---");
            TestRunner.runTests(test.BankServiceTest.class);
        } catch (Exception e) {
            System.err.println("Practical 14 execution error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printHeader(int number, String title) {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf(">>> [PRACTICAL %2d] %s\n", number, title);
        System.out.println("--------------------------------------------------------------------------------");
    }
}
