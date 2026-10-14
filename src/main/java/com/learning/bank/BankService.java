package com.learning.bank;

import java.util.HashMap;
import java.util.Map;

/**
 * High-level Banking Service coordinating accounts and inter-account transfers.
 * Demonstrates: Service layer separation and transactional integrity.
 */
public class BankService {
    private final Map<String, Account> accounts = new HashMap<>();

    public void registerAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return acc;
    }

    public synchronized void transferFunds(String fromAccNum, String toAccNum, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (fromAccNum.equals(toAccNum)) {
            throw new IllegalArgumentException("Source and destination accounts cannot be identical.");
        }

        Account source = getAccount(fromAccNum);
        Account destination = getAccount(toAccNum);

        // Perform atomic withdrawal from source
        source.withdraw(amount);

        // Perform deposit to destination
        destination.deposit(amount);

        System.out.printf(">> SUCCESS: Transferred $%.2f from [%s] to [%s]\n", amount, fromAccNum, toAccNum);
    }
}
