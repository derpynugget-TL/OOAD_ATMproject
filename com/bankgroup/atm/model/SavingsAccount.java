package com.bankgroup.atm.model;

import com.bankgroup.atm.exception.InsufficientFundsException;
import java.time.LocalDateTime;

/**
 * Owner: Core Banking Logic (You)
 *
 * Rules to implement:
 *  - Enforces a minimum balance (cannot withdraw below it)
 *  - No overdraft
 *  - applyMonthlyUpdate() adds simulated interest
 */
public class SavingsAccount extends Account {

    private static final double MINIMUM_BALANCE = 50.00;
    private static final double MONTHLY_INTEREST_RATE = 0.01; // 1%, adjust as needed

    public SavingsAccount(String accountNumber, double initialBalance) {
        super(accountNumber, initialBalance);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(
                    "Withdrawal denied: balance cannot drop below the required minimum of $"
                            + MINIMUM_BALANCE);
        }

        double newBalance = getBalance() - amount;
        setBalance(newBalance);

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
        double interestEarned = getBalance() * MONTHLY_INTEREST_RATE;
        double newBalance = getBalance() + interestEarned;
        setBalance(newBalance);

        logTransaction(new Transaction(
                generateTransactionId(),
                TransactionType.INTEREST,
                interestEarned,
                LocalDateTime.now(),
                newBalance,
                null
        ));
    }
}
