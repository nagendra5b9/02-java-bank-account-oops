package com.learning.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Abstract Base Class representing a generic Bank Account.
 * Demonstrates: Abstraction, Encapsulation, and Polymorphism foundation.
 */
public abstract class Account {
    private final String accountNumber;
    private final String accountHolder;
    protected double balance;
    private final List<Transaction> transactions;

    public Account(String accountNumber, String accountHolder, double initialDeposit) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (accountHolder == null || accountHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();

        if (initialDeposit > 0) {
            recordTransaction(Transaction.Type.DEPOSIT, initialDeposit, "Initial opening deposit");
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be strictly positive.");
        }
        this.balance += amount;
        recordTransaction(Transaction.Type.DEPOSIT, amount, "Deposit to " + getAccountType());
    }

    /**
     * Polymorphic method implemented according to specific account rules.
     */
    public abstract void withdraw(double amount);

    /**
     * Polymorphic identifier for runtime inspection.
     */
    public abstract String getAccountType();

    protected void recordTransaction(Transaction.Type type, double amount, String description) {
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.transactions.add(new Transaction(txId, type, amount, this.balance, description));
    }

    public void printStatement() {
        System.out.println("===============================================================");
        System.out.printf("STATEMENT: %s (%s) | Holder: %s\n", accountNumber, getAccountType(), accountHolder);
        System.out.printf("Current Cleared Balance: $%.2f\n", balance);
        System.out.println("---------------------------------------------------------------");
        if (transactions.isEmpty()) {
            System.out.println("No transaction activity recorded.");
        } else {
            for (Transaction tx : transactions) {
                System.out.println(tx);
            }
        }
        System.out.println("===============================================================\n");
    }
}
