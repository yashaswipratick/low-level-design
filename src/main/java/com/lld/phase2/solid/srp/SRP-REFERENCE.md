# SRP — Single Responsibility Principle
> Personal reference: definition + tree architecture + interview answer

---

## Definition

> "A class should have only ONE reason to change."

### Plain English Version

> **"Ask: what would make me change this class? If you can give two different answers — SRP is violated."**

Not "one class, one job" — that's too vague.
The real test is: **how many different business changes force you to touch this class?**

### One-Line Memory Hook

> **"One class = one reason to change."**

---

## Problem 1 — ReportService

### BEFORE — Violation

```
ReportService  (1 class — 3 reasons to change)
│
├── fetchSalesData()     ← Reason 1: DB schema changes
│       └── raw JDBC SQL query
│
├── formatAsHtml()       ← Reason 2: HTML layout changes
│       └── builds HTML table string
│
└── emailReport()        ← Reason 3: Email provider changes
        └── JavaMailSender + MimeMessage

If DB schema changes    → touch ReportService ❌
If HTML layout changes  → touch ReportService ❌
If email provider swaps → touch ReportService ❌
```

**Why SRP is violated:**
- One class has 3 unrelated concerns packed together
- A UI designer changing the HTML table layout touches the same class as a DBA changing SQL
- Testing email logic requires setting up a DB connection

---

### AFTER — Fixed

```
ReportServiceOrchestrator  (@Service — orchestrator)
│   └── generateAndSend(emailTo, from, to)
│           │
│           ├── 1. salesDataRepository.fetchSalesData(from, to)
│           │                │
│           │         SalesDataRepository  (@Repository)
│           │             └── Reason to change: DB schema / SQL query only
│           │
│           ├── 2. formatterService.formatAsHtml(salesData)
│           │                │
│           │         ReportFormatterService  (@Component)
│           │             └── Reason to change: HTML layout only
│           │
│           └── 3. emailSenderService.emailReport(emailTo, htmlReport)
│                            │
│                    ReportEmailSenderService  (@Component)
│                        └── Reason to change: email provider / template only


Each class has EXACTLY ONE reason to change:
    SalesDataRepository     → DB schema changes
    ReportFormatterService  → HTML layout changes
    ReportEmailSenderService → Email provider/template changes
    ReportServiceOrchestrator → Pipeline order changes
```

---

### The Key Shift

```
BEFORE                              AFTER
────────────────────────────────────────────────────────────
ReportService                       ReportServiceOrchestrator
    │                                   │ (thin — no logic)
    ├── fetchSalesData()  ❌            ├── SalesDataRepository     @Repository
    ├── formatAsHtml()    ❌            ├── ReportFormatterService   @Component
    └── emailReport()     ❌            └── ReportEmailSenderService @Component

3 reasons to change                 1 reason to change each
One fat class                       4 focused classes
Hard to test in isolation           Each testable independently
```

---

### Spring Stereotype Reminder

```
@Repository  →  talks to DB (SalesDataRepository)
@Component   →  utility/infrastructure (Formatter, EmailSender)
@Service     →  orchestrates business logic (ReportServiceOrchestrator)

Rule: if a class runs SQL queries → @Repository
      if a class sends email / formats data → @Component
      if a class coordinates other services → @Service
```

---

### Code Structure (your implementation)

```
srp/problem1/
├── ReportService.java                    ← VIOLATION: fetch + format + email in one class
│
└── fix/
    ├── SalesDataRepository.java          ← @Repository: DB concern only ✅
    ├── ReportFormatterService.java       ← @Component: HTML format concern only ✅
    ├── ReportEmailSenderService.java     ← @Component: email concern only ✅
    └── ReportServiceOrchestrator.java    ← @Service: thin orchestrator ✅
```

---

### The SRP Test

Ask for EACH class: **"What would make me change this?"**

```
SalesDataRepository      → "orders table gets a new column"    ✅ one answer
ReportFormatterService   → "we switch from table to div layout" ✅ one answer
ReportEmailSenderService → "we switch from JavaMail to AWS SES" ✅ one answer
ReportServiceOrchestrator→ "we add a PDF step to the pipeline"  ✅ one answer

Old ReportService        → DB change OR layout change OR email change ❌ three answers
```

---

## How to Detect SRP Violations (Quick Reference)

| Tell-tale Sign | Example | Verdict |
|---|---|---|
| Class name has `Manager`, `Helper`, `Utils` | `UserManager`, `DataHelper` | ❌ SRP likely violated |
| Method list spans unrelated concerns | `validateUser()` + `sendEmail()` + `generatePDF()` in one class | ❌ SRP violated |
| Class has 5+ injected dependencies | `@Autowired` on 6 different services | ❌ SRP violated |
| Changing email template forces touching DB code | Single class handles both | ❌ SRP violated |
| Each class answers "one reason to change" | `SalesDataRepository` → DB only | ✅ SRP satisfied |

---

## Interview Answer Template

> "SRP says a class should have only one reason to change. The real test isn't
> 'does it do one thing' — it's 'how many different business changes force me to touch
> this class?'
>
> In my code, `ReportService` had three reasons to change: DB schema changes, HTML
> layout changes, and email provider changes. A UI designer and a DBA both had to touch
> the same class — that's the violation.
>
> The fix was to split into four classes: `SalesDataRepository` for DB queries,
> `ReportFormatterService` for HTML formatting, `ReportEmailSenderService` for email,
> and a thin `ReportServiceOrchestrator` that just wires the pipeline together.
> Now each class changes for exactly one reason, and each can be tested in isolation."

---

## SOLID Quick Recap (where SRP fits)

```
S — Single Responsibility  →  One reason to change  ← YOU ARE HERE
O — Open/Closed            →  Open for extension, closed for modification
L — Liskov Substitution    →  Subtypes must honor parent's contract
I — Interface Segregation  →  Don't force implementors to use unused methods
D — Dependency Inversion   →  Depend on abstractions, not concretions
```

---

## Your Reference Files So Far

```
phase-2-solid/
├── srp/SRP-REFERENCE.md   ← Single Responsibility Principle  ← THIS FILE
├── lsp/LSP-REFERENCE.md   ← Liskov Substitution Principle
├── isp/ISP-REFERENCE.md   ← Interface Segregation Principle
└── dip/DIP-REFERENCE.md   ← Dependency Inversion Principle
```
