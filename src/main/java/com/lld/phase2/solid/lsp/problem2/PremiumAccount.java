package com.lld.phase2.solid.lsp.problem2;

import java.math.BigDecimal;
import java.util.List;

// TODO: LSP VIOLATION — PremiumAccount weakens the postcondition of withdraw().
//
// Parent contract: withdraw(amount) deducts exactly `amount` OR throws.
// PremiumAccount:  silently withdraws LESS than `amount` when overdraft limit is reached.
//
// Silent corruption in caller:
//   void processWithdrawals(List<BankAccount> accounts, BigDecimal amount) {
//       for (BankAccount account : accounts) {
//           account.withdraw(amount);
//           recordTransaction(amount);  // WRONG: records full amount, but PremiumAccount
//       }                               // may have withdrawn only partial amount
//   }
//
// Your task: make PremiumAccount honor the contract.
// Fix: change withdraw() return type to BigDecimal (actual amount withdrawn).

public class PremiumAccount extends BankAccount {

    private static final BigDecimal OVERDRAFT_LIMIT = BigDecimal.valueOf(-5000);

    public PremiumAccount(BigDecimal initialBalance) {
        super(initialBalance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal projected = balance.subtract(amount);

        if (projected.compareTo(OVERDRAFT_LIMIT) < 0) {
            // VIOLATION: silently caps the withdrawal — caller gets LESS than requested
            // No exception, no return value, no signal — caller assumes full withdrawal happened
            balance = OVERDRAFT_LIMIT;
        } else {
            balance = projected;
        }
    }

    // This caller code is broken when used with PremiumAccount at its overdraft limit
    public static void processWithdrawals(List<BankAccount> accounts, BigDecimal amount) {
        for (BankAccount account : accounts) {
            account.withdraw(amount);
            recordTransaction(amount); // records wrong amount for a capped PremiumAccount
        }
    }

    private static void recordTransaction(BigDecimal amount) {
        System.out.println("Transaction recorded: " + amount);
    }
}
