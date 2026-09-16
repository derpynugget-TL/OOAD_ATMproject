package com.bankgroup.atm.app;

import com.bankgroup.atm.model.CheckingAccount;
import com.bankgroup.atm.model.SavingsAccount;
import com.bankgroup.atm.model.User;
import com.bankgroup.atm.service.Bank;

import java.util.Scanner;

/**
 * Owner: You (Team Lead) — this is the integration point where everyone's
 * pieces get wired together into the console menu loop.
 *
 * Baseline requirement: console-based (Scanner), so keep the interaction
 * here simple and readable.
 */
public class ATM {

    private final Bank bank;
    private final Scanner scanner = new Scanner(System.in);

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        seedSampleData(bank); // so all 3 of you have consistent test data

        ATM atm = new ATM(bank);
        atm.run();
    }

    /**
     * Owner: Data & Testing (Member 3) — this is the "sample data" piece
     * mentioned in the deliverables (README needs sample login credentials).
     */
    private static void seedSampleData(Bank bank) {
        // TODO(Member 3): create a couple of sample accounts/users, e.g.
        // bank.addAccount(new SavingsAccount("SA001", 500.0), new User("SA001", hashOf("1234")));
        // bank.addAccount(new CheckingAccount("CA001", 200.0), new User("CA001", hashOf("5678")));
        // Document these credentials in the README for testing.
    }

    public void run() {
        // TODO(You): main menu loop -> login, then show authenticated menu
        // (balance, deposit, withdraw, transfer, history, change PIN, exit)
        // Wrap calls to Bank/Account in try-catch for the custom exceptions
        // and print a friendly message — NEVER let a raw stack trace show
        // (this is explicitly graded).
        System.out.println("ATM System — starting up (menu loop TODO)");
    }
}
