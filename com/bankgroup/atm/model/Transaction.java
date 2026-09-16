package com.bankgroup.atm.model;

import java.time.LocalDateTime;

/**
 * Owner: Data & Testing (Member 3)
 *
 * This is the SHARED CONTRACT between Core Banking Logic and Data & Testing.
 * Core Banking Logic (Account.deposit/withdraw) will construct one of these
 * every time money moves, so the fields below should not change without
 * telling the whole team.
 *
 * Kept immutable (no setters) since a transaction record shouldn't be edited
 * after the fact — that's good practice for an audit trail.
 */
public class Transaction {

    private final String transactionId;      // e.g. UUID or incrementing counter
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final double resultingBalance;    // balance AFTER this transaction applied
    private final String relatedAccountNumber; // for transfers: the other account; null otherwise

    public Transaction(String transactionId, TransactionType type, double amount,
                        LocalDateTime timestamp, double resultingBalance,
                        String relatedAccountNumber) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.resultingBalance = resultingBalance;
        this.relatedAccountNumber = relatedAccountNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getResultingBalance() {
        return resultingBalance;
    }

    public String getRelatedAccountNumber() {
        return relatedAccountNumber;
    }

    @Override
    public String toString() {
        // TODO(Member 3): format nicely for the "view transaction history" feature
        return timestamp + " | " + type + " | " + amount + " | balance: " + resultingBalance;
    }
}
