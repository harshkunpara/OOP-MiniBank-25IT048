package model;

public enum MenuOption {
    OPEN_ACCOUNT(1, "Open Account"),
    DEPOSIT(2, "Deposit"),
    WITHDRAW(3, "Withdraw"),
    TRANSFER(4, "Transfer"),
    STATEMENT(5, "Account Statement"),
    LIST_ACCOUNTS(6, "List All Accounts"),
    SEARCH_ACCOUNTS(7, "Search Account by Owner Name"),
    BATCH_TRANSACTIONS(8, "Process Batch Transactions"),
    REPORT(9, "End-of-Day Summary Report"),
    WORKING_HOURS(10, "Bank Working Hours"),
    RUN_PRACTICAL_DEMO(11, "Run Practical Demonstrations (1-14)"),
    RUN_TESTS(12, "Run Automated Unit Tests"),
    EXIT(13, "Exit");

    private final int optionNumber;
    private final String displayName;

    MenuOption(int optionNumber, String displayName) {
        this.optionNumber = optionNumber;
        this.displayName = displayName;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MenuOption fromChoice(int choice) {
        for (MenuOption opt : values()) {
            if (opt.optionNumber == choice) {
                return opt;
            }
        }
        return null;
    }
}
