package com.learning.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encapsulates immutable record of an individual bank transaction.
 * Demonstrates: Encapsulation, final fields, and formatting.
 */
public class Transaction {
    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT }

    private final String transactionId;
    private final LocalDateTime timestamp;
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String description;

    public Transaction(String transactionId, Type type, double amount, double balanceAfter, String description) {
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public String getTransactionId() { return transactionId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %-12s | Amount: $%8.2f | Balance: $%8.2f | %s",
                timestamp.format(fmt), type, amount, balanceAfter, description);
    }
}
