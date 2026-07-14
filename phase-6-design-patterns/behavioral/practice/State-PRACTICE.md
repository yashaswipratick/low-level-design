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
