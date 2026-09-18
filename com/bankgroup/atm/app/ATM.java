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
        System.out.println("=== Welcome to the ATM ===");
        while (true) {
            User currentUser = authenticate();
            if (currentUser == null) {
                break; // user chose to quit at the login prompt
            }
            showAuthenticatedMenu(currentUser);
        }
        System.out.println("Goodbye.");
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
        } else {
            history.forEach(System.out::println); // TODO(Member 3): nicer formatting via Transaction.toString()
        }
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
