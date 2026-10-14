package com.learning.bank;

/**
 * Represents an interest-earning Savings Account with minimum balance constraint.
 * Demonstrates: Inheritance (extends Account) and Polymorphic method overriding.
 */
public class SavingsAccount extends Account {
    private final double annualInterestRate; // e.g. 0.045 for 4.5%
    private final double minimumBalance;

    public SavingsAccount(String accountNumber, String accountHolder, double initialDeposit, double annualInterestRate, double minimumBalance) {
        super(accountNumber, accountHolder, initialDeposit);
        this.annualInterestRate = annualInterestRate;
        this.minimumBalance = minimumBalance;
    }

    @Override
    public synchronized void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if ((balance - amount) < minimumBalance) {
            throw new IllegalStateException(String.format(
                "Withdrawal denied! Minimum balance of $%.2f must be maintained. Current: $%.2f",
                minimumBalance, balance));
        }
        this.balance -= amount;
        recordTransaction(Transaction.Type.WITHDRAWAL, amount, "Savings withdrawal (Min balance: $" + minimumBalance + ")");
    }

    public synchronized void applyMonthlyInterest() {
        double monthlyInterest = balance * (annualInterestRate / 12.0);
        if (monthlyInterest > 0) {
            this.balance += monthlyInterest;
            recordTransaction(Transaction.Type.DEPOSIT, monthlyInterest,
                    String.format("Monthly Interest Credit (%.2f%% p.a.)", annualInterestRate * 100));
        }
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public double getAnnualInterestRate() { return annualInterestRate; }
    public double getMinimumBalance() { return minimumBalance; }
}
