# Problem: Splitwise — Expense Sharing
> Domain: Fintech | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design an expense-sharing application where users can split bills with friends in multiple ways.

Requirements:
1. Users can create groups and add expenses
2. Multiple split strategies: equal split, exact amounts, percentage split, share-based split
3. After multiple expenses, compute who owes whom and how much
4. Debt simplification: if A owes B ₹100 and B owes C ₹100, simplify to A owes C ₹100
5. Users can settle debts (mark as paid)
6. Expense history and balance summary per user

---

## Clarifying Questions to Ask

- Is a group required or can expenses be between two individuals?
- Can one expense use multiple split strategies (e.g. person A pays exact, rest split equally)?
- Is debt simplification across all users or within a group only?
- Can expenses be edited or deleted after creation?
- Multi-currency support needed?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — split algorithm (equal, exact, percent, shares) is pluggable
- **Observer** — notify users when a new expense is added or settled

</details>

---

## Your Task

1. Model: `User`, `Group`, `Expense`, `SplitStrategy`, `Balance`
2. Implement all 4 split strategies
3. Implement balance computation: who owes whom across all expenses
4. Implement in `src/main/java/com/lld/phase8/problems/classics/splitwise/`
5. Demo: 3 users, 4 expenses (mixed strategies), compute final balances

---

## Edge Cases

- User leaves a group with outstanding balance
- Expense added in wrong currency (if multi-currency)
- Circular debt: A→B→C→A — simplification must resolve
- Same user as both payer and receiver in debt simplification
