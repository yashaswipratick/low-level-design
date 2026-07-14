# Chain of Responsibility — Practice Problems
> Goal: Pass a request through a CHAIN of handlers until one handles it.

---

## Key Intuition
**Chain = customer support escalation.** You call support. Bot tries to help. Fails → Level 1 agent. Fails → Level 2 specialist. Fails → Manager. Each handler either handles the request OR passes it to the next.

**When to think CoR:** Multiple validators/processors that run in sequence, where each can stop or pass through.

---

## Problem 1: Customer Support Escalation

### Scenario
A customer contacts support. The request goes through:
1. **Bot** — handles FAQs (returns answer if it's a known FAQ, otherwise escalates)
2. **Level 1 Agent** — handles common issues (password reset, account unlock)
3. **Level 2 Specialist** — handles technical issues
4. **Manager** — handles everything else (complaints, refunds)

Each level either handles the ticket OR passes it up. The customer shouldn't know which level handled it.

### Your Task
1. `SupportHandler` abstract class: `setNext(handler)`, `handle(ticket)` — if can't handle, pass to next
2. `BotHandler`, `Level1Handler`, `Level2Handler`, `ManagerHandler`
3. A `Ticket` with a type (`FAQ`, `ACCOUNT`, `TECHNICAL`, `COMPLAINT`)

<details>
<summary>🔍 Hint</summary>

Each handler has a `next` reference. If THIS handler can't handle it, call `next.handle(ticket)`. If there's no next, the request is unhandled (log an error).

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Chain of Responsibility**

Why: Each level handles a subset of requests. The chain is configured at startup. Adding a new level = add one class, wire it into the chain. No existing handlers change.

</details>

### Starter Code

```java
public enum TicketType { FAQ, ACCOUNT, TECHNICAL, COMPLAINT, BILLING }

public record Ticket(String id, TicketType type, String description) {}

// Abstract handler
public abstract class SupportHandler {
    protected SupportHandler next;

    public SupportHandler setNext(SupportHandler next) {
        this.next = next;
        return next;  // return next for fluent chaining
    }

    public void handle(Ticket ticket) {
        if (canHandle(ticket)) {
            process(ticket);
        } else if (next != null) {
            System.out.println(getHandlerName() + ": Escalating ticket " + ticket.id());
            next.handle(ticket);
        } else {
            System.out.println("UNHANDLED ticket: " + ticket.id());
        }
    }

    protected abstract boolean canHandle(Ticket ticket);
    protected abstract void process(Ticket ticket);
    protected abstract String getHandlerName();
}

public class BotHandler extends SupportHandler {
    @Override
    protected boolean canHandle(Ticket ticket) {
        return ticket.type() == TicketType.FAQ;
    }

    @Override
    protected void process(Ticket ticket) {
        System.out.println("[BOT] Handling FAQ: " + ticket.description());
    }

    @Override protected String getHandlerName() { return "Bot"; }
}

public class Level1Handler extends SupportHandler {
    @Override
    protected boolean canHandle(Ticket ticket) {
        return ticket.type() == TicketType.ACCOUNT;
    }

    @Override
    protected void process(Ticket ticket) {
        System.out.println("[Level 1] Handling account issue: " + ticket.description());
    }

    @Override protected String getHandlerName() { return "Level 1 Agent"; }
}

// TODO: Level2Handler (handles TECHNICAL), ManagerHandler (handles COMPLAINT + BILLING + anything)

// Test:
class Main {
    public static void main(String[] args) {
        // Build the chain
        BotHandler bot = new BotHandler();
        Level1Handler l1 = new Level1Handler();
        // Level2Handler l2 = new Level2Handler();
        // ManagerHandler mgr = new ManagerHandler();

        bot.setNext(l1); // .setNext(l2).setNext(mgr);

        // Process various tickets
        bot.handle(new Ticket("T001", TicketType.FAQ, "How do I reset password?"));
        bot.handle(new Ticket("T002", TicketType.ACCOUNT, "Account locked"));
        bot.handle(new Ticket("T003", TicketType.TECHNICAL, "App crashing on iOS"));
        bot.handle(new Ticket("T004", TicketType.COMPLAINT, "Very unhappy with service"));
    }
}
```

---

## Problem 2: Expense Approval

### Scenario
An employee submits an expense claim. Different levels approve based on amount:
- **Team Lead** — can approve up to ₹5,000
- **Manager** — can approve ₹5,001–₹25,000
- **Director** — can approve ₹25,001–₹100,000
- **CFO** — approves anything above ₹100,000

If nobody in the chain can approve (shouldn't happen with CFO at the end), reject.

### Your Task
1. `Approver` abstract handler
2. `TeamLead`, `Manager`, `Director`, `CFO` handlers
3. Each checks if amount is within their limit; otherwise escalates

### Starter Code

```java
public record ExpenseRequest(String employeeName, double amount, String purpose) {}

public abstract class Approver {
    protected Approver next;
    protected String name;
    protected double maxApprovalLimit;

    public Approver(String name, double maxApprovalLimit) {
        this.name = name;
        this.maxApprovalLimit = maxApprovalLimit;
    }

    public Approver setNext(Approver next) {
        this.next = next;
        return next;
    }

    public void process(ExpenseRequest request) {
        if (request.amount() <= maxApprovalLimit) {
            System.out.printf("[%s] APPROVED ₹%.0f for '%s' by %s%n",
                name, request.amount(), request.purpose(), request.employeeName());
        } else if (next != null) {
            System.out.printf("[%s] Forwarding ₹%.0f to next level...%n", name, request.amount());
            next.process(request);
        } else {
            System.out.printf("REJECTED: ₹%.0f exceeds all approval limits%n", request.amount());
        }
    }
}

public class TeamLead extends Approver {
    public TeamLead()  { super("Team Lead", 5_000); }
}

public class Manager extends Approver {
    public Manager()   { super("Manager", 25_000); }
}

// TODO: Director (100_000), CFO (Double.MAX_VALUE)

// Test:
// TeamLead tl = new TeamLead();
// tl.setNext(new Manager()).setNext(new Director()).setNext(new CFO());
// tl.process(new ExpenseRequest("Alice", 3000, "Team lunch"));
// tl.process(new ExpenseRequest("Bob", 20000, "Laptop"));
// tl.process(new ExpenseRequest("Charlie", 80000, "Conference travel"));
// tl.process(new ExpenseRequest("Dave", 500000, "Server hardware"));
```

---

## Problem 3: HTTP Request Filter Chain

### Scenario
An HTTP server processes requests through a filter chain:
1. **AuthenticationFilter** — rejects if no valid token
2. **RateLimitFilter** — rejects if too many requests from this IP
3. **RequestSizeFilter** — rejects if body > 10MB
4. **Handler** — actually processes the request

Each filter either blocks the request (returns error) or passes it to the next filter.

### Starter Code

```java
public record HttpRequest(String path, String token, String ipAddress, int bodySizeBytes) {}
public record HttpResponse(int statusCode, String body) {
    public static HttpResponse ok(String body)        { return new HttpResponse(200, body); }
    public static HttpResponse unauthorized()          { return new HttpResponse(401, "Unauthorized"); }
    public static HttpResponse tooManyRequests()       { return new HttpResponse(429, "Too Many Requests"); }
    public static HttpResponse payloadTooLarge()       { return new HttpResponse(413, "Payload Too Large"); }
}

public interface Filter {
    HttpResponse doFilter(HttpRequest request, FilterChain chain);
}

public interface FilterChain {
    HttpResponse proceed(HttpRequest request);
}

public class AuthenticationFilter implements Filter {
    private static final String VALID_TOKEN = "Bearer valid-token-123";

    @Override
    public HttpResponse doFilter(HttpRequest request, FilterChain chain) {
        if (!VALID_TOKEN.equals(request.token())) {
            System.out.println("[Auth] REJECTED: invalid token");
            return HttpResponse.unauthorized();
        }
        System.out.println("[Auth] PASSED");
        return chain.proceed(request);  // pass to next filter
    }
}

// TODO: RateLimitFilter (reject if same IP makes >5 requests)
// TODO: RequestSizeFilter (reject if bodySizeBytes > 10_485_760)
// TODO: FilterChainImpl that holds a list of Filter + Handler

// Test:
// chain.proceed(new HttpRequest("/api/data", "Bearer valid-token-123", "192.168.1.1", 1024))
//   → [Auth] PASSED → [RateLimit] PASSED → [Size] PASSED → 200 OK
// chain.proceed(new HttpRequest("/api/data", "invalid-token", "192.168.1.1", 1024))
//   → [Auth] REJECTED → 401
```

---

## Problem 4: Loan Application Processing

### Scenario
A loan application is processed through:
1. **CreditScoreCheck** — score must be ≥ 700
2. **IncomeVerification** — monthly income must be ≥ 3× EMI
3. **DebtRatioCheck** — existing debts must be < 40% of income
4. **LoanApproval** — final approval with loan ID

### Starter Code — Quick Version

```java
public record LoanApplication(String applicant, int creditScore,
                               double monthlyIncome, double requestedEMI,
                               double existingDebt) {}

public abstract class LoanProcessor {
    protected LoanProcessor next;

    public LoanProcessor setNext(LoanProcessor next) { this.next = next; return next; }

    public void process(LoanApplication app) {
        if (check(app)) {
            onPass(app);
            if (next != null) next.process(app);
        } else {
            onFail(app);
        }
    }

    protected abstract boolean check(LoanApplication app);
    protected abstract void onPass(LoanApplication app);
    protected abstract void onFail(LoanApplication app);
}

public class CreditScoreCheck extends LoanProcessor {
    @Override
    protected boolean check(LoanApplication app) { return app.creditScore() >= 700; }

    @Override
    protected void onPass(LoanApplication app) {
        System.out.println("[Credit] PASSED score=" + app.creditScore());
    }

    @Override
    protected void onFail(LoanApplication app) {
        System.out.println("[Credit] REJECTED: score " + app.creditScore() + " < 700");
    }
}

// TODO: IncomeVerification, DebtRatioCheck, LoanApproval
```
