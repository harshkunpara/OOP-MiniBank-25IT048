package test;

import java.lang.reflect.Method;
import test.annotation.Test;

/**
 * Practical 14: Reflective Test Runner discovering and executing @Test methods.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("          MINIBANK AUTOMATED UNIT TEST SUITE      ");
        System.out.println("==================================================");

        runTests(BankServiceTest.class);
    }

    public static void runTests(Class<?> testClass) {
        System.out.println("\nRunning test cases in class: " + testClass.getSimpleName());
        System.out.println("--------------------------------------------------");

        int total = 0;
        int passed = 0;
        int failed = 0;

        Method[] methods = testClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                total++;
                String testName = method.getName();
                try {
                    Object instance = testClass.getDeclaredConstructor().newInstance();
                    method.setAccessible(true);
                    method.invoke(instance);
                    passed++;
                    System.out.printf("  [PASS] %-45s [OK]\n", testName);
                } catch (Throwable t) {
                    failed++;
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    System.out.printf("  [FAIL] %-45s [FAILED]\n", testName);
                    System.out.println("         Reason: " + cause.getMessage());
                }
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("Test Summary: Total: %d | Passed: %d | Failed: %d\n", total, passed, failed);
        if (failed == 0) {
            System.out.println("RESULT: ALL UNIT TESTS PASSED SUCCESSFULLY! (100% PASS RATE)");
        } else {
            System.out.println("RESULT: SOME UNIT TESTS FAILED!");
        }
        System.out.println("==================================================");
    }
}
