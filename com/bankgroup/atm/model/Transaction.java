package com.bankgroup.atm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Transaction {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String HISTORY_HEADER =
            String.format("%-19s  %-13s  %12s  %14s  %s",
                    "DATE / TIME", "TYPE", "AMOUNT", "BALANCE", "NOTE")
            + System.lineSeparator()
            + "-".repeat(78);

    private final String transactionId;     
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final double resultingBalance;   
    private final String relatedAccountNumber; 

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