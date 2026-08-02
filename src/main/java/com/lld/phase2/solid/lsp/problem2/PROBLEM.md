# LSP — Problem 2: The Premium Account That Breaks Withdrawals

## Your Task

Look at `BankAccount.java` and `PremiumAccount.java`.

The parent's `withdraw()` contract: **reduce balance by `amount` or throw `InsufficientFundsException`**.
`PremiumAccount.withdraw()` silently withdraws *less* than requested with no exception and no signal.

1. Name the specific LSP contract violation: which postcondition is weakened?
2. Fix the design so `PremiumAccount` can support overdraft **without violating LSP**.

## Hints
- The fix is to change the return type of `withdraw()` to return the **actual amount withdrawn**
- Make `BankAccount.withdraw()` abstract so both subtypes must explicitly implement the contract
- Callers use the return value — no silent failures
- The contract becomes: "return value ≤ requested amount, always ≥ 0, never silent"

## Expected Output
Modify in this directory:
- `BankAccount.java` (make `withdraw` abstract, return `BigDecimal`)
- `StandardAccount.java`
- `PremiumAccount.java` (honors new contract)
- `WithdrawalProcessor.java` (caller that works correctly with both account types)
