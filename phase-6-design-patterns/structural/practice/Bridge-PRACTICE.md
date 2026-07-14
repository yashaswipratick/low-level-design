# Bridge — Practice Problems
> Goal: Recognize when you have TWO independent dimensions that would cause class explosion with inheritance.

---

## Key Intuition
**Bridge = TV remote vs TV.** The remote (abstraction) can be Basic or Advanced. The TV (implementation) can be Samsung or LG. That's 4 combinations: BasicRemote+Samsung, BasicRemote+LG, AdvancedRemote+Samsung, AdvancedRemote+LG.

WITHOUT Bridge: 4 classes. Add 2 more TV brands and 2 more remote types → 12 classes.
WITH Bridge: 2 remote classes + 4 TV classes = 6 classes. Always `remotes + tvs`, never `remotes × tvs`.

---

## Problem 1: Shape Renderer

### Scenario
You have shapes: **Circle**, **Rectangle**
You have renderers: **VectorRenderer** (SVG-style), **RasterRenderer** (pixel-style)

Without Bridge, you need:
- `VectorCircle`, `VectorRectangle`
- `RasterCircle`, `RasterRectangle`

Adding `Triangle` → 2 more classes. Adding `OpenGLRenderer` → 3 more classes.

**How do you design this so adding a new shape requires 1 class, and adding a new renderer requires 1 class?**

### Your Task
1. `Renderer` interface (implementation hierarchy): `void renderCircle(double x, double y, double r)`, `void renderRectangle(double x, double y, double w, double h)`
2. `VectorRenderer` and `RasterRenderer` implement `Renderer`
3. `Shape` abstract class (abstraction hierarchy): has a `Renderer` field
4. `Circle` and `Rectangle` extend `Shape`, call the renderer

<details>
<summary>🔍 Hint</summary>

The "bridge" is the `renderer` field inside `Shape`. `Shape` holds a reference to `Renderer`. When `Circle.draw()` is called, it delegates to `this.renderer.renderCircle(...)`.

Adding `Triangle`: only create `Triangle extends Shape` — no renderer changes.
Adding `OpenGL`: only create `OpenGLRenderer implements Renderer` — no shape changes.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Bridge**

Why: Two independent axes of variation (shape type AND rendering method) would cause N×M class explosion with inheritance. Bridge connects them via a reference instead.

</details>

### Starter Code

```java
// Implementation hierarchy (Renderer side)
public interface Renderer {
    void renderCircle(double x, double y, double radius);
    void renderRectangle(double x, double y, double width, double height);
}

public class VectorRenderer implements Renderer {
    @Override
    public void renderCircle(double x, double y, double r) {
        System.out.printf("SVG: <circle cx='%.0f' cy='%.0f' r='%.0f'/>%n", x, y, r);
    }

    @Override
    public void renderRectangle(double x, double y, double w, double h) {
        System.out.printf("SVG: <rect x='%.0f' y='%.0f' width='%.0f' height='%.0f'/>%n", x, y, w, h);
    }
}

public class RasterRenderer implements Renderer {
    @Override
    public void renderCircle(double x, double y, double r) {
        System.out.printf("Pixel: Drawing circle at (%.0f,%.0f) radius=%.0f%n", x, y, r);
    }

    @Override
    public void renderRectangle(double x, double y, double w, double h) {
        System.out.printf("Pixel: Drawing rect at (%.0f,%.0f) size=%.0fx%.0f%n", x, y, w, h);
    }
}

// Abstraction hierarchy (Shape side)
public abstract class Shape {
    protected Renderer renderer;  // the "bridge" — injected, not hardcoded

    protected Shape(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract void draw();
    public abstract void resize(double factor);
}

public class Circle extends Shape {
    private double x, y, radius;

    public Circle(double x, double y, double radius, Renderer renderer) {
        super(renderer);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void draw() {
        renderer.renderCircle(x, y, radius);  // delegate to renderer
    }

    @Override
    public void resize(double factor) {
        this.radius *= factor;
    }
}

// TODO: Rectangle extends Shape

// Test:
class Main {
    public static void main(String[] args) {
        Shape vectorCircle  = new Circle(50, 50, 25, new VectorRenderer());
        Shape rasterCircle  = new Circle(50, 50, 25, new RasterRenderer());
        Shape vectorRect    = new Rectangle(10, 10, 100, 50, new VectorRenderer());

        vectorCircle.draw();   // SVG circle
        rasterCircle.draw();   // Pixel circle
        vectorRect.draw();     // SVG rectangle

        vectorCircle.resize(2.0);
        vectorCircle.draw();   // larger circle
    }
}
```

---

## Problem 2: Message + Format

### Scenario
You send messages via two channels: **Email** and **SMS**

Messages come in two formats: **HTML** and **PlainText**

Without Bridge: `HtmlEmail`, `PlainTextEmail`, `HtmlSms`, `PlainTextSms` — 4 classes now, 6 if you add Slack, 8 if you add Markdown.

With Bridge: 2 sender classes + 2 formatter classes = 4 classes, and they compose freely.

### Your Task
1. `MessageFormatter` interface (implementation): `String format(String title, String body)`
2. `HtmlFormatter`, `PlainTextFormatter` implement it
3. `MessageSender` abstract class (abstraction): has a `MessageFormatter` field
4. `EmailSender` and `SmsSender` extend `MessageSender`

### Starter Code

```java
// Implementation: formatters
public interface MessageFormatter {
    String format(String title, String body);
}

public class HtmlFormatter implements MessageFormatter {
    @Override
    public String format(String title, String body) {
        return "<h1>" + title + "</h1><p>" + body + "</p>";
    }
}

public class PlainTextFormatter implements MessageFormatter {
    @Override
    public String format(String title, String body) {
        return title.toUpperCase() + "\n" + body;
    }
}

// Abstraction: senders
public abstract class MessageSender {
    protected final MessageFormatter formatter;  // bridge

    protected MessageSender(MessageFormatter formatter) {
        this.formatter = formatter;
    }

    public abstract void send(String recipient, String title, String body);
}

public class EmailSender extends MessageSender {
    public EmailSender(MessageFormatter formatter) { super(formatter); }

    @Override
    public void send(String recipient, String title, String body) {
        String content = formatter.format(title, body);
        System.out.println("EMAIL to " + recipient + ":\n" + content);
    }
}

// TODO: SmsSender

// Test:
class Main {
    public static void main(String[] args) {
        MessageSender htmlEmail     = new EmailSender(new HtmlFormatter());
        MessageSender plainEmail    = new EmailSender(new PlainTextFormatter());
        MessageSender htmlSms       = new SmsSender(new HtmlFormatter());

        htmlEmail.send("alice@example.com", "Welcome!", "Thanks for joining.");
        System.out.println();
        plainEmail.send("bob@example.com", "Alert", "Your account was accessed.");
        System.out.println();
        htmlSms.send("+1-555-1234", "OTP", "Your code is 123456");
    }
}
```

---

## Problem 3: Remote Control + Device

### Scenario
Remote controls: **BasicRemote** (on/off, volume), **AdvancedRemote** (on/off, volume, channels, mute)
Devices: **TV**, **Radio**, **AirConditioner**

Each remote should work with ANY device. Adding a new device shouldn't require new remote classes.

### Your Task
Build the Bridge: Remote is the abstraction, Device is the implementation.

### Starter Code

```java
// Implementation side
public interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int volume);
    String getStatus();
}

public class TV implements Device {
    private boolean on = false;
    private int volume = 30;

    @Override public boolean isEnabled()  { return on; }
    @Override public void enable()        { on = true; System.out.println("TV ON"); }
    @Override public void disable()       { on = false; System.out.println("TV OFF"); }
    @Override public int getVolume()      { return volume; }
    @Override public void setVolume(int v){ volume = Math.max(0, Math.min(100, v)); }
    @Override public String getStatus()   { return "TV: " + (on ? "ON" : "OFF") + " Vol:" + volume; }
}

// TODO: Radio class implementing Device

// Abstraction side
public class BasicRemote {
    protected Device device;  // the bridge

    public BasicRemote(Device device) { this.device = device; }

    public void power() {
        if (device.isEnabled()) device.disable();
        else device.enable();
    }

    public void volumeUp()   { device.setVolume(device.getVolume() + 10); }
    public void volumeDown() { device.setVolume(device.getVolume() - 10); }
}

public class AdvancedRemote extends BasicRemote {
    public AdvancedRemote(Device device) { super(device); }

    public void mute() {
        device.setVolume(0);
        System.out.println("Muted");
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        TV tv = new TV();
        BasicRemote basicRemote = new BasicRemote(tv);
        basicRemote.power();       // TV ON
        basicRemote.volumeUp();
        System.out.println(tv.getStatus());  // TV: ON Vol:40

        Radio radio = new Radio();
        AdvancedRemote advancedRemote = new AdvancedRemote(radio);
        advancedRemote.power();    // Radio ON
        advancedRemote.mute();     // Muted
        System.out.println(radio.getStatus());
    }
}
```
