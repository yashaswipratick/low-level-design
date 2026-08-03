# State — Practice Problems
> Goal: Recognize when an object's BEHAVIOR changes based on its current STATE.

---

## Key Intuition
**State = traffic light.** The same "light" object behaves differently depending on whether it's RED, YELLOW, or GREEN. You press the "next" button: RED→GREEN, GREEN→YELLOW, YELLOW→RED. The logic for what happens when you press "next" lives INSIDE each state, not in a giant if-else.

**Warning sign:** You have a field like `status = "PLACED"` and your methods are full of:
```java
if (status.equals("PLACED")) { ... }
else if (status.equals("CONFIRMED")) { ... }
// This is the smell. Each new state = editing existing code.
```

---

## Problem 1: Traffic Light

### Scenario
A traffic light cycles: **RED** (stop, 60s) → **GREEN** (go, 45s) → **YELLOW** (slow, 5s) → back to RED.

When `light.next()` is called, it moves to the next state.

A junior dev wrote:

```java
public void next() {
    if (state.equals("RED"))    { state = "GREEN";  duration = 45; }
    else if (state.equals("GREEN"))  { state = "YELLOW"; duration = 5; }
    else if (state.equals("YELLOW")) { state = "RED";    duration = 60; }
}
```

**Adding a new state (FLASHING) requires editing `next()`. How do you fix this?**

### Your Task
1. `TrafficLightState` interface: `void next(TrafficLight light)`, `String getColor()`, `int getDurationSeconds()`
2. `RedState`, `GreenState`, `YellowState` — each knows what state comes AFTER it
3. `TrafficLight` (context) — delegates `next()` to the current state

<details>
<summary>🔍 Hint</summary>

Each state implementation of `next()` calls `light.setState(new GreenState())` to transition. The context (`TrafficLight`) just holds `currentState` and delegates to it.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: State**

Why: The traffic light's behavior (which color comes next) depends on its current state. Each state knows its own transitions. Adding "FLASHING" state = add one class, zero changes to existing states.

</details>

### Starter Code

```java
// State interface
public interface TrafficLightState {
    void next(TrafficLight light);
    String getColor();
    int getDurationSeconds();
}

// Concrete states — each knows its own transition
public class RedState implements TrafficLightState {
    @Override
    public void next(TrafficLight light) {
        System.out.println("RED → GREEN");
        light.setState(new GreenState());  // transition to green
    }

    @Override public String getColor()          { return "RED"; }
    @Override public int getDurationSeconds()   { return 60; }
}

public class GreenState implements TrafficLightState {
    @Override
    public void next(TrafficLight light) {
        // TODO: transition to YELLOW
    }

    @Override public String getColor()          { return "GREEN"; }
    @Override public int getDurationSeconds()   { return 45; }
}

// TODO: YellowState → transitions to RED

// Context
public class TrafficLight {
    private TrafficLightState state;

    public TrafficLight() {
        this.state = new RedState();  // starts at RED
    }

    public void setState(TrafficLightState state) {
        this.state = state;
    }

    public void next() {
        state.next(this);  // delegates — doesn't know what state comes next
    }

    public void display() {
        System.out.println("Current: " + state.getColor() + " (" + state.getDurationSeconds() + "s)");
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        TrafficLight light = new TrafficLight();
        light.display();  // RED (60s)
        light.next();
        light.display();  // GREEN (45s)
        light.next();
        light.display();  // YELLOW (5s)
        light.next();
        light.display();  // RED (60s) — cycled back
    }
}
```

---

## Problem 2: ATM Machine

### Scenario
An ATM moves through these states:
- **Idle** — waiting for card insertion
- **CardInserted** — card in, waiting for PIN
- **PINVerified** — PIN correct, waiting for transaction
- **Dispensing** — dispensing cash

Operations:
- `insertCard()` — only valid in Idle state
- `enterPIN(int pin)` — only valid in CardInserted state
- `withdraw(double amount)` — only valid in PINVerified state
- `ejectCard()` — resets to Idle from any state

Invalid operations in the current state → print error message, don't crash.

### Your Task
1. `ATMState` interface: all operation methods
2. `IdleState`, `CardInsertedState`, `PINVerifiedState`, `DispensingState`
3. `ATM` (context) — delegates all operations to current state

### Starter Code

```java
public interface ATMState {
    void insertCard(ATM atm);
    void enterPIN(ATM atm, int pin);
    void withdraw(ATM atm, double amount);
    void ejectCard(ATM atm);
}

public class IdleState implements ATMState {
    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card inserted. Please enter PIN.");
        atm.setState(new CardInsertedState());
    }

    @Override
    public void enterPIN(ATM atm, int pin) {
        System.out.println("Please insert card first.");
    }

    @Override
    public void withdraw(ATM atm, double amount) {
        System.out.println("Please insert card first.");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("No card to eject.");
    }
}

public class CardInsertedState implements ATMState {
    private static final int CORRECT_PIN = 1234;

    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card already inserted.");
    }

    @Override
    public void enterPIN(ATM atm, int pin) {
        if (pin == CORRECT_PIN) {
            System.out.println("PIN correct. Welcome!");
            atm.setState(new PINVerifiedState());
        } else {
            System.out.println("Wrong PIN. Card ejected.");
            atm.setState(new IdleState());
        }
    }

    @Override
    public void withdraw(ATM atm, double amount) {
        System.out.println("Please enter PIN first.");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Card ejected.");
        atm.setState(new IdleState());
    }
}

// TODO: PINVerifiedState, DispensingState

public class ATM {
    private ATMState state;
    private double balance;

    public ATM(double balance) {
        this.balance = balance;
        this.state = new IdleState();
    }

    public void setState(ATMState state) { this.state = state; }
    public double getBalance()           { return balance; }
    public void setBalance(double b)     { this.balance = b; }

    // Delegate all operations to current state
    public void insertCard()          { state.insertCard(this); }
    public void enterPIN(int pin)     { state.enterPIN(this, pin); }
    public void withdraw(double amt)  { state.withdraw(this, amt); }
    public void ejectCard()           { state.ejectCard(this); }
}

// Test:
class Main {
    public static void main(String[] args) {
        ATM atm = new ATM(5000.0);

        atm.withdraw(100);      // error: insert card first
        atm.insertCard();       // card inserted
        atm.insertCard();       // error: already inserted
        atm.enterPIN(9999);     // wrong PIN → ejected
        atm.insertCard();       // insert again
        atm.enterPIN(1234);     // correct
        atm.withdraw(500);      // success
        atm.ejectCard();        // back to idle
    }
}
```

---

## Problem 3: Vending Machine

### Scenario
A vending machine states:
- **Idle** — no money inserted
- **HasMoney** — money inserted, item not selected
- **ItemSelected** — item selected, payment verified
- **Dispensing** — dispensing item

Transitions triggered by: `insertMoney(double)`, `selectItem(String)`, `dispenseItem()`, `cancelAndRefund()`

### Starter Code

```java
public interface VendingMachineState {
    void insertMoney(VendingMachine vm, double amount);
    void selectItem(VendingMachine vm, String item);
    void dispenseItem(VendingMachine vm);
    void cancelAndRefund(VendingMachine vm);
}

public class VendingMachine {
    private VendingMachineState state = new IdleState();
    private double insertedAmount = 0;
    private String selectedItem = null;

    public void setState(VendingMachineState s) { state = s; }
    public double getInsertedAmount()            { return insertedAmount; }
    public void addMoney(double amount)          { insertedAmount += amount; }
    public void refundMoney() {
        System.out.println("Refunding: $" + insertedAmount);
        insertedAmount = 0;
    }
    public void setSelectedItem(String item)     { selectedItem = item; }
    public String getSelectedItem()              { return selectedItem; }

    // Delegates
    public void insertMoney(double amount)  { state.insertMoney(this, amount); }
    public void selectItem(String item)     { state.selectItem(this, item); }
    public void dispenseItem()              { state.dispenseItem(this); }
    public void cancelAndRefund()           { state.cancelAndRefund(this); }
}

// TODO: implement IdleState, HasMoneyState, ItemSelectedState, DispensingState
```

---

## Problem 4: Order Lifecycle (E-Commerce)

### Scenario
Every e-commerce order moves through a lifecycle. Operations vary dramatically by state:
- You can cancel a **Placed** order instantly — but not a **Shipped** order.
- You can return a **Delivered** order — but not a **Placed** one.
- Calling `ship()` on a **Delivered** order makes no sense and must be rejected.

A junior dev wrote:

```java
public void cancel(Order order) {
    if (order.getStatus().equals("PLACED"))         { order.setStatus("CANCELLED"); }
    else if (order.getStatus().equals("CONFIRMED")) { order.setStatus("CANCELLED"); }
    else if (order.getStatus().equals("SHIPPED"))   { throw new IllegalStateException("Cannot cancel shipped order"); }
    else if (order.getStatus().equals("DELIVERED")) { throw new IllegalStateException("Cannot cancel delivered order"); }
    // every new status = edit this method AND every other method
}
```

**Adding a new status ("RETURN_REQUESTED") means editing every operation method. How do you fix it?**

### State Transition Diagram

```
                        cancel()
               ┌──────────────────────────────┐
               ▼                              │
[PLACED] ──confirm()──► [CONFIRMED] ──ship()──► [SHIPPED] ──deliver()──► [DELIVERED]
    │               │                                                           │
    └──cancel()──►  └──cancel()──►                                        requestReturn()
                   [CANCELLED]                                                  │
                   (terminal)                                                   ▼
                                                                     [RETURN_REQUESTED]
                                                                                │
                                                                          approveReturn()
                                                                                │
                                                                                ▼
                                                                          [RETURNED]
                                                                          (terminal)
```

### Your Task
1. `OrderState` interface: `confirm(Order)`, `ship(Order)`, `deliver(Order)`, `cancel(Order)`, `requestReturn(Order)`, `getStatusLabel()`
2. States: `PlacedState`, `ConfirmedState`, `ShippedState`, `DeliveredState`, `CancelledState`, `ReturnRequestedState`, `ReturnedState`
3. `Order` context — holds `orderId`, `customerId`, `totalAmount`, current `OrderState`
4. Terminal states (`CancelledState`, `ReturnedState`) — ALL operations print "Order is already [status]. No further action possible."
5. `Order.getStatusLabel()` returns human-readable current status

### Key Rules
- `confirm()` valid from: `PLACED` only
- `ship()` valid from: `CONFIRMED` only
- `deliver()` valid from: `SHIPPED` only
- `cancel()` valid from: `PLACED`, `CONFIRMED` only
- `requestReturn()` valid from: `DELIVERED` only
- Any invalid operation → print descriptive error, stay in current state

### Starter Code

```java
public interface OrderState {
    void confirm(Order order);
    void ship(Order order);
    void deliver(Order order);
    void cancel(Order order);
    void requestReturn(Order order);
    String getStatusLabel();
}

public class PlacedState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("Order " + order.getOrderId() + " confirmed.");
        order.setState(new ConfirmedState());
    }

    @Override
    public void ship(Order order) {
        System.out.println("Cannot ship — order not yet confirmed.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Cannot deliver — order not yet confirmed.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order " + order.getOrderId() + " cancelled.");
        order.setState(new CancelledState());
    }

    @Override
    public void requestReturn(Order order) {
        System.out.println("Cannot return — order not yet delivered.");
    }

    @Override
    public String getStatusLabel() { return "PLACED"; }
}

// TODO: ConfirmedState, ShippedState, DeliveredState
// TODO: CancelledState (terminal — all ops print "Order already cancelled")
// TODO: ReturnRequestedState, ReturnedState (terminal)

public class Order {
    private final String orderId;
    private final String customerId;
    private final double totalAmount;
    private OrderState state;

    public Order(String orderId, String customerId, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.state = new PlacedState();
    }

    public void setState(OrderState state)  { this.state = state; }
    public String getOrderId()              { return orderId; }
    public String getCustomerId()           { return customerId; }
    public double getTotalAmount()          { return totalAmount; }
    public String getStatusLabel()          { return state.getStatusLabel(); }

    public void confirm()        { state.confirm(this); }
    public void ship()           { state.ship(this); }
    public void deliver()        { state.deliver(this); }
    public void cancel()         { state.cancel(this); }
    public void requestReturn()  { state.requestReturn(this); }

    public void printStatus() {
        System.out.printf("Order [%s] | Customer: %s | Amount: $%.2f | Status: %s%n",
            orderId, customerId, totalAmount, state.getStatusLabel());
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Order order = new Order("ORD-001", "customer-42", 1299.99);

        // Happy path: full lifecycle
        order.printStatus();      // PLACED
        order.confirm();          // → CONFIRMED
        order.ship();             // → SHIPPED
        order.deliver();          // → DELIVERED
        order.requestReturn();    // → RETURN_REQUESTED
        order.printStatus();

        System.out.println();

        // Cancellation path
        Order order2 = new Order("ORD-002", "customer-99", 499.00);
        order2.ship();            // error: not confirmed yet
        order2.cancel();          // → CANCELLED
        order2.cancel();          // error: already cancelled (terminal)
        order2.confirm();         // error: already cancelled (terminal)
    }
}
```

---

## Problem 5: Document Workflow

### Scenario
A document (blog post, policy doc, report) flows through an editorial pipeline. Each state controls what operations are valid:
- **Draft** — author writes, can submit for review or discard
- **InReview** — reviewer reads, can approve or reject back to Draft
- **Approved** — editor approved, can publish or send back to Draft
- **Published** — live on site, can archive
- **Archived** — read-only, can restore to Draft

### State Transition Diagram

```
[DRAFT] ──submitForReview()──► [IN_REVIEW] ──approve()──► [APPROVED] ──publish()──► [PUBLISHED]
   ▲                                │                           │                         │
   │                            reject()               sendBackToDraft()             archive()
   │                                │                           │                         │
   │                                ▼                           ▼                         ▼
   └────────────────────────── [DRAFT] ◄───────────────────── [DRAFT]              [ARCHIVED]
                                                                                         │
                                                                                     restore()
                                                                                         │
                                                                                         ▼
                                                                                      [DRAFT]
```

### Your Task
1. `DocumentState` interface: `submitForReview(Doc)`, `approve(Doc)`, `reject(Doc)`, `publish(Doc)`, `archive(Doc)`, `restore(Doc)`, `getStatusLabel()`
2. States: `DraftState`, `InReviewState`, `ApprovedState`, `PublishedState`, `ArchivedState`
3. `Document` context — holds `title`, `author`, current state — starts in `DraftState`
4. Invalid operations print: `"Cannot [action] — document is currently [STATUS]."`

### Key Rules
- `submitForReview()` valid from: `DRAFT` only
- `approve()` valid from: `IN_REVIEW` only
- `reject()` valid from: `IN_REVIEW` only → back to `DRAFT`
- `publish()` valid from: `APPROVED` only
- `archive()` valid from: `PUBLISHED` only
- `restore()` valid from: `ARCHIVED` only → back to `DRAFT`

### Starter Code

```java
public interface DocumentState {
    void submitForReview(Document doc);
    void approve(Document doc);
    void reject(Document doc);
    void publish(Document doc);
    void archive(Document doc);
    void restore(Document doc);
    String getStatusLabel();
}

public class DraftState implements DocumentState {
    @Override
    public void submitForReview(Document doc) {
        System.out.println("'" + doc.getTitle() + "' submitted for review.");
        doc.setState(new InReviewState());
    }

    @Override
    public void approve(Document doc)  { invalid("approve", doc); }
    @Override
    public void reject(Document doc)   { invalid("reject", doc); }
    @Override
    public void publish(Document doc)  { invalid("publish", doc); }
    @Override
    public void archive(Document doc)  { invalid("archive", doc); }
    @Override
    public void restore(Document doc)  { invalid("restore", doc); }
    @Override
    public String getStatusLabel()     { return "DRAFT"; }

    private void invalid(String action, Document doc) {
        System.out.println("Cannot " + action + " — document is currently " + doc.getStatusLabel() + ".");
    }
}

// TODO: InReviewState, ApprovedState, PublishedState, ArchivedState

public class Document {
    private final String title;
    private final String author;
    private DocumentState state;

    public Document(String title, String author) {
        this.title = title;
        this.author = author;
        this.state = new DraftState();
    }

    public void setState(DocumentState state) { this.state = state; }
    public String getTitle()                  { return title; }
    public String getAuthor()                 { return author; }
    public String getStatusLabel()            { return state.getStatusLabel(); }

    public void submitForReview() { state.submitForReview(this); }
    public void approve()         { state.approve(this); }
    public void reject()          { state.reject(this); }
    public void publish()         { state.publish(this); }
    public void archive()         { state.archive(this); }
    public void restore()         { state.restore(this); }

    public void printStatus() {
        System.out.println("'" + title + "' by " + author + " — Status: " + state.getStatusLabel());
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Document doc = new Document("LLD Design Patterns Guide", "John");

        // Happy path: full pipeline
        doc.printStatus();       // DRAFT
        doc.submitForReview();   // → IN_REVIEW
        doc.approve();           // → APPROVED
        doc.publish();           // → PUBLISHED
        doc.archive();           // → ARCHIVED
        doc.restore();           // → DRAFT

        System.out.println();

        // Rejection and resubmission
        doc.submitForReview();   // → IN_REVIEW
        doc.reject();            // → DRAFT
        doc.submitForReview();   // → IN_REVIEW (resubmit)
        doc.approve();           // → APPROVED

        System.out.println();

        // Invalid operations
        doc.archive();           // error: not yet published
        doc.publish();           // ok → PUBLISHED
        doc.approve();           // error: cannot approve a published doc
    }
}
```

---

## Problem 6: Subscription Billing State Machine *(Hard)*

### Scenario
A SaaS subscription (Walmart+, Netflix) has a complex lifecycle with billing failures, grace periods, and voluntary cancellations:

- **Trial** — 30-day free, no payment
- **Active** — paid, full access
- **PastDue** — payment failed, 7-day grace period
- **Suspended** — grace expired, access blocked, account retained
- **Cancelled** — user cancelled, access until period end
- **Terminated** — permanently closed (terminal — no recovery)

### State Transition Diagram

```
[TRIAL] ──activate()──► [ACTIVE] ──paymentFailed()──► [PAST_DUE] ──pay()──► [ACTIVE]
   │                        │                               │
cancel()                cancel()                  gracePeriodExpired()
   │                        │                               │
   ▼                        ▼                               ▼
[TERMINATED]           [CANCELLED] ──terminate()──► [TERMINATED]        [SUSPENDED]
                                                                              │
                                                                         reactivate()
                                                                              │
                                                                              ▼
                                                                          [ACTIVE]
```

### Your Task
1. `SubscriptionState` interface: `activate(Sub)`, `paymentFailed(Sub)`, `pay(Sub)`, `cancel(Sub)`, `reactivate(Sub)`, `gracePeriodExpired(Sub)`, `terminate(Sub)`, `getStatusLabel()`
2. States: `TrialState`, `ActiveState`, `PastDueState`, `SuspendedState`, `CancelledState`, `TerminatedState`
3. `Subscription` context — holds `userId`, `planName`, `billingAmount`, starts in `TrialState`
4. `TerminatedState` — ALL operations print: `"Account permanently terminated. Please create a new account."`
5. `cancel()` from `TRIAL` → goes directly to `TERMINATED` (no billing period to honour)

### Key Rules
- `activate()` valid from: `TRIAL`, `SUSPENDED` (reactivation path)
- `paymentFailed()` valid from: `ACTIVE` only
- `pay()` valid from: `PAST_DUE` only → back to `ACTIVE`
- `cancel()` valid from: `ACTIVE`, `TRIAL`, `PAST_DUE`
- `reactivate()` valid from: `SUSPENDED` only
- `gracePeriodExpired()` valid from: `PAST_DUE` only
- `terminate()` valid from: `CANCELLED` only

### Starter Code

```java
public interface SubscriptionState {
    void activate(Subscription sub);
    void paymentFailed(Subscription sub);
    void pay(Subscription sub);
    void cancel(Subscription sub);
    void reactivate(Subscription sub);
    void gracePeriodExpired(Subscription sub);
    void terminate(Subscription sub);
    String getStatusLabel();
}

public class TrialState implements SubscriptionState {
    @Override
    public void activate(Subscription sub) {
        System.out.println("Trial converted to active. Billing starts now.");
        sub.setState(new ActiveState());
    }

    @Override
    public void cancel(Subscription sub) {
        System.out.println("Trial cancelled. No charges applied.");
        sub.setState(new TerminatedState());
    }

    @Override public void paymentFailed(Subscription sub)      { System.out.println("No billing during trial."); }
    @Override public void pay(Subscription sub)                { System.out.println("No payment needed during trial."); }
    @Override public void reactivate(Subscription sub)         { System.out.println("Trial is already active."); }
    @Override public void gracePeriodExpired(Subscription sub) { System.out.println("No grace period during trial."); }
    @Override public void terminate(Subscription sub)          { System.out.println("Cannot terminate — cancel first."); }
    @Override public String getStatusLabel()                   { return "TRIAL"; }
}

// TODO: ActiveState, PastDueState, SuspendedState, CancelledState, TerminatedState

public class Subscription {
    private final String userId;
    private final String planName;
    private final double billingAmount;
    private SubscriptionState state;

    public Subscription(String userId, String planName, double billingAmount) {
        this.userId = userId;
        this.planName = planName;
        this.billingAmount = billingAmount;
        this.state = new TrialState();
    }

    public void setState(SubscriptionState state) { this.state = state; }
    public String getUserId()                     { return userId; }
    public String getPlanName()                   { return planName; }
    public double getBillingAmount()              { return billingAmount; }
    public String getStatusLabel()                { return state.getStatusLabel(); }

    public void activate()             { state.activate(this); }
    public void paymentFailed()        { state.paymentFailed(this); }
    public void pay()                  { state.pay(this); }
    public void cancel()               { state.cancel(this); }
    public void reactivate()           { state.reactivate(this); }
    public void gracePeriodExpired()   { state.gracePeriodExpired(this); }
    public void terminate()            { state.terminate(this); }

    public void printStatus() {
        System.out.printf("User [%s] | Plan: %s | $%.2f/mo | Status: %s%n",
            userId, planName, billingAmount, state.getStatusLabel());
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Subscription sub = new Subscription("user-123", "Walmart+ Premium", 12.95);

        // Happy path: Trial → Active → payment failure → recovery
        sub.printStatus();         // TRIAL
        sub.activate();            // → ACTIVE
        sub.paymentFailed();       // → PAST_DUE
        sub.pay();                 // → ACTIVE (recovered)

        System.out.println();

        // Suspension and reactivation
        sub.paymentFailed();       // → PAST_DUE
        sub.gracePeriodExpired();  // → SUSPENDED
        sub.reactivate();          // → ACTIVE

        System.out.println();

        // Cancellation and termination
        sub.cancel();              // → CANCELLED
        sub.terminate();           // → TERMINATED
        sub.activate();            // error: permanently terminated
        sub.reactivate();          // error: permanently terminated
    }
}
```

