package test;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.Account;
import service.BankService;
import test.annotation.Test;
import static test.Assertions.assertEquals;
import static test.Assertions.assertNotNull;
import static test.Assertions.assertThrows;
import static test.Assertions.assertTrue;

/**
 * Practical 14: Unit test suite for MiniBank business logic, exception safety,
 * persistence, and concurrency.
 */
public class BankServiceTest {

    @Test
    public void testDepositIncreasesBalance() throws BankException {
        BankService service = new BankService();
        Account acc = service.openAccount("SAVINGS", "John Doe", 1000, 500);
        long initialBalance = acc.getBalance();

        service.deposit(acc.getAccountNumber(), 500);

        assertEquals(initialBalance + 500, acc.getBalance());
        assertEquals(1500, acc.getBalance());
    }

    @Test
    public void testWithdrawalOverLimitThrowsException() throws BankException {
        BankService service = new BankService();
        Account acc = service.openAccount("SAVINGS", "Alice Smith", 2000, 1000);

        // Attempting to withdraw 1500 from 2000 with minBalance=1000 leaves 500 < 1000 -> throws InsufficientFundsException
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(acc.getAccountNumber(), 1500)
        );

        assertNotNull(ex);
        assertEquals(2000, acc.getBalance()); // Balance remains unchanged
    }

    @Test
    public void testNegativeDepositThrowsException() throws BankException {
        BankService service = new BankService();
        Account acc = service.openAccount("CURRENT", "Bob Martin", 5000, 2000);

        assertThrows(
                InvalidAmountException.class,
                () -> service.deposit(acc.getAccountNumber(), -200)
        );

        assertEquals(5000, acc.getBalance());
    }

    @Test
    public void testSafeTransferPreservesTotalBalance() throws BankException {
        BankService service = new BankService();
        Account a1 = service.openAccount("SAVINGS", "User1", 3000, 0);
        Account a2 = service.openAccount("SAVINGS", "User2", 3000, 0);

        long totalBefore = a1.getBalance() + a2.getBalance();

        service.transfer(a1.getAccountNumber(), a2.getAccountNumber(), 1000);

        assertEquals(2000, a1.getBalance());
        assertEquals(4000, a2.getBalance());
        long totalAfter = a1.getBalance() + a2.getBalance();

        assertEquals(totalBefore, totalAfter);
    }

    @Test
    public void testConcurrentDepositsKeepBalanceCorrect() throws Exception {
        BankService service = new BankService();
        long initialBalance = 1000;
        Account acc = service.openAccount("SAVINGS", "ConcurrentTester", initialBalance, 500);

        int threads = 10;
        int depositsPerThread = 500;
        long amountPerDeposit = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.execute(() -> {
                try {
                    for (int j = 0; j < depositsPerThread; j++) {
                        acc.deposit(amountPerDeposit);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        pool.shutdown();

        long expectedBalance = initialBalance + ((long) threads * depositsPerThread * amountPerDeposit); // 1000 + (10 * 500 * 2) = 11000
        assertEquals(expectedBalance, acc.getBalance());
    }

    @Test
    public void testSaveAndLoadStatePersistsData() throws Exception {
        Path tempFile = Path.of("scratch", "test_accounts.dat");
        BankService service1 = new BankService();
        Account savedAcc = service1.openAccount("SAVINGS", "PersistedUser", 7500, 1000);

        service1.saveState(tempFile);
        assertTrue(Files.exists(tempFile));

        BankService service2 = new BankService();
        service2.loadState(tempFile);

        Account loadedAcc = service2.getRepository().findById(savedAcc.getAccountNumber());
        assertNotNull(loadedAcc);
        assertEquals(savedAcc.getAccountNumber(), loadedAcc.getAccountNumber());
        assertEquals(savedAcc.getOwnerName(), loadedAcc.getOwnerName());
        assertEquals(savedAcc.getBalance(), loadedAcc.getBalance());

        Files.deleteIfExists(tempFile);
    }
}
