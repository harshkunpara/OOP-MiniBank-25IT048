package util;

public class BankSession implements AutoCloseable {
    private final String sessionId;

    public BankSession(String sessionId) {
        this.sessionId = sessionId;
        System.out.println("[BankSession] Session " + sessionId + " opened.");
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void close() {
        System.out.println("[BankSession] Session " + sessionId + " closed safely.");
    }
}
