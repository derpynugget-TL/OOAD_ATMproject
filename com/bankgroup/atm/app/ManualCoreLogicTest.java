package com.bankgroup.atm.app;

import com.bankgroup.atm.exception.AccountNotFoundException;
import com.bankgroup.atm.exception.InsufficientFundsException;
import com.bankgroup.atm.model.CheckingAccount;
import com.bankgroup.atm.model.SavingsAccount;
import com.bankgroup.atm.model.User;
import com.bankgroup.atm.service.Bank;

/**
 * Owner: You (Core Banking Logic) — quick manual sanity check, NOT the
 * official test suite. Member 3 will replace/extend this with real unit
 * tests (JUnit) once testing tooling is set up. This just answers "does the
 * logic I wrote actually behave correctly?" before handing it off.
 *
 * Run with: java -cp out com.bankgroup.atm.app.ManualCoreLogicTest
 * (after compiling — see README for the full compile command)
 */
public class ManualCoreLogicTest {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        testSavingsDepositAndWithdraw();
        testSavingsMinimumBalanceEnforced();
        testSavingsInterest();
        testCheckingOverdraft();
        testCheckingDailyLimit();
        testBankTransferSuccess();
        testBankTransferInsufficientFunds();
        testBankTransferUnknownAccount();
        testBankTransferToSelfRejected();

        System.out.println("\n=== Results: " + passCount + " passed, " + failCount + " failed ===");
    }

    private static void testSavingsDepositAndWithdraw() {
        System.out.println("\n-- SavingsAccount: deposit + withdraw --");
        SavingsAccount account = new SavingsAccount("SA001", 100.0);
        account.deposit(50.0);
        check("balance after deposit is 150.0", account.getBalance() == 150.0);

        try {
            account.withdraw(30.0);
            check("balance after withdraw is 120.0", account.getBalance() == 120.0);
        } catch (InsufficientFundsException e) {
            check("withdraw within limits should not throw", false);
        }

        check("transaction history has 2 entries", account.getTransactionHistory().size() == 2);
    }

    private static void testSavingsMinimumBalanceEnforced() {
        System.out.println("\n-- SavingsAccount: minimum balance rule --");
        SavingsAccount account = new SavingsAccount("SA002", 100.0);
        try {
            account.withdraw(60.0); // would leave 40.0, below the $50 minimum
            check("withdrawing below minimum balance should throw", false);
        } catch (InsufficientFundsException e) {
            check("withdrawing below minimum balance throws InsufficientFundsException", true);
        }
        check("balance unchanged after rejected withdrawal", account.getBalance() == 100.0);
    }

    private static void testSavingsInterest() {
        System.out.println("\n-- SavingsAccount: monthly interest --");
        SavingsAccount account = new SavingsAccount("SA003", 1000.0);
        account.applyMonthlyUpdate(); // 1% of 1000.0 = 10.0
        check("balance after interest is 1010.0", account.getBalance() == 1010.0);
    }

    private static void testCheckingOverdraft() {
        System.out.println("\n-- CheckingAccount: overdraft limit --");
        CheckingAccount account = new CheckingAccount("CA001", 50.0);
        try {
            account.withdraw(120.0); // balance would be -70.0, within the $100 overdraft limit
            check("balance after overdraft withdrawal is -70.0", account.getBalance() == -70.0);
        } catch (InsufficientFundsException e) {
            check("withdrawal within overdraft limit should not throw", false);
        }

        try {
            account.withdraw(50.0); // balance would be -120.0, exceeds the $100 overdraft limit
            check("withdrawing beyond overdraft limit should throw", false);
        } catch (InsufficientFundsException e) {
            check("withdrawing beyond overdraft limit throws InsufficientFundsException", true);
        }
    }

    private static void testCheckingDailyLimit() {
        System.out.println("\n-- CheckingAccount: daily withdrawal limit --");
        CheckingAccount account = new CheckingAccount("CA002", 1000.0);
        try {
            account.withdraw(500.0); // exactly at the $500 daily limit
            check("withdrawal exactly at daily limit should not throw", true);
        } catch (InsufficientFundsException e) {
            check("withdrawal exactly at daily limit should not throw", false);
        }

        try {
            account.withdraw(1.0); // pushes today's total to 501.0, over the limit
            check("exceeding daily limit on a second withdrawal should throw", false);
        } catch (InsufficientFundsException e) {
            check("exceeding daily limit on a second withdrawal throws InsufficientFundsException", true);
        }
    }

    private static void testBankTransferSuccess() {
        System.out.println("\n-- Bank: successful transfer --");
        Bank bank = new Bank();
        SavingsAccount from = new SavingsAccount("SA010", 200.0);
        CheckingAccount to = new CheckingAccount("CA010", 0.0);
        bank.addAccount(from, new User("SA010", "1111"));
        bank.addAccount(to, new User("CA010", "2222"));

        try {
            bank.transfer("SA010", "CA010", 75.0);
            check("source balance reduced to 125.0", from.getBalance() == 125.0);
            check("destination balance increased to 75.0", to.getBalance() == 75.0);
        } catch (Exception e) {
            check("valid transfer should not throw", false);
        }
    }

    private static void testBankTransferInsufficientFunds() {
        System.out.println("\n-- Bank: transfer blocked by account rules --");
        Bank bank = new Bank();
        SavingsAccount from = new SavingsAccount("SA011", 60.0); // min balance $50
        CheckingAccount to = new CheckingAccount("CA011", 0.0);
        bank.addAccount(from, new User("SA011", "3333"));
        bank.addAccount(to, new User("CA011", "4444"));

        try {
            bank.transfer("SA011", "CA011", 30.0); // would leave 30.0, below minimum
            check("transfer violating minimum balance should throw", false);
        } catch (InsufficientFundsException e) {
            check("transfer violating minimum balance throws InsufficientFundsException", true);
        } catch (AccountNotFoundException e) {
            check("unexpected AccountNotFoundException", false);
        }
        check("destination balance unaffected by failed transfer", to.getBalance() == 0.0);
    }

    private static void testBankTransferUnknownAccount() {
        System.out.println("\n-- Bank: transfer to unknown account --");
        Bank bank = new Bank();
        SavingsAccount from = new SavingsAccount("SA012", 200.0);
        bank.addAccount(from, new User("SA012", "5555"));

        try {
            bank.transfer("SA012", "DOES_NOT_EXIST", 10.0);
            check("transfer to unknown account should throw", false);
        } catch (AccountNotFoundException e) {
            check("transfer to unknown account throws AccountNotFoundException", true);
        } catch (InsufficientFundsException e) {
            check("unexpected InsufficientFundsException", false);
        }
    }

    private static void testBankTransferToSelfRejected() {
        System.out.println("\n-- Bank: transfer to the same account --");
        Bank bank = new Bank();
        SavingsAccount account = new SavingsAccount("SA013", 200.0);
        bank.addAccount(account, new User("SA013", "6666"));

        try {
            bank.transfer("SA013", "SA013", 10.0);
            check("self-transfer should throw", false);
        } catch (IllegalArgumentException e) {
            check("self-transfer throws IllegalArgumentException", true);
        } catch (Exception e) {
            check("unexpected exception type for self-transfer", false);
        }
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            passCount++;
            System.out.println("  [PASS] " + label);
        } else {
            failCount++;
            System.out.println("  [FAIL] " + label);
        }
    }
}
