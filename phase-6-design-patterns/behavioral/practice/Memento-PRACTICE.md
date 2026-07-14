# Memento — Practice Problems
> Goal: Save and restore an object's state WITHOUT exposing its internals.

---

## Key Intuition
**Memento = save game.** You're playing Dark Souls. Before a boss fight, you save the game. You die 10 times. You restore the save. The game is back to exactly where you saved — without knowing HOW the game engine stores all that data internally.

Three roles:
- **Originator**: the object whose state you want to save (e.g., `TextEditor`, `Game`)
- **Memento**: a snapshot of the originator's state at a moment in time (immutable)
- **Caretaker**: holds the history of mementos (e.g., `History`, `UndoManager`)

---

## Problem 1: Text Editor Snapshot

### Scenario
A text editor lets users type text. They can press **Ctrl+Z** (undo) to go back to a previous state.

The editor's state = `{String content, int cursorPosition}`.

The undo history keeps the last 10 states.

### Your Task
1. `EditorMemento` — immutable snapshot of editor state (content + cursor)
2. `TextEditor` (Originator) — can `save()` → creates a memento; `restore(memento)` → restores state
3. `UndoHistory` (Caretaker) — stack of mementos; `push()`, `pop()`

<details>
<summary>🔍 Hint</summary>

The Memento should be a simple data holder — just the state, no methods. The Originator creates and uses Mementos. The Caretaker just stores and retrieves them (doesn't look inside).

```java
// Originator
EditorMemento save() { return new EditorMemento(content, cursor); }
void restore(EditorMemento m) { content = m.content(); cursor = m.cursor(); }
```

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Memento**

Why: You need undo functionality. Memento captures the state as an opaque object. The caretaker stores them without needing to know what's inside. The originator knows how to save and restore its own state.

</details>

### Starter Code

```java
import java.util.ArrayDeque;
import java.util.Deque;

// Memento — immutable snapshot
public record EditorMemento(String content, int cursorPosition) {}

// Originator — the object we want to save/restore
public class TextEditor {
    private String content = "";
    private int cursorPosition = 0;

    public void type(String text) {
        content = content.substring(0, cursorPosition) + text + content.substring(cursorPosition);
        cursorPosition += text.length();
    }

    public void delete(int chars) {
        int from = Math.max(0, cursorPosition - chars);
        content = content.substring(0, from) + content.substring(cursorPosition);
        cursorPosition = from;
    }

    // Save current state to a memento
    public EditorMemento save() {
        System.out.println("[Save] State saved: '" + content + "' cursor@" + cursorPosition);
        return new EditorMemento(content, cursorPosition);
    }

    // Restore state from a memento
    public void restore(EditorMemento memento) {
        this.content = memento.content();
        this.cursorPosition = memento.cursorPosition();
        System.out.println("[Restore] State restored: '" + content + "' cursor@" + cursorPosition);
    }

    public String getContent() { return content; }
    public int getCursor()     { return cursorPosition; }
}

// Caretaker — manages undo history
public class UndoHistory {
    private final Deque<EditorMemento> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 10;

    public void push(EditorMemento memento) {
        if (history.size() >= MAX_HISTORY) {
            history.removeLast();  // remove oldest when at capacity
        }
        history.push(memento);
    }

    public EditorMemento pop() {
        return history.isEmpty() ? null : history.pop();
    }

    public boolean hasHistory() { return !history.isEmpty(); }
}

// Test:
class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        UndoHistory history = new UndoHistory();

        // User types and saves state at each step
        history.push(editor.save());  // save empty state
        editor.type("Hello");
        System.out.println("After 'Hello': " + editor.getContent());

        history.push(editor.save());  // save "Hello"
        editor.type(" World");
        System.out.println("After ' World': " + editor.getContent());

        history.push(editor.save());  // save "Hello World"
        editor.type("!!!");
        System.out.println("After '!!!': " + editor.getContent());

        // Undo: go back to "Hello World"
        editor.restore(history.pop());
        System.out.println("After undo: " + editor.getContent());

        // Undo: go back to "Hello"
        editor.restore(history.pop());
        System.out.println("After undo: " + editor.getContent());

        // Undo: go back to empty
        editor.restore(history.pop());
        System.out.println("After undo: " + editor.getContent());
    }
}
```

### Expected Output
```
[Save] State saved: '' cursor@0
After 'Hello': Hello
[Save] State saved: 'Hello' cursor@5
After ' World': Hello World
[Save] State saved: 'Hello World' cursor@11
After '!!!': Hello World!!!
[Restore] State restored: 'Hello World' cursor@11
After undo: Hello World
[Restore] State restored: 'Hello' cursor@5
After undo: Hello
[Restore] State restored: '' cursor@0
After undo: 
```

---

## Problem 2: Game Save/Load

### Scenario
In a game, the player has a `GameState`:
- `level` (int)
- `health` (int)
- `score` (long)
- `position` (x, y coordinates)

The game allows saving at "checkpoints" and loading the last save on death.

### Your Task
1. `GameStateMemento` — snapshot of the game state
2. `Game` (Originator) — creates and restores mementos
3. `CheckpointManager` (Caretaker) — stores saves by checkpoint name; can load by name or load most recent

### Starter Code

```java
import java.util.HashMap;
import java.util.Map;

public record GameStateMemento(int level, int health, long score, double x, double y) {}

public class Game {
    private int level = 1;
    private int health = 100;
    private long score = 0;
    private double x = 0, y = 0;

    // Simulate player progress
    public void play() {
        level++;
        score += 1000;
        x += 50; y += 30;
        health -= 20;
        System.out.printf("Playing... Level=%d HP=%d Score=%d Pos=(%.0f,%.0f)%n",
            level, health, score, x, y);
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Took " + damage + " damage! HP=" + health);
        if (health <= 0) System.out.println("DEAD!");
    }

    public GameStateMemento save() {
        System.out.printf("[SAVE] L=%d HP=%d Score=%d%n", level, health, score);
        return new GameStateMemento(level, health, score, x, y);
    }

    public void load(GameStateMemento m) {
        level = m.level(); health = m.health();
        score = m.score(); x = m.x(); y = m.y();
        System.out.printf("[LOAD] Restored: L=%d HP=%d Score=%d%n", level, health, score);
    }
}

public class CheckpointManager {
    private final Map<String, GameStateMemento> saves = new HashMap<>();
    private GameStateMemento lastSave;

    public void save(String checkpointName, GameStateMemento memento) {
        saves.put(checkpointName, memento);
        lastSave = memento;
        System.out.println("Checkpoint saved: " + checkpointName);
    }

    public GameStateMemento load(String checkpointName) {
        GameStateMemento m = saves.get(checkpointName);
        if (m == null) throw new IllegalArgumentException("No checkpoint: " + checkpointName);
        return m;
    }

    public GameStateMemento loadLast() {
        if (lastSave == null) throw new IllegalStateException("No save found");
        return lastSave;
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Game game = new Game();
        CheckpointManager cm = new CheckpointManager();

        game.play();
        game.play();
        cm.save("checkpoint-1", game.save());  // Save after level 2

        game.play();
        game.play();
        cm.save("checkpoint-2", game.save());  // Save after level 4

        game.takeDamage(200);  // Die!

        // Load last save
        game.load(cm.loadLast());  // Restore to level 4

        // Load specific checkpoint
        game.load(cm.load("checkpoint-1"));  // Back to level 2
    }
}
```

---

## Problem 3: Browser History (Back Button)

### Scenario
A browser navigates to pages. The user presses:
- **Navigate(url)** — goes to new page, saves current page in back history
- **Back** — goes to previous page (restores from history)
- **Forward** — goes forward if they went back

### Starter Code

```java
import java.util.ArrayDeque;
import java.util.Deque;

public record BrowserMemento(String url, String title) {}

public class Browser {
    private String currentUrl = "about:blank";
    private String currentTitle = "New Tab";

    public void navigate(String url, String title) {
        System.out.println("Navigating to: " + url);
        currentUrl = url;
        currentTitle = title;
    }

    public BrowserMemento save() {
        return new BrowserMemento(currentUrl, currentTitle);
    }

    public void restore(BrowserMemento m) {
        currentUrl = m.url();
        currentTitle = m.title();
        System.out.println("Page: " + currentTitle + " (" + currentUrl + ")");
    }
}

public class BrowserHistory {
    private final Deque<BrowserMemento> backStack    = new ArrayDeque<>();
    private final Deque<BrowserMemento> forwardStack = new ArrayDeque<>();
    private final Browser browser;

    public BrowserHistory(Browser browser) { this.browser = browser; }

    public void navigate(String url, String title) {
        backStack.push(browser.save());  // save current page before leaving
        forwardStack.clear();            // forward history cleared on new navigation
        browser.navigate(url, title);
    }

    public void back() {
        if (backStack.isEmpty()) { System.out.println("No history"); return; }
        forwardStack.push(browser.save());
        browser.restore(backStack.pop());
    }

    public void forward() {
        // TODO: implement forward navigation
    }
}

// Test:
// navigate to google → walmart → amazon → back → back → forward
```
