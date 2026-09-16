package com.bankgroup.atm.exception;

/**
 * Owner: Auth & Security (Member 2), used across the app
 * Thrown when a lookup by account/card number does not match any known account,
 * e.g. during login or when a transfer targets a non-existent account.
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
