package com.bankgroup.atm.model;

/**
 * Owner: Data & Testing (Member 3)
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT,
    INTEREST // only relevant if SavingsAccount interest is implemented
}
