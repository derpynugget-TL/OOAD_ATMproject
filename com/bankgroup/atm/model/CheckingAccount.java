package com.bankgroup.atm.model;

import com.bankgroup.atm.exception.InsufficientFundsException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Owner: Core Banking Logic (You)
 *
 * Rules to implement:
 *  - Allows a small overdraft (unlike SavingsAccount)
 *  - Enforces a daily withdrawal limit (core requirement #4)
 *  - applyMonthlyUpdate() could apply a maintenance fee, or do nothing
 */
public class CheckingAccount extends Account {

    private static final double OVERDRAFT_LIMIT = 100.00;
    private static final double DAILY_WITHDRAWAL_LIMIT = 500.00;

    // Running total of today's withdrawals, plus the date it applies to.
    // When withdraw() sees a different date than lastWithdrawalDate, it
    // resets withdrawnToday to 0 before checking the limit. null means
    // "no withdrawal yet" so the very first one always resets cleanly.
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
            // First withdrawal of a new day — reset the running total.
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
        // Design decision: this CheckingAccount implementation charges no
        // maintenance fee and earns no interest, unlike SavingsAccount.
        // Left as an intentional no-op rather than an unexplained empty
        // method — worth a line in the report under "design decisions."
    }
}
