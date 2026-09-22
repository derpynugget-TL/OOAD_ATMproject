package com.bankgroup.atm.model;

import com.bankgroup.atm.exception.InsufficientFundsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        return Collections.unmodifiableList(transactionHistory);
    }

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

    protected String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    public abstract void withdraw(double amount) throws InsufficientFundsException;

    public abstract void applyMonthlyUpdate();

    protected void setBalance(double newBalance) {
        this.balance = newBalance;
    }

    protected void logTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}
