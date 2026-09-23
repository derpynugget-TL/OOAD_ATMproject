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

    public List<Account> getAllAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }

    public User getUser(String accountNumber) throws AccountNotFoundException {
        User user = users.get(accountNumber);
        if (user == null) {
            throw new AccountNotFoundException("No account found: " + accountNumber);
        }
        return user;
    }

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