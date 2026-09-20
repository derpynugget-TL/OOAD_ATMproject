package com.bankgroup.atm.model;

import com.bankgroup.atm.exception.InvalidPinException;

public class User {

    private final String accountNumber;
    private String pinHash;
    private int failedAttempts = 0;
    private boolean locked = false;

    public User(String accountNumber, String pin) {
        this.accountNumber = accountNumber;
        this.pinHash = hashPin(pin);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean checkPin(String enteredPin) {
        return pinHash.equals(hashPin(enteredPin));
    }

    public void registerFailedAttempt() {
        failedAttempts++;

        if (failedAttempts >= 3) {
            locked = true;
        }
    }

    public void resetFailedAttempts() {
        failedAttempts = 0;
    }

    public void unlock() {
        locked = false;
        failedAttempts = 0;
    }

    public void changePin(String oldPin, String newPin)
            throws InvalidPinException {

        if (!checkPin(oldPin)) {
            throw new InvalidPinException("Incorrect old PIN.");
        }

        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new InvalidPinException(
                    "New PIN must contain exactly 4 digits."
            );
        }

        
        pinHash = hashPin(newPin);
    }

    private String hashPin(String pin) {
        return Integer.toString(pin.hashCode());
    }
}