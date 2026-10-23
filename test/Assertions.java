package test;

import java.util.Objects;

/**
 * Standard test assertions for Practical 14 test suite.
 */
public class Assertions {

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", but got: " + actual);
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected: " + expected + ", but got: " + actual);
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true, but was false");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected condition to be false, but was true");
        }
    }

    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected non-null object, but was null");
        }
    }

    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return expectedType.cast(actual);
            }
            throw new AssertionError("Expected exception " + expectedType.getName() + " but caught " + actual.getClass().getName(), actual);
        }
        throw new AssertionError("Expected exception " + expectedType.getName() + " was not thrown.");
    }
}
