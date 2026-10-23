package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import model.Account;

/**
 * Practical 12: Account storage using the Java Collections Framework (HashMap, ArrayList, Comparator).
 */
public class AccountStore {
    private final Map<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    public void add(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
        }
    }

    public Account get(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void recordTransaction(String transaction) {
        if (transaction != null) {
            transactionHistory.add(transaction);
        }
    }

    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    /**
     * Returns accounts sorted by balance descending (highest balance first) using a Comparator.
     */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sortedList = new ArrayList<>(accounts.values());
        sortedList.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return sortedList;
    }

    // Supplementary: Returns all accounts with balance greater than or equal to threshold
    public List<Account> getAccountsWithBalanceAbove(long threshold) {
        List<Account> filtered = new ArrayList<>();
        for (Account acc : accounts.values()) {
            if (acc.getBalance() >= threshold) {
                filtered.add(acc);
            }
        }
        return filtered;
    }

    // Supplementary: Returns accounts in a TreeMap to preserve natural key order (accountNumber)
    public TreeMap<String, Account> toTreeMap() {
        return new TreeMap<>(accounts);
    }

    public int size() {
        return accounts.size();
    }
}
