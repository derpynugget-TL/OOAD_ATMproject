package com.bankgroup.atm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    /** Shared timestamp format so every printed row lines up. */
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Column header for the "view transaction history" screen. */
    public static final String HISTORY_HEADER =
            String.format("%-19s  %-13s  %12s  %14s  %s",
                    "DATE / TIME", "TYPE", "AMOUNT", "BALANCE", "NOTE")
            + System.lineSeparator()
            + "-".repeat(78);

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

    /**
     * One aligned row for the "view transaction history" feature.
     *
     * Money in is shown with a leading '+', money out with a leading '-', so the
     * user can scan the column without reading the type. Transfers append the
     * other account number; everything else leaves that column blank.
     *
     * Example:
     *   2026-09-20 14:03:11  Deposit          +1,000.00       3,500.00
     *   2026-09-20 14:05:42  Transfer Out       -250.00       3,250.00  to 0021-4477
     */
    @Override
    public String toString() {
        String signedAmount = String.format("%s%,.2f",
                type.getSign() > 0 ? "+" : "-", amount);

        String note = "";
        if (relatedAccountNumber != null && !relatedAccountNumber.isBlank()) {
            note = (type == TransactionType.TRANSFER_OUT ? "to " : "from ")
                    + relatedAccountNumber;
        }

        return String.format("%-19s  %-13s  %12s  %,14.2f  %s",
                timestamp.format(TIMESTAMP_FORMAT),
                type.getLabel(),
                signedAmount,
                resultingBalance,
                note).stripTrailing();
    }

    /**
     * Fuller single-transaction view, e.g. for a printed receipt or when the
     * user drills into one row. Includes the ID, which the compact row omits.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction ID : ").append(transactionId).append(System.lineSeparator());
        sb.append("Date / Time    : ").append(timestamp.format(TIMESTAMP_FORMAT)).append(System.lineSeparator());
        sb.append("Type           : ").append(type.getLabel()).append(System.lineSeparator());
        sb.append(String.format("Amount         : %s%,.2f%n",
                type.getSign() > 0 ? "+" : "-", amount));
        sb.append(String.format("Balance After  : %,.2f%n", resultingBalance));
        if (relatedAccountNumber != null && !relatedAccountNumber.isBlank()) {
            sb.append(type == TransactionType.TRANSFER_OUT ? "To Account     : " : "From Account   : ")
              .append(relatedAccountNumber).append(System.lineSeparator());
        }
        return sb.toString();
    }
}