package com.bankgroup.atm.model;

/**
 * Owner: Auth & Security (Member 2)
 *
 * Links login credentials (card/account number + PIN) to an Account.
 * Also tracks failed login attempts for the account-locking requirement.
 */
public class User {

    private final String accountNumber; // matches Account.getAccountNumber()
    private String pinHash;             // store a hash, not the raw PIN
    private int failedAttempts = 0;
    private boolean locked = false;

    public User(String accountNumber, String pinHash) {
        this.accountNumber = accountNumber;
        this.pinHash = pinHash;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isLocked() {
        return locked;
    }

    /**
     * @return true if the PIN matched; false otherwise. Caller (Bank/ATM)
     *         decides what to do with failed attempts / locking.
     */
    public boolean checkPin(String enteredPin) {
        // TODO(Member 2): compare hash of enteredPin to pinHash
        return false;
    }

    public void registerFailedAttempt() {
        // TODO(Member 2): increment failedAttempts; lock at 3
    }

    public void resetFailedAttempts() {
        failedAttempts = 0;
    }

    public void unlock() {
        // TODO(Member 2): for admin mode, if attempted
        locked = false;
        failedAttempts = 0;
    }

    public void changePin(String oldPin, String newPin) {
        // TODO(Member 2): validate oldPin via checkPin first, then update pinHash
    }
}
