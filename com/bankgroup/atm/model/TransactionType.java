package com.bankgroup.atm.model;

/**
 * Owner: Data & Testing (Member 3)
 *
 * Part of the shared contract — Core Banking Logic passes one of these into
 * every Transaction it creates.
 *
 * getSign() lets Account apply a transaction without an if/else chain:
 *     balance += type.getSign() * amount;
 */
public enum TransactionType {

    DEPOSIT("Deposit", +1),
    WITHDRAWAL("Withdrawal", -1),
    TRANSFER_IN("Transfer In", +1),
    TRANSFER_OUT("Transfer Out", -1);

    private final String label;   // human-readable, used by the history screen
    private final int sign;       // +1 increases balance, -1 decreases it

    TransactionType(String label, int sign) {
        this.label = label;
        this.sign = sign;
    }

    public String getLabel() {
        return label;
    }

    public int getSign() {
        return sign;
    }

    /** True for the two types that require a relatedAccountNumber. */
    public boolean isTransfer() {
        return this == TRANSFER_IN || this == TRANSFER_OUT;
    }
}