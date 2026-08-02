package com.lld.phase2.solid.lsp.problem2;

import com.lld.phase2.solid.stubs.InsufficientFundsException;

import java.math.BigDecimal;

// TODO: LSP VIOLATION SETUP
//
// Parent contract for withdraw():
//   "Reduce balance by `amount` exactly, OR throw InsufficientFundsException."
//
// PremiumAccount.withdraw() breaks this contract silently:
//   - Does NOT throw when amount > overdraft limit
//   - Withdraws LESS than requested with no signal to the caller
//   - Callers that assume full amount was withdrawn record wrong transaction amounts
//
// Your task:
//   1. Make withdraw() return BigDecimal (the actual amount withdrawn)
//   2. Make it abstract — force subclasses to explicitly honor the contract
//   3. Fix PremiumAccount.withdraw() to return the actual amount — no silent partial withdrawals

public class BankAccount {

    protected BigDecimal balance;

    public BankAccount(BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    // VIOLATION: contract says "withdraw amount or throw" — PremiumAccount does neither correctly
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                "Requested: " + amount + ", Available: " + balance
            );
        }
        this.balance = this.balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
