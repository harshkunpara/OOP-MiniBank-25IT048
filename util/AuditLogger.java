package util;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.annotation.Audit;

/**
 * Practical 14 (Optional Requirement 5): Reflective Audit Logger that inspects
 * methods annotated with @Audit and records invocation telemetry.
 */
public class AuditLogger {
    private static final List<String> AUDIT_LOGS = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void logIfAudited(Class<?> clazz, String methodName, Object... args) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                    Audit audit = method.getAnnotation(Audit.class);
                    String action = audit.action().isEmpty() ? method.getName().toUpperCase() : audit.action();
                    String timestamp = LocalDateTime.now().format(FORMATTER);
                    StringBuilder argStr = new StringBuilder();
                    if (args != null && args.length > 0) {
                        for (int i = 0; i < args.length; i++) {
                            if (i > 0) argStr.append(", ");
                            argStr.append(args[i]);
                        }
                    }
                    String entry = String.format("[%s] [AUDIT] Action: %s | Method: %s.%s(%s)",
                            timestamp, action, clazz.getSimpleName(), methodName, argStr);
                    AUDIT_LOGS.add(entry);
                    System.out.println(entry);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Audit logging failure: " + e.getMessage());
        }
    }

    public static synchronized List<String> getAuditLogs() {
        return Collections.unmodifiableList(new ArrayList<>(AUDIT_LOGS));
    }

    public static synchronized void clear() {
        AUDIT_LOGS.clear();
    }
}
