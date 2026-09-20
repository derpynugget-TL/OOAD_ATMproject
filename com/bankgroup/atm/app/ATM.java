package com.bankgroup.atm.app;

import com.bankgroup.atm.exception.AccountNotFoundException;
import com.bankgroup.atm.exception.InsufficientFundsException;
import com.bankgroup.atm.exception.InvalidPinException;
import com.bankgroup.atm.model.Account;
import com.bankgroup.atm.model.CheckingAccount;
import com.bankgroup.atm.model.SavingsAccount;
import com.bankgroup.atm.model.Transaction;
import com.bankgroup.atm.model.User;
import com.bankgroup.atm.service.Bank;

import java.util.List;
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

    public void run() {
        while (true) {
            User user = authenticate();
            if (user == null) {
                System.out.println("Goodbye.");
                return;
            }
            showAuthenticatedMenu(user);
        }
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        seedSampleData(bank); // so all 3 of you have consistent test data

        ATM atm = new ATM(bank);
        atm.run();
    }

        /**
     * Owner: Data & Testing (Member 3) — sample data for the README's
     * "test credentials" section. Deliberately covers both account subclasses
     * and gives each account a small starting history so option 5 has
     * something to display on a fresh run.
     */
    private static void seedSampleData(Bank bank) {
        try {
            SavingsAccount savings = new SavingsAccount("SA001", 500.0);
            bank.addAccount(savings, new User("SA001", "1234"));

            CheckingAccount checking = new CheckingAccount("CA001", 200.0);
            bank.addAccount(checking, new User("CA001","5678"));

            // Second savings account so transfers can be tested without
            // crossing account types.
            SavingsAccount savings2 = new SavingsAccount("SA002", 1000.0);
            bank.addAccount(savings2, new User("SA002","4321"));

            // Pre-populate a little history so "5. Transaction History" is
            // not empty the first time a marker runs the program.
            savings.deposit(150.0);
            savings.withdraw(75.50);
            bank.transfer("SA002", "CA001", 250.0);

        } catch (Exception e) {
            // Seeding failing should not take the whole ATM down — the app
            // still runs, just with no sample accounts.
            System.out.println("Warning: could not seed sample data (" + e.getMessage() + ")");
        }
    }

    

    /**
     * Loops on the login prompt until either a login succeeds or the user
     * types "exit". Relies on Bank.login() (Member 2's implementation) to
     * do the real credential/lock checking.
     */
    private User authenticate() {
        while (true) {
            System.out.print("Enter account number (or 'exit' to quit): ");
            String accountNumber = scanner.nextLine().trim();
            if (accountNumber.equalsIgnoreCase("exit")) {
                return null;
            }

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            try {
                return bank.login(accountNumber, pin);
            } catch (AccountNotFoundException e) {
                System.out.println("No account found with that number. Please try again.");
            } catch (InvalidPinException e) {
                // Message text (e.g. "incorrect PIN" vs "account locked")
                // is up to Member 2's exception messages.
                System.out.println(e.getMessage());
            }
        }
    }

    private void showAuthenticatedMenu(User user) {
        boolean sessionActive = true;
        while (sessionActive) {
            System.out.println("\n--- Account " + user.getAccountNumber() + " ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Change PIN");
            System.out.println("7. Exit / Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();


            // Single catch point for every custom exception in this session
            // -> satisfies "no raw stack traces shown to the user" (mandatory
            // requirement #5) without repeating try-catch in every branch.
            try {
                switch (choice) {
                    case "1" : checkBalance(user); break;
                    case "2" : deposit(user); break;
                    case "3" : withdraw(user); break;
                    case "4" : transfer(user); break;
                    case "5" : showHistory(user); break;
                    case "6" : changePin(user); break;
                    case "7" : {
                        sessionActive = false;
                        System.out.println("Logging out...");
                        break;
                    }
                    default : System.out.println("Invalid option, please choose 1-7.");
                }
            } catch (AccountNotFoundException | InsufficientFundsException | InvalidPinException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void checkBalance(User user) throws AccountNotFoundException {
        Account account = bank.getAccount(user.getAccountNumber());
        System.out.printf("Current balance: $%.2f%n", account.getBalance());
    }

    private void deposit(User user) throws AccountNotFoundException {
        Account account = bank.getAccount(user.getAccountNumber());
        double amount = readAmount("Enter deposit amount: ");
        account.deposit(amount);
        System.out.printf("Deposited $%.2f. New balance: $%.2f%n", amount, account.getBalance());
    }

    private void withdraw(User user) throws AccountNotFoundException, InsufficientFundsException {
        Account account = bank.getAccount(user.getAccountNumber());
        double amount = readAmount("Enter withdrawal amount: ");
        account.withdraw(amount);
        System.out.printf("Withdrew $%.2f. New balance: $%.2f%n", amount, account.getBalance());
    }

    private void transfer(User user) throws AccountNotFoundException, InsufficientFundsException {
        System.out.print("Enter destination account number: ");
        String toAccountNumber = scanner.nextLine().trim();
        double amount = readAmount("Enter transfer amount: ");
        bank.transfer(user.getAccountNumber(), toAccountNumber, amount);
        System.out.println("Transfer complete.");
    }

    private void showHistory(User user) throws AccountNotFoundException {
        Account account = bank.getAccount(user.getAccountNumber());
        List<Transaction> history = account.getTransactionHistory();

        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        System.out.println();
        System.out.println(Transaction.HISTORY_HEADER);
        history.forEach(t -> System.out.println(t.toString()));
        System.out.println("-".repeat(78));
        System.out.printf("%d transaction(s). Current balance: %,.2f%n",
                history.size(), account.getBalance());
    }

    private void changePin(User user) throws InvalidPinException {
        System.out.print("Enter current PIN: ");
        String oldPin = scanner.nextLine().trim();
        System.out.print("Enter new PIN: ");
        String newPin = scanner.nextLine().trim();
        user.changePin(oldPin, newPin);
        System.out.println("PIN changed successfully.");
    }

    /**
     * Keeps prompting until the user enters a valid positive number, so a
     * typo doesn't crash the session or need to be handled by every caller.
     */
    private double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    System.out.println("Amount must be positive.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
