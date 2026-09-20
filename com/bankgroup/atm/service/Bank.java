package com.bankgroup.atm.service;

import com.bankgroup.atm.exception.AccountNotFoundException;
import com.bankgroup.atm.exception.InsufficientFundsException;
import com.bankgroup.atm.exception.InvalidPinException;
import com.bankgroup.atm.model.Account;
import com.bankgroup.atm.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Owner: Shared — mainly You (Team Lead) since this is where everyone's
 * pieces meet. Uses HashMap -> satisfies the "collections" requirement.
 *
 * This is the class the ATM (console controller) will talk to. Keep the
 * method signatures below stable once agreed, since Member 2 and Member 3
 * will both call into this.
 */
public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    public void addAccount(Account account, User user) {
        accounts.put(account.getAccountNumber(), account);
        users.put(user.getAccountNumber(), user);
    }

    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("No account found: " + accountNumber);
        }
        return account;
    }

    /**
     * Owner: Auth & Security (Member 2) implements the body.
     * Team Lead just needs this signature to build the ATM login flow against.
     */
    public User login(String accountNumber, String pin)
            throws AccountNotFoundException, InvalidPinException {
        User user = users.get(accountNumber);

        if (user == null) {
            throw new AccountNotFoundException("Account not found.");
        }

        if (user.isLocked()) {
            throw new InvalidPinException("Account is locked.");
        }

        if (!user.checkPin(pin)) {
            user.registerFailedAttempt();

            if (user.isLocked()) {
                throw new InvalidPinException(
                        "Invalid PIN. Account has been locked after 3 failed attempts.");
            }

            throw new InvalidPinException("Invalid PIN.");
        }
        

        user.resetFailedAttempts();
        return user;

    }

    /**
     * Owner: You (Core Banking Logic).
     * Moves funds between two accounts within the bank.
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);

        // Resolves the earlier open question about a partial failure between
        // withdraw and deposit: withdraw() already validates amount > 0 and
        // enforces whichever account-specific rule applies (minimum balance
        // for Savings, overdraft/daily limit for Checking) before it changes
        // anything. If it throws, execution never reaches deposit(), so the
        // transfer either fully happens or doesn't happen at all - no
        // partial state to worry about here.
        from.withdraw(amount);
        to.deposit(amount);

        // KNOWN LIMITATION (flag for the team): this logs the two legs as a
        // plain WITHDRAWAL and DEPOSIT rather than TRANSFER_OUT/TRANSFER_IN,
        // and relatedAccountNumber stays null on both. TransactionType
        // already has TRANSFER_IN/TRANSFER_OUT defined for this, but using
        // them would mean withdraw()/deposit() need to accept a type and a
        // related-account parameter, which changes their shared contract.
        // Worth deciding together before Member 3 builds the transaction
        // history view around whichever behavior we pick.
    }
}
