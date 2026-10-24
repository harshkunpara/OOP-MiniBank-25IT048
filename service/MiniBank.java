package service;

import exception.BankException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Account;
import model.BankInfo;
import model.Command;
import model.MenuOption;
import model.TransactionType;

/**
 * Practical 1 & 14: Interactive Console Banking Application.
 * Drives all banking operations through BankService, persists state across restarts,
 * and executes concurrent batches and audit reports.
 */
public class MiniBank {
    private static final Path DATA_FILE = Path.of("data", "accounts.dat");
    private static final Path LOGS_DIR = Path.of("logs");
    private static final Path REPORT_FILE = Path.of("data", "end_of_day_report.txt");

    public static void main(String[] args) {
        BankInfo bankInfo = new BankInfo("MiniBank", "Dhrangadhra");

        // CLI argument handling for non-interactive execution
        if (args != null && args.length > 0) {
            String command = args[0].trim().toLowerCase();
            switch (command) {
                case "test", "--test" -> {
                    test.TestRunner.runTests(test.BankServiceTest.class);
                    return;
                }
                case "demo", "--demo" -> {
                    if (args.length > 1) {
                        try {
                            int pNum = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
                            Main.runPractical(pNum);
                        } catch (NumberFormatException e) {
                            Main.runAll();
                        }
                    } else {
                        Main.runAll();
                    }
                    return;
                }
                case "hours", "--hours" -> {
                    printWorkingHours(bankInfo);
                    return;
                }
            }
        }

        System.out.println("==================================================");
        System.out.println("   " + bankInfo.name() + " — Branch: " + bankInfo.branch());
        System.out.println("   Enterprise Banking System | Practicals 1 - 14  ");
        System.out.println("==================================================");

        BankService bankService = new BankService();

        // Practical 11 & 14: Load existing state on startup
        try {
            bankService.loadState(DATA_FILE);
            int count = bankService.listAccounts().size();
            if (count > 0) {
                System.out.println("[State Restored] Successfully loaded " + count + " accounts from " + DATA_FILE);
            }
        } catch (Exception e) {
            System.out.println("[Info] No previous state found. Initializing fresh storage.");
        }

        Scanner sc = new Scanner(System.in);

        try {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Enter choice (1-13): ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    sc.next();
                    continue;
                }

                int choice = sc.nextInt();
                sc.nextLine(); // consume trailing newline

                MenuOption option = MenuOption.fromChoice(choice);
                if (option == null) {
                    System.out.println("Invalid choice! Please select an option between 1 and 13.");
                    continue;
                }

                switch (option) {
                    case OPEN_ACCOUNT -> handleOpenAccount(sc, bankService);
                    case DEPOSIT -> handleDeposit(sc, bankService);
                    case WITHDRAW -> handleWithdraw(sc, bankService);
                    case TRANSFER -> handleTransfer(sc, bankService);
                    case STATEMENT -> handleStatement(sc, bankService);
                    case LIST_ACCOUNTS -> handleListAccounts(bankService);
                    case SEARCH_ACCOUNTS -> handleSearchAccounts(sc, bankService);
                    case BATCH_TRANSACTIONS -> handleBatchTransactions(bankService);
                    case REPORT -> handleReport(bankService);
                    case WORKING_HOURS -> printWorkingHours(bankInfo);
                    case RUN_PRACTICAL_DEMO -> handleRunPracticalDemo(sc);
                    case RUN_TESTS -> test.TestRunner.runTests(test.BankServiceTest.class);
                    case EXIT -> {
                        running = false;
                        System.out.println("\nSaving application state before exit...");
                        try {
                            bankService.saveState(DATA_FILE);
                            System.out.println("[Persistence] State saved successfully to " + DATA_FILE);
                        } catch (IOException e) {
                            System.err.println("[Error] Failed to save state: " + e.getMessage());
                        }
                        bankService.getTransactionProcessor().stop();
                        System.out.println("Thank you for using " + bankInfo.name() + " of " + bankInfo.branch() + ". Goodbye!");
                    }
                }
            }
        } finally {
            sc.close();
        }
    }

    private static void printMenu() {
        System.out.println("\n================ MAIN MENU ================");
        for (MenuOption opt : MenuOption.values()) {
            System.out.printf(" %d. %s\n", opt.getOptionNumber(), opt.getDisplayName());
        }
        System.out.println("===========================================");
    }

    private static void handleOpenAccount(Scanner sc, BankService service) {
        System.out.println("\n--- [1] Open Account ---");
        System.out.print("Account Type (SAVINGS / CURRENT / FD): ");
        String type = sc.nextLine().trim();

        System.out.print("Account Holder Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Initial Deposit Amount (Rs): ");
        long initialDeposit = readLong(sc);

        long extra = 0;
        if ("SAVINGS".equalsIgnoreCase(type)) {
            System.out.print("Minimum Balance Requirement (default 1000): ");
            extra = readLong(sc);
        } else if ("CURRENT".equalsIgnoreCase(type)) {
            System.out.print("Overdraft Limit (default 5000): ");
            extra = readLong(sc);
        }

        try {
            Account acc = service.openAccount(type, name, initialDeposit, extra);
            System.out.println("[SUCCESS] Account Created Successfully!");
            System.out.println("  Account Number : " + acc.getAccountNumber());
            System.out.println("  Owner Name     : " + acc.getOwnerName());
            System.out.println("  Balance        : Rs. " + acc.getBalance());
            System.out.println("  Interest Rate  : " + acc.interestRate() + "%");
        } catch (BankException e) {
            System.err.println("[FAILED] Could not open account: " + e.getMessage());
        }
    }

    private static void handleDeposit(Scanner sc, BankService service) {
        System.out.println("\n--- [2] Deposit Funds ---");
        System.out.print("Account Number (e.g. AC0001): ");
        String accNum = sc.nextLine().trim();

        System.out.print("Deposit Amount (Rs): ");
        long amount = readLong(sc);

        try {
            service.deposit(accNum, amount);
            System.out.println("[SUCCESS] Successfully deposited Rs. " + amount + " into " + accNum);
        } catch (BankException e) {
            System.err.println("[FAILED] Deposit failed: " + e.getMessage());
        }
    }

    private static void handleWithdraw(Scanner sc, BankService service) {
        System.out.println("\n--- [3] Withdraw Funds ---");
        System.out.print("Account Number (e.g. AC0001): ");
        String accNum = sc.nextLine().trim();

        System.out.print("Withdrawal Amount (Rs): ");
        long amount = readLong(sc);

        try {
            service.withdraw(accNum, amount);
            System.out.println("[SUCCESS] Successfully withdrew Rs. " + amount + " from " + accNum);
        } catch (BankException e) {
            System.err.println("[FAILED] Withdrawal failed: " + e.getMessage());
        }
    }

    private static void handleTransfer(Scanner sc, BankService service) {
        System.out.println("\n--- [4] Transfer Funds ---");
        System.out.print("Source Account Number (From): ");
        String fromAcc = sc.nextLine().trim();

        System.out.print("Target Account Number (To): ");
        String toAcc = sc.nextLine().trim();

        System.out.print("Transfer Amount (Rs): ");
        long amount = readLong(sc);

        try {
            service.transfer(fromAcc, toAcc, amount);
            System.out.println("[SUCCESS] Transferred Rs. " + amount + " from " + fromAcc + " to " + toAcc);
        } catch (BankException e) {
            System.err.println("[FAILED] Transfer failed: " + e.getMessage());
        }
    }

    private static void handleStatement(Scanner sc, BankService service) {
        System.out.println("\n--- [5] Account Statement ---");
        System.out.print("Account Number: ");
        String accNum = sc.nextLine().trim();

        try {
            String statement = service.statement(accNum);
            System.out.println(statement);
        } catch (BankException e) {
            System.err.println("[FAILED] Could not generate statement: " + e.getMessage());
        }
    }

    private static void handleListAccounts(BankService service) {
        System.out.println("\n--- [6] Registered Accounts List ---");
        List<Account> accounts = service.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered yet.");
            return;
        }
        System.out.printf("%-10s | %-20s | %-12s | %-10s\n", "Acc Number", "Owner", "Balance", "Type");
        System.out.println("------------------------------------------------------------");
        for (Account a : accounts) {
            System.out.printf("%-10s | %-20s | Rs. %-8d | %-10s\n",
                    a.getAccountNumber(), a.getOwnerName(), a.getBalance(), a.getClass().getSimpleName());
        }
    }

    private static void handleBatchTransactions(BankService service) {
        System.out.println("\n--- [7] Concurrent Batch Transaction Processing (Practical 10 & 14) ---");
        List<Account> accounts = service.listAccounts();
        if (accounts.size() < 2) {
            System.out.println("Creating sample accounts to run the batch processing demonstration...");
            try {
                service.openAccount("SAVINGS", "BatchUser1", 5000, 1000);
                service.openAccount("SAVINGS", "BatchUser2", 5000, 1000);
                accounts = service.listAccounts();
            } catch (BankException e) {
                System.err.println("Failed to create demo accounts: " + e.getMessage());
                return;
            }
        }

        String acc1 = accounts.get(0).getAccountNumber();
        String acc2 = accounts.get(1).getAccountNumber();

        List<Command> batch = new ArrayList<>();
        batch.add(new Command(TransactionType.DEPOSIT, acc1, 250));
        batch.add(new Command(TransactionType.DEPOSIT, acc1, 150));
        batch.add(new Command(TransactionType.DEPOSIT, acc2, 300));
        batch.add(new Command(TransactionType.WITHDRAW, acc2, 100));

        System.out.println("Submitting " + batch.size() + " concurrent transactions to managed thread pool...");
        try {
            service.processBatch(batch);
            System.out.println("[SUCCESS] Concurrent batch processing completed safely without race conditions.");
        } catch (InterruptedException e) {
            System.err.println("[FAILED] Batch processing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static void handleReport(BankService service) {
        System.out.println("\n--- [8] End-of-Day Audit Report (Practical 11 & 14) ---");
        try {
            String report = service.generateReport(LOGS_DIR, REPORT_FILE);
            System.out.println(report);
            System.out.println("[Report Saved] Written to: " + REPORT_FILE.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[FAILED] Report generation failed: " + e.getMessage());
        }
    }

    private static void handleSearchAccounts(Scanner sc, BankService service) {
        System.out.println("\n--- [7] Search Account by Owner Name (Practical 14 Supplementary) ---");
        System.out.print("Enter owner name or keyword: ");
        String query = sc.nextLine().trim();

        List<Account> matches = service.searchByOwnerName(query);
        if (matches.isEmpty()) {
            System.out.println("No matching accounts found for: \"" + query + "\"");
            return;
        }
        System.out.printf("Found %d matching account(s):\n", matches.size());
        System.out.printf("%-10s | %-20s | %-12s | %-10s\n", "Acc Number", "Owner", "Balance", "Type");
        System.out.println("------------------------------------------------------------");
        for (Account a : matches) {
            System.out.printf("%-10s | %-20s | Rs. %-8d | %-10s\n",
                    a.getAccountNumber(), a.getOwnerName(), a.getBalance(), a.getClass().getSimpleName());
        }
    }

    private static void printWorkingHours(BankInfo info) {
        System.out.println("\n--- [10] Bank Working Hours (Practical 1 Supplementary) ---");
        System.out.println("Branch: " + info.branch() + " (" + info.name() + ")");
        System.out.println("--------------------------------------------------");
        System.out.println("Monday - Friday    : 09:30 AM – 04:30 PM");
        System.out.println("Saturday           : 09:30 AM – 01:30 PM (1st & 3rd Saturdays)");
        System.out.println("Sunday & Holidays  : Closed");
        System.out.println("ATM / Net Banking  : 24 Hours / 7 Days Available");
        System.out.println("--------------------------------------------------");
    }

    private static void handleRunPracticalDemo(Scanner sc) {
        System.out.println("\n--- [11] Run Practical Demonstrations (Practicals 1 - 14) ---");
        System.out.print("Enter Practical Number (1-14) or 'all': ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("all") || input.isEmpty()) {
            Main.runAll();
        } else {
            try {
                int pNum = Integer.parseInt(input);
                Main.runPractical(pNum);
            } catch (NumberFormatException e) {
                System.out.println("Invalid practical number entered. Running all practicals.");
                Main.runAll();
            }
        }
    }

    private static long readLong(Scanner sc) {
        while (!sc.hasNextLong()) {
            System.out.print("Invalid amount. Enter a numeric value: ");
            sc.next();
        }
        long val = sc.nextLong();
        sc.nextLine(); // consume newline
        return val;
    }
}
