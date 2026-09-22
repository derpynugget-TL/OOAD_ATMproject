package com.bankgroup.atm.service;

import com.bankgroup.atm.exception.AccountNotFoundException;
import com.bankgroup.atm.exception.InsufficientFundsException;
import com.bankgroup.atm.exception.InvalidPinException;
import com.bankgroup.atm.model.Account;
import com.bankgroup.atm.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
     * Owner: You (Team Lead) — added for Admin Mode (advanced feature).
     * Unmodifiable snapshot, same reasoning as Account.getTransactionHistory():
     * admin can view accounts but shouldn't be able to mutate the map directly.
     */
    public List<Account> getAllAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }

    /**
     * Owner: You (Team Lead) — added for Admin Mode, so the "unlock account"
     * action can call user.unlock() (Auth & Security's method) on the right
     * User by account number.
     */
    public User getUser(String accountNumber) throws AccountNotFoundException {
        User user = users.get(accountNumber);
        if (user == null) {
            throw new AccountNotFoundException("No account found: " + accountNumber);
        }
        return user;
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


    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);

        from.withdraw(amount);
        to.deposit(amount);

    }
}