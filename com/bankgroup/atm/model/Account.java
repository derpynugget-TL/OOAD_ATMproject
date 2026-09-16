package com.bankgroup.atm.model;

import com.bankgroup.atm.exception.InsufficientFundsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Owner: Core Banking Logic (You)
 *
 * Abstract base class -> satisfies the "abstraction" and "inheritance"
 * mandatory requirements. SavingsAccount and CheckingAccount extend this.
 *
 * All fields are private with controlled access -> satisfies "encapsulation".
 */
public abstract class Account {

    private final String accountNumber;
    private double balance;
    private final List<Transaction> transactionHistory = new ArrayList<>();

    protected Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        // Unmodifiable view: callers can read history but can't add/remove
        // transactions directly, which would bypass logTransaction() and
        // break the audit trail. This is what "controlled access" means in
        // practice for a collection field, not just for a single value.
        return Collections.unmodifiableList(transactionHistory);
    }

    /**
     * Adds funds to the account. Kept concrete here since deposit rules
     * are the same across account types (unlike withdraw).
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        double newBalance = balance + amount;
        setBalance(newBalance);

        logTransaction(new Transaction(
                generateTransactionId(),
                TransactionType.DEPOSIT,
                amount,
                LocalDateTime.now(),
                newBalance,
                null // no related account for a plain deposit
        ));
    }

    /**
     * Shared by deposit() here and by withdraw() in each subclass, so both
     * account types generate IDs the same way instead of duplicating logic.
     */
    protected String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Withdraws funds. Declared abstract so each account type can enforce
     * its own rules (e.g. CheckingAccount allows overdraft, SavingsAccount
     * enforces a minimum balance) -> this is where POLYMORPHISM is demonstrated.
     *
     * @throws InsufficientFundsException if the withdrawal violates the
     *         account's rules (insufficient balance, below minimum, over daily limit)
     */
    public abstract void withdraw(double amount) throws InsufficientFundsException;

    /**
     * Called periodically (e.g. simulated monthly) — another polymorphism hook.
     * SavingsAccount applies interest; CheckingAccount might do nothing or
     * check for a maintenance fee.
     */
    public abstract void applyMonthlyUpdate();

    /**
     * Protected so subclasses can adjust the balance directly after they've
     * validated their own rules, without exposing a public mutator.
     */
    protected void setBalance(double newBalance) {
        this.balance = newBalance;
    }

    protected void logTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}
