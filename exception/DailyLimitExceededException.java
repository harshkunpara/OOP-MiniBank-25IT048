package exception;

public class DailyLimitExceededException extends BankException {
    private final long dailyLimit;
    private final long attemptedAmount;

    public DailyLimitExceededException(String message, long dailyLimit, long attemptedAmount) {
        super(message);
        this.dailyLimit = dailyLimit;
        this.attemptedAmount = attemptedAmount;
    }

    public long getDailyLimit() {
        return dailyLimit;
    }

    public long getAttemptedAmount() {
        return attemptedAmount;
    }
}
