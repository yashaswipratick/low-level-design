# Command — Practice Problems
> Goal: Encapsulate an operation as an object so it can be undone, queued, or logged.

---

## Key Intuition
**Command = a written order in a restaurant.** Waiter writes down "Pasta for table 5" on paper. The paper IS the command. The kitchen EXECUTES it. If the customer changes their mind, you can CANCEL it. The manager can review all orders (audit log).

Key benefit: the one who creates the command is different from the one who executes it.

---

## Problem 1: Text Editor with Undo

### Scenario
You're building a simple text editor. Operations:
- **Write** text at the current position
- **Delete** last N characters
- **Undo** the last operation

Without Command:
```java
editor.write("Hello");
editor.write(" World");
editor.undo();  // How do you know what to undo?? The editor has no history.
```

**How do you track every operation so it can be reversed?**

### Your Task
1. `EditorCommand` interface: `void execute()`, `void undo()`
2. `WriteCommand` — writes text to editor; undo removes it
3. `DeleteCommand` — deletes last N chars; undo restores them
4. `Editor` — maintains the text content
5. `EditorHistory` — stack of commands; `executeCommand()` runs + pushes; `undo()` pops + undoes

<details>
<summary>🔍 Hint</summary>

The command object captures everything needed to both DO and UNDO the action:
- `WriteCommand` stores the text it wrote → undo deletes it
- `DeleteCommand` stores the text it deleted → undo puts it back

`EditorHistory` is a `Deque<EditorCommand>` (stack). Push on execute, pop on undo.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Command**

Why: You need undo/redo. The Command pattern encapsulates each operation as an object that knows both how to execute itself and how to reverse itself.

</details>

### Starter Code

```java
import java.util.ArrayDeque;
import java.util.Deque;

// Receiver: knows how to actually manipulate text
public class Editor {
    private StringBuilder text = new StringBuilder();

    public void write(String addition) {
        text.append(addition);
    }

    public String deleteLastChars(int count) {
        int from = Math.max(0, text.length() - count);
        String deleted = text.substring(from);
        text.delete(from, text.length());
        return deleted;
    }

    public void insertAt(int position, String text) {
        this.text.insert(position, text);
    }

    public String getText() { return text.toString(); }
}

// Command interface
public interface EditorCommand {
    void execute();
    void undo();
}

// Concrete Command: Write
public class WriteCommand implements EditorCommand {
    private final Editor editor;
    private final String text;

    public WriteCommand(Editor editor, String text) {
        this.editor = editor;
        this.text = text;
    }

    @Override
    public void execute() {
        editor.write(text);
    }

    @Override
    public void undo() {
        editor.deleteLastChars(text.length());  // remove what we wrote
    }
}

// Concrete Command: Delete
public class DeleteCommand implements EditorCommand {
    private final Editor editor;
    private final int count;
    private String deletedText;  // saved to restore on undo

    public DeleteCommand(Editor editor, int count) {
        this.editor = editor;
        this.count = count;
    }

    @Override
    public void execute() {
        deletedText = editor.deleteLastChars(count);  // save what we deleted
    }

    @Override
    public void undo() {
        editor.write(deletedText);  // restore deleted text
    }
}

// Invoker: tracks history
public class EditorHistory {
    private final Deque<EditorCommand> history = new ArrayDeque<>();

    public void executeCommand(EditorCommand command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (history.isEmpty()) {
            System.out.println("Nothing to undo");
            return;
        }
        history.pop().undo();
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Editor editor = new Editor();
        EditorHistory history = new EditorHistory();

        history.executeCommand(new WriteCommand(editor, "Hello"));
        System.out.println(editor.getText());  // Hello

        history.executeCommand(new WriteCommand(editor, " World"));
        System.out.println(editor.getText());  // Hello World

        history.executeCommand(new DeleteCommand(editor, 5));
        System.out.println(editor.getText());  // Hello (deleted " World" = 6 chars, oops use 6)

        history.undo();
        System.out.println(editor.getText());  // Hello World (restored)

        history.undo();
        System.out.println(editor.getText());  // Hello

        history.undo();
        System.out.println(editor.getText());  // (empty)
    }
}
```

---

## Problem 2: Smart Home Controller

### Scenario
A smart home controller handles: TV, Air Conditioner, Lights — each with on/off operations.

You want:
1. A single remote that can control any device via generic `execute()`
2. An UNDO button that reverses the last action
3. A MACRO: execute multiple commands in sequence with ONE button press

### Your Task
1. `Command` interface: `void execute()`, `void undo()`
2. `TurnOnTVCommand`, `TurnOffTVCommand`, `TurnOnACCommand`, `SetLightBrightnessCommand`
3. `MacroCommand` — holds a list of commands, executes all on `execute()`, undoes all in reverse on `undo()`
4. `RemoteControl` — slot-based: assign a command to each button; has an UNDO button

### Starter Code

```java
// Receivers
public class TV {
    public void turnOn()  { System.out.println("TV: ON"); }
    public void turnOff() { System.out.println("TV: OFF"); }
}

public class AirConditioner {
    private int temperature = 24;
    public void turnOn()  { System.out.println("AC: ON at " + temperature + "°C"); }
    public void turnOff() { System.out.println("AC: OFF"); }
    public void setTemp(int t) { temperature = t; System.out.println("AC: Temp set to " + t + "°C"); }
}

public class Light {
    public void setBrightness(int level) { System.out.println("Light: Brightness " + level + "%"); }
}

// Command interface
public interface Command {
    void execute();
    void undo();
}

// TV commands
public class TurnOnTVCommand implements Command {
    private final TV tv;
    public TurnOnTVCommand(TV tv) { this.tv = tv; }

    @Override public void execute() { tv.turnOn(); }
    @Override public void undo()    { tv.turnOff(); }
}

// TODO: TurnOffTVCommand, TurnOnACCommand, SetLightBrightnessCommand

// Macro Command — "movie mode": TV on, AC on at 22°C, lights dim to 30%
public class MacroCommand implements Command {
    private final List<Command> commands;

    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        commands.forEach(Command::execute);
    }

    @Override
    public void undo() {
        // Undo in REVERSE order
        List<Command> reversed = new ArrayList<>(commands);
        Collections.reverse(reversed);
        reversed.forEach(Command::undo);
    }
}

// Remote Control with 5 slots
public class RemoteControl {
    private final Command[] slots = new Command[5];
    private Command lastCommand;

    public void setCommand(int slot, Command command) {
        slots[slot] = command;
    }

    public void pressButton(int slot) {
        if (slots[slot] != null) {
            slots[slot].execute();
            lastCommand = slots[slot];
        }
    }

    public void pressUndo() {
        if (lastCommand != null) lastCommand.undo();
    }
}

// Test:
// remote.setCommand(0, new TurnOnTVCommand(tv));
// remote.setCommand(1, movieMacro);
// remote.pressButton(0);  → TV ON
// remote.pressUndo();     → TV OFF
// remote.pressButton(1);  → TV ON + AC ON + Lights dim
// remote.pressUndo();     → all undone in reverse
```

---

## Problem 3: Restaurant Order System

### Scenario
In a restaurant:
- Waiter takes an order (creates Command)
- Order is queued
- Chef executes orders from the queue
- Orders can be cancelled if not yet started

### Your Task
1. `OrderCommand` interface: `execute()`, `cancel()`, `String getDescription()`
2. `CookPastaCommand`, `CookPizzaCommand`, `CookBurgerCommand`
3. `KitchenQueue` — orders wait here; chef pops and executes; orders can be cancelled by ID

```java
import java.util.*;

public interface OrderCommand {
    void execute();
    void cancel();
    String getOrderId();
    String getDescription();
}

public class CookPastaCommand implements OrderCommand {
    private final String orderId;
    private final String customerName;

    public CookPastaCommand(String orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
    }

    @Override public void execute() { System.out.println("Chef cooking Pasta for " + customerName); }
    @Override public void cancel()  { System.out.println("Order " + orderId + " cancelled before cooking"); }
    @Override public String getOrderId()     { return orderId; }
    @Override public String getDescription() { return "Pasta for " + customerName; }
}

// TODO: CookPizzaCommand, CookBurgerCommand

public class KitchenQueue {
    private final Queue<OrderCommand> queue = new LinkedList<>();

    public void addOrder(OrderCommand order) {
        queue.offer(order);
        System.out.println("Order queued: " + order.getDescription());
    }

    public boolean cancelOrder(String orderId) {
        // TODO: remove from queue and call cancel() if found
        for (OrderCommand cmd : queue) {
            if (cmd.getOrderId().equals(orderId)) {
                queue.remove(cmd);
                cmd.cancel();
                return true;
            }
        }
        System.out.println("Order " + orderId + " not found or already cooking");
        return false;
    }

    public void processNext() {
        OrderCommand order = queue.poll();
        if (order != null) order.execute();
        else System.out.println("No orders in queue");
    }
}
```
