# ATM System — Java OOP Group Project

A simulated ATM console application built in Java, using an `Account`
(abstract) → `SavingsAccount` / `CheckingAccount` hierarchy backed by a
`Bank` service and a console menu.

## How to Compile and Run

From the project root (the folder that directly contains `com/`):

```bash
# Compile everything into an "out" folder
find com -name "*.java" | xargs javac -d out

# Run the ATM console app
java -cp out com.bankgroup.atm.app.ATM
```

To run the manual sanity check for the core account/transfer logic instead:

```bash
java -cp out com.bankgroup.atm.app.ManualCoreLogicTest
```

Requires JDK 17+ (developed and tested on JDK 21).

## Sample Login Credentials

Seeded automatically each time the program starts:

| Account Number | PIN  | Type     |
|---|---|---|
| SA001 | 1234 | Savings  |
| CA001 | 5678 | Checking |
| SA002 | 4321 | Savings  |

**Admin mode:** at the login prompt, type `admin` instead of an account
number, then password `admin123`.

## Implemented Features

### Core Features

| # | Feature | Status |
|---|---|---|
| 1 | Authentication (login + lock after 3 failed attempts) | Done |
| 2 | Check Balance | Done |
| 3 | Deposit | Done |
| 4 | Withdraw (daily limit enforced on Checking) | Done |
| 5 | Transfer between accounts | Done |
| 6 | Transaction History | Done |
| 7 | PIN Management | Done |
| 8 | Exit / Session End | Done |

### Advanced Features Attempted

1. **Multiple account types with different rules** — `SavingsAccount`
   (minimum balance, monthly interest) and `CheckingAccount` (overdraft,
   daily withdrawal limit). **Done.**
2. **Admin mode** — create accounts, view all accounts, unlock a locked
   account. **Done.**

## Team Members & Contributions

- **Tang Leng — Team Lead / Core Banking Logic:** Account hierarchy,
  transaction/transfer logic, ATM console menu loop, admin mode,
  integration of all members' work.
- **Chin Chanvattey — Auth & Security:** Login, PIN handling, account
  locking, custom exceptions.
- **Souer Porhok — Data & Testing:** Transaction history, sample data, test
  coverage.