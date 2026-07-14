# Abstract Factory — Practice Problems
> Goal: Recognize when you need FAMILIES of related objects that must work together.

---

## Problem 1: UI Theme Factory

### Scenario
You're building a desktop application that supports two themes: **Dark Mode** and **Light Mode**.

Each theme needs to create matching UI components:
- `Button` (renders differently in dark vs light)
- `TextField` (different border color, background)
- `Dialog` (different background, text color)

A Dark Mode button looks: black background, white text  
A Light Mode button looks: white background, black text

**The critical rule:** You must NEVER mix a Dark Mode button with a Light Mode dialog — they would look terrible together.

**How do you ensure that all components always come from the same theme?**

### Your Task
1. Which pattern ensures you always get a matching FAMILY of components?
2. Create `UIFactory` interface with `createButton()`, `createTextField()`, `createDialog()`
3. Implement `DarkThemeFactory` and `LightThemeFactory`
4. The application creates all components through ONE factory — impossible to mix themes

<details>
<summary>🔍 Hint 1</summary>
Factory Method creates ONE type of object. Abstract Factory creates a FAMILY of related objects. The key word is "family" — components that belong together.
</details>

<details>
<summary>🔍 Hint 2</summary>
The calling code (Application) should only know about `UIFactory` — not `DarkButton` or `LightButton`. It calls `factory.createButton()` and gets back the right type.
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Abstract Factory**

Why: You need families of objects (all dark, or all light) that must be consistent. Abstract Factory guarantees you can't accidentally mix `DarkButton` with `LightDialog` — the factory creates the entire matching set.

</details>

### Starter Code

```java
// Product interfaces
public interface Button {
    void render();
    void onClick();
}

public interface TextField {
    void render();
    String getText();
}

public interface Dialog {
    void show(String title, String message);
}

// Abstract Factory
public interface UIFactory {
    Button createButton();
    TextField createTextField();
    Dialog createDialog();
}

// Dark Theme — all dark components
public class DarkButton implements Button {
    @Override public void render() { System.out.println("[ Dark Button ]  (black bg, white text)"); }
    @Override public void onClick() { System.out.println("Dark button clicked"); }
}

// TODO: implement DarkTextField, DarkDialog, LightButton, LightTextField, LightDialog

public class DarkThemeFactory implements UIFactory {
    @Override public Button createButton()       { return new DarkButton(); }
    // TODO: createTextField(), createDialog()
    @Override public TextField createTextField() { return null; }
    @Override public Dialog createDialog()       { return null; }
}

// TODO: implement LightThemeFactory

// Application only knows about UIFactory — not about dark or light
public class Application {
    private final Button button;
    private final TextField textField;
    private final Dialog dialog;

    public Application(UIFactory factory) {
        // Gets matching components from factory — can't mix themes
        this.button    = factory.createButton();
        this.textField = factory.createTextField();
        this.dialog    = factory.createDialog();
    }

    public void render() {
        button.render();
        textField.render();
        dialog.show("Welcome", "Hello World");
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        System.out.println("=== Dark Mode ===");
        Application darkApp = new Application(new DarkThemeFactory());
        darkApp.render();

        System.out.println("\n=== Light Mode ===");
        Application lightApp = new Application(new LightThemeFactory());
        lightApp.render();
    }
}
```

---

## Problem 2: Cross-Platform Logging (OS-specific)

### Scenario
Your app needs to write logs and create file paths. The way you do this differs per OS:

| OS | Log write | File separator | Temp dir |
|----|-----------|----------------|---------|
| **Windows** | Windows Event Log | `\` | `C:\Temp\` |
| **Linux** | `/var/log/app.log` | `/` | `/tmp/` |
| **Mac** | `Console.app` | `/` | `/var/tmp/` |

A system utility class creates `LogWriter`, `FilePath`, and `TempDir` — but all three must match the SAME operating system.

**Design a factory that creates the right family of OS utilities.**

### Your Task
1. `OSFactory` interface: `createLogWriter()`, `createFilePath()`, `createTempDir()`
2. `WindowsFactory`, `LinuxFactory`, `MacFactory` implement `OSFactory`
3. A `SystemUtils` class accepts `OSFactory` — works on any OS without change
4. Auto-detect OS using `System.getProperty("os.name")` to pick the factory

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Abstract Factory**

Why: Log writer, file path, and temp dir must always come from the SAME OS family. Mixing a Windows log writer with Linux paths would produce invalid paths.

</details>

### Starter Code

```java
public interface LogWriter {
    void write(String message);
}

public interface FilePath {
    String join(String... parts);
    String separator();
}

public interface TempDir {
    String getPath();
}

public interface OSFactory {
    LogWriter createLogWriter();
    FilePath createFilePath();
    TempDir createTempDir();
}

// Linux implementations
public class LinuxLogWriter implements LogWriter {
    @Override
    public void write(String message) {
        System.out.println("/var/log/app.log >> " + message);
    }
}

public class LinuxFilePath implements FilePath {
    @Override
    public String separator() { return "/"; }

    @Override
    public String join(String... parts) {
        return String.join("/", parts);
    }
}

public class LinuxTempDir implements TempDir {
    @Override
    public String getPath() { return "/tmp/"; }
}

public class LinuxFactory implements OSFactory {
    @Override public LogWriter createLogWriter() { return new LinuxLogWriter(); }
    @Override public FilePath createFilePath()   { return new LinuxFilePath(); }
    @Override public TempDir createTempDir()     { return new LinuxTempDir(); }
}

// TODO: implement WindowsFactory and MacFactory with matching components

// OS auto-detection
public class OSFactoryProvider {
    public static OSFactory getFactory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))   return new WindowsFactory();
        if (os.contains("mac"))   return new MacFactory();
        return new LinuxFactory(); // default
    }
}

// SystemUtils uses any factory — never knows which OS
public class SystemUtils {
    private final LogWriter logWriter;
    private final FilePath filePath;
    private final TempDir tempDir;

    public SystemUtils(OSFactory factory) {
        this.logWriter = factory.createLogWriter();
        this.filePath  = factory.createFilePath();
        this.tempDir   = factory.createTempDir();
    }

    public void logAndWrite(String data) {
        String path = filePath.join(tempDir.getPath(), "output.txt");
        logWriter.write("Writing to: " + path);
    }
}
```

---

## Problem 3: Vehicle Parts Factory

### Scenario
You're building a vehicle assembly simulator. Two factories:
- **CarFactory**: produces `CarEngine` (4-cylinder), `CarWheel` (15-inch), `CarBody` (sedan)
- **TruckFactory**: produces `TruckEngine` (V8), `TruckWheel` (22-inch), `TruckBody` (cargo)

A car engine must go in a car body — not a truck body. Parts from different factories should never be mixed.

### Your Task
Build this with Abstract Factory. An `Assembler` class takes a `VehiclePartsFactory` and assembles a vehicle.

### Starter Code

```java
public interface Engine  { String getType(); }
public interface Wheel   { String getSize(); }
public interface Body    { String getStyle(); }

public interface VehiclePartsFactory {
    Engine createEngine();
    Wheel  createWheel();
    Body   createBody();
}

// Car factory creates matching car parts
public class CarFactory implements VehiclePartsFactory {
    @Override public Engine createEngine() { return () -> "4-Cylinder"; }
    @Override public Wheel  createWheel()  { return () -> "15-inch alloy"; }
    @Override public Body   createBody()   { return () -> "Sedan"; }
}

// TODO: TruckFactory

// Assembler doesn't care which factory — just assembles
public class Assembler {
    public void assemble(VehiclePartsFactory factory) {
        Engine engine = factory.createEngine();
        Wheel  wheel  = factory.createWheel();
        Body   body   = factory.createBody();

        System.out.println("Assembled vehicle:");
        System.out.println("  Engine : " + engine.getType());
        System.out.println("  Wheels : " + wheel.getSize());
        System.out.println("  Body   : " + body.getStyle());
    }
}

// Test:
// new Assembler().assemble(new CarFactory());    → Car parts
// new Assembler().assemble(new TruckFactory());  → Truck parts
```
