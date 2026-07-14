# Problem: Banking System
> Domain: FinTech | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design the core of a banking system.

Requirements:
1. Account types: Savings, Current, Fixed Deposit — each with different rules
2. Core operations: deposit, withdrawal, transfer (intra-bank and inter-bank)
3. Transaction history with filtering and pagination
4. Overdraft protection: savings accounts cannot go below zero (by default)
5. Interest calculation: daily compounding for savings, fixed rate for FD
6. Fraud detection: flag suspicious transactions (large amount, unusual location, rapid succession)
7. Audit log: immutable record of every operation (who, what, when, amount, balance before/after)

---

## Clarifying Questions to Ask

- Is inter-bank transfer synchronous (RTGS) or async (NEFT)?
- Is overdraft allowed for current accounts with a limit?
- Is the fraud detection rule-based or ML-based?
- Are joint accounts in scope?
- What is the concurrency model — can two transfers debit the same account simultaneously?
- Is currency conversion in scope for international transfers?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — account states: Active, Dormant, Frozen, Closed
- **Command** — each transaction is a command (auditable; transfers can be reversed via compensating transaction)
- **Strategy** — interest calculation algorithm per account type; fraud detection rules
- **Chain of Responsibility** — transaction validation: account active → sufficient balance → fraud check → execute
- **Observer** — fraud alert observer, low-balance alert, transaction notification

</details>

---

## Class Design Starting Point

```
Account (abstract)
  ├── String accountNumber
  ├── Money balance
  ├── AccountState state
  ├── void deposit(Money amount)
  ├── void withdraw(Money amount)
  └── abstract Money calculateInterest()

SavingsAccount extends Account
CurrentAccount extends Account
FixedDepositAccount extends Account

Transaction (Command)
  ├── TransactionType type   // DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT, TRANSFER_CREDIT
  ├── Money amount
  ├── Money balanceBefore
  ├── Money balanceAfter
  └── Instant timestamp

TransactionValidator (Chain node)
  └── void validate(Account account, Transaction txn) throws ValidationException

FraudDetector (Observer)
  └── void onTransaction(Transaction txn)

InterestCalculationStrategy (interface)
  └── Money calculate(Account account, Period period)
```

---

## Your Task

1. `Account` hierarchy with type-specific withdrawal rules (overdraft protection)
2. `TransactionValidator` chain: active check → balance check → daily limit check → fraud check
3. `TransferService` for atomic debit+credit (both succeed or both fail)
4. `FraudDetector` as Observer with rules: large amount, rapid transactions, unusual time
5. `InterestCalculationService` with `CompoundInterestStrategy` and `SimpleInterestStrategy`
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/banking/`

---

## Edge Cases

- Concurrent transfers: Account A → B and Account A → C simultaneously — oversell balance
- Account frozen mid-transfer — debit succeeds but credit fails — compensating transaction
- Interest calculated on FD that was broken early — penalty logic
- Transaction during system downtime — how is idempotency maintained?
- Customer disputes a transaction — what audit data is needed?
- Dormant account reactivation — what checks are needed?

---

## Extension Points

- Scheduled transfers (standing instructions) → `ScheduledTransaction` with `Trigger`
- Multi-currency accounts → `Money` type with `Currency` and conversion
- Loan accounts → `LoanAccount` with EMI calculation
- Two-factor authentication for large transfers → `AuthorizationChain` node
