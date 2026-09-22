package com.bankgroup.atm.model;

public enum TransactionType {

    DEPOSIT("Deposit", +1),
    WITHDRAWAL("Withdrawal", -1),
    INTEREST("Interest", +1),
    TRANSFER_IN("Transfer In", +1),
    TRANSFER_OUT("Transfer Out", -1);

    private final String label;
    private final int sign;

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

    public boolean isTransfer() {
        return this == TRANSFER_IN || this == TRANSFER_OUT;
    }
}