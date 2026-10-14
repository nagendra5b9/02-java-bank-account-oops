package com.learning.bank;

/**
 * Driver program demonstrating Object-Oriented Programming (OOP) in Java:
 * 1. Abstraction: Abstract Account class with standardized interface.
 * 2. Encapsulation: Private state mutated strictly through validated methods.
 * 3. Inheritance: SavingsAccount & CheckingAccount extend Account.
 * 4. Polymorphism: Dynamic method dispatch for withdraw() and getAccountType().
 */
public class BankApplication {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("        JAVA OOPS: BANK ACCOUNT SYSTEM            ");
        System.out.println("==================================================");

        BankService bank = new BankService();

        // Instantiate polymorphic objects
        Account savings = new SavingsAccount("SA-1001", "Alice Vance", 1500.00, 0.045, 200.00);
        Account checking = new CheckingAccount("CA-2002", "Bob Smith", 500.00, 300.00, 25.00);

        bank.registerAccount(savings);
        bank.registerAccount(checking);

        System.out.println("1. Performing standard deposits & withdrawals...");
        savings.deposit(350.00);
        checking.deposit(200.00);

        System.out.println("\n2. Testing Savings Minimum Balance Rule...");
        try {
            savings.withdraw(1800.00); // Exceeds balance - minBalance constraint
        } catch (IllegalStateException e) {
            System.out.println(">> Caught expected rule exception: " + e.getMessage());
        }

        System.out.println("\n3. Testing Checking Overdraft Privilege...");
        // Balance is $700. Withdraw $850 (uses $150 of $300 overdraft)
        checking.withdraw(850.00);

        System.out.println("\n4. Testing Inter-Account Funds Transfer...");
        savings.withdraw(200.00);
        checking.deposit(200.00);

        System.out.println("\n5. Applying Monthly Interest on Savings...");
        ((SavingsAccount) savings).applyMonthlyInterest();

        System.out.println("\n6. Generating Final Account Statements:");
        savings.printStatement();
        checking.printStatement();
    }
}
