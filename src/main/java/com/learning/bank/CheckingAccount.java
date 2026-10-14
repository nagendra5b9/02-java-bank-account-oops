package com.learning.bank;

/**
 * Represents a Checking Account with overdraft privilege and overdraft fee.
 * Demonstrates: Inheritance, specialized business rules, and polymorphism.
 */
public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double overdraftFee;

    public CheckingAccount(String accountNumber, String accountHolder, double initialDeposit, double overdraftLimit, double overdraftFee) {
        super(accountNumber, accountHolder, initialDeposit);
        this.overdraftLimit = overdraftLimit;
        this.overdraftFee = overdraftFee;
    }

    @Override
    public synchronized void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        double available = balance + overdraftLimit;
        if (amount > available) {
            throw new IllegalStateException(String.format(
                "Transaction declined! Exceeds overdraft capacity. Max available: $%.2f", available));
        }

        this.balance -= amount;
        recordTransaction(Transaction.Type.WITHDRAWAL, amount, "Checking debit/withdrawal");

        // Apply overdraft fee if account drops into negative balance
        if (this.balance < 0) {
            this.balance -= overdraftFee;
            recordTransaction(Transaction.Type.WITHDRAWAL, overdraftFee,
                    String.format("Overdraft Penalty Fee Applied ($%.2f)", overdraftFee));
            System.out.printf(">> WARNING: Overdraft activated! Fee of $%.2f deducted.\n", overdraftFee);
        }
    }

    @Override
    public String getAccountType() {
        return "Checking Account";
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public double getOverdraftFee() { return overdraftFee; }
}
