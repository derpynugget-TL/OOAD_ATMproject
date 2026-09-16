package com.bankgroup.atm.exception;

/**
 * Owner: Auth & Security (Member 2)
 * Thrown when a login attempt or PIN-change attempt supplies a wrong PIN.
 * Should NOT reveal whether the account exists or the correct PIN — just "invalid".
 */
public class InvalidPinException extends Exception {

    public InvalidPinException(String message) {
        super(message);
    }
}
