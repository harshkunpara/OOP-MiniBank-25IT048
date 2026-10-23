package model;

public enum MenuOption {
    OPEN_ACCOUNT(1, "Open Account"),
    DEPOSIT(2, "Deposit"),
    WITHDRAW(3, "Withdraw"),
    TRANSFER(4, "Transfer"),
    STATEMENT(5, "Account Statement"),
    LIST_ACCOUNTS(6, "List All Accounts"),
    BATCH_TRANSACTIONS(7, "Process Batch Transactions"),
    REPORT(8, "End-of-Day Summary Report"),
    EXIT(9, "Exit");

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
