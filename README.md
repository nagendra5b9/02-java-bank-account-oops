# Project 2: OOPs (Java) — Bank Account System

## Overview
Demonstrates all four pillars of Object-Oriented Programming (OOP) in Java:
1. **Abstraction**: Generic `Account` abstract class defines unified contracts (`deposit`, `withdraw`, `printStatement`) without exposing implementation internals.
2. **Encapsulation**: Private fields (`balance`, `accountNumber`, `transactions`) protected against direct tampering, with invariants enforced via methods.
3. **Inheritance**: `SavingsAccount` and `CheckingAccount` derive from `Account`, inheriting core bookkeeping while adding specialized attributes.
4. **Polymorphism**: Dynamic method dispatch on `withdraw()` enforces account-specific validation rules (minimum balance vs. overdraft buffer & penalties).

## Directory Layout
```
02_oops_bank_account_system/
├── src/main/java/com/learning/bank/
│   ├── Account.java
│   ├── SavingsAccount.java
│   ├── CheckingAccount.java
│   ├── Transaction.java
│   ├── BankService.java
│   └── BankApplication.java
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── app.js
└── README.md
```

## Running the Java Application
```bash
# Compile
javac -d bin src/main/java/com/learning/bank/*.java

# Run Driver
java -cp bin com.learning.bank.BankApplication
```
