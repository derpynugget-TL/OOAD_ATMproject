package com.bankgroup.atm.exception;

/**
 * Owner: Core Banking Logic (You)
 * Thrown when a withdrawal or transfer would exceed the available balance
 * (or violate an account-specific rule, e.g. minimum balance / daily limit).
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
