package com.bankgroup.atm.model;

import com.bankgroup.atm.exception.InsufficientFundsException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CheckingAccount extends Account {

    private static final double OVERDRAFT_LIMIT = 100.00;
    private static final double DAILY_WITHDRAWAL_LIMIT = 500.00;
    private double withdrawnToday = 0.0;
    private LocalDate lastWithdrawalDate = null;

    public CheckingAccount(String accountNumber, double initialBalance) {
        super(accountNumber, initialBalance);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        LocalDate today = LocalDate.now();
        if (!today.equals(lastWithdrawalDate)) {
            withdrawnToday = 0.0;
            lastWithdrawalDate = today;
        }

        if (withdrawnToday + amount > DAILY_WITHDRAWAL_LIMIT) {
            throw new InsufficientFundsException(
                    "Withdrawal denied: exceeds the daily withdrawal limit of $"
                            + DAILY_WITHDRAWAL_LIMIT);
        }

        if (getBalance() - amount < -OVERDRAFT_LIMIT) {
            throw new InsufficientFundsException(
                    "Withdrawal denied: exceeds the overdraft limit of $" + OVERDRAFT_LIMIT);
        }

        double newBalance = getBalance() - amount;
        setBalance(newBalance);
        withdrawnToday += amount;

        logTransaction(new Transaction(
                generateTransactionId(),
                TransactionType.WITHDRAWAL,
                amount,
                LocalDateTime.now(),
                newBalance,
                null
        ));
    }

    @Override
    public void applyMonthlyUpdate() {
        // No monthly update required for CheckingAccount
    }
}
