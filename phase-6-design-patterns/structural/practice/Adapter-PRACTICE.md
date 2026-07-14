# Adapter — Practice Problems
> Goal: Recognize when two incompatible interfaces need to work together WITHOUT modifying either.

---

## Key Intuition
**Adapter = plug converter.** Your laptop (client) expects a USB-C socket. The projector (service) has HDMI. You can't change either. You use an adapter in the middle.

---

## Problem 1: Legacy Rectangle API

### Scenario
You've inherited an old drawing library with this class:

```java
// OLD library — can't modify
public class OldRectangle {
    public void drawRectangle(int x1, int y1, int x2, int y2) {
        System.out.println("Drawing rectangle: (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
    }
}
```

Your new drawing framework expects shapes to implement:

```java
// YOUR interface — modern coordinate system
public interface Shape {
    void draw(int x, int y, int width, int height);
}
```

You cannot change `OldRectangle` (it's a library JAR). You cannot change `Shape` (100 classes use it).

**How do you make `OldRectangle` work as a `Shape`?**

### Your Task
1. Which pattern solves this?
2. Create `RectangleAdapter` that implements `Shape` and internally uses `OldRectangle`
3. The adapter must convert `(x, y, width, height)` → `(x1, y1, x2, y2)`

<details>
<summary>🔍 Hint</summary>

```
x1 = x,         y1 = y
x2 = x + width, y2 = y + height
```

Create a class that:
- Implements `Shape` (so your system accepts it)
- Contains an `OldRectangle` (so it delegates the actual drawing)

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Adapter**

Why: `OldRectangle` and `Shape` have incompatible interfaces. Adapter bridges them without changing either. When you see "integrate old/third-party code" — think Adapter.

</details>

### Starter Code

```java
// OLD library (do not modify)
public class OldRectangle {
    public void drawRectangle(int x1, int y1, int x2, int y2) {
        System.out.println("Drawing: (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
    }
}

// YOUR modern interface
public interface Shape {
    void draw(int x, int y, int width, int height);
}

// ADAPTER: bridges OldRectangle → Shape
public class RectangleAdapter implements Shape {
    private final OldRectangle oldRectangle;

    public RectangleAdapter(OldRectangle oldRectangle) {
        this.oldRectangle = oldRectangle;
    }

    @Override
    public void draw(int x, int y, int width, int height) {
        // TODO: convert new coordinates to old format and delegate
        int x1 = x;
        int y1 = y;
        int x2 = /* TODO */;
        int y2 = /* TODO */;
        oldRectangle.drawRectangle(x1, y1, x2, y2);
    }
}

// Test: your system only knows about Shape
class DrawingApp {
    public void render(Shape shape) {
        shape.draw(10, 20, 100, 50);  // x=10, y=20, width=100, height=50
    }

    public static void main(String[] args) {
        DrawingApp app = new DrawingApp();
        Shape rectangle = new RectangleAdapter(new OldRectangle());
        app.render(rectangle);
        // Expected: Drawing: (10,20) to (110,70)
    }
}
```

---

## Problem 2: Third-Party Payment Integration

### Scenario
Your system uses this payment interface:

```java
public interface PaymentProcessor {
    boolean processPayment(String userId, double amount, String currency);
    boolean refund(String transactionId, double amount);
}
```

You want to integrate **Stripe** (third-party SDK). Stripe's API looks like:

```java
// Stripe SDK — cannot modify
public class StripeAPI {
    public StripeResponse chargeCard(String customerId, long amountCents, String currencyCode) {
        System.out.println("Stripe charging " + amountCents + " cents to customer " + customerId);
        return new StripeResponse("txn_" + System.currentTimeMillis(), true);
    }

    public boolean refundCharge(String chargeId) {
        System.out.println("Stripe refunding charge: " + chargeId);
        return true;
    }
}

public record StripeResponse(String chargeId, boolean success) {}
```

**Problems:**
- Your system uses `double amount` — Stripe uses `long amountCents`
- Your system uses `userId` — Stripe uses `customerId`
- Your refund takes `transactionId + amount` — Stripe refund takes just `chargeId`

### Your Task
Create `StripePaymentAdapter` that:
1. Implements `PaymentProcessor`
2. Converts `double amount` → `long amountCents` (multiply by 100)
3. Internally uses `StripeAPI`

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Adapter**

Why: Stripe has a different interface from what your system expects. Adapter converts the call without modifying either side. Tomorrow when you add PayPal, you write `PayPalPaymentAdapter` — zero changes to existing code.

</details>

### Starter Code

```java
public class StripePaymentAdapter implements PaymentProcessor {
    private final StripeAPI stripeApi;

    public StripePaymentAdapter(StripeAPI stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public boolean processPayment(String userId, double amount, String currency) {
        // TODO: convert amount (double) → amountCents (long)
        long amountCents = (long)(amount * 100);
        StripeResponse response = stripeApi.chargeCard(userId, amountCents, currency);
        return response.success();
    }

    @Override
    public boolean refund(String transactionId, double amount) {
        // Stripe only needs the chargeId — amount is ignored in this implementation
        return stripeApi.refundCharge(transactionId);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new StripePaymentAdapter(new StripeAPI());
        processor.processPayment("user123", 29.99, "USD");
        processor.refund("txn_12345", 29.99);
    }
}
```

---

## Problem 3: Media Player Adapter

### Scenario
You have a `MediaPlayer` interface and an `AudioPlayer` that plays MP3:

```java
public interface MediaPlayer {
    void play(String filename);
}

public class AudioPlayer implements MediaPlayer {
    public void play(String filename) {
        if (filename.endsWith(".mp3")) {
            System.out.println("Playing MP3: " + filename);
        } else {
            System.out.println("Unsupported format");
        }
    }
}
```

You want to also support MP4 and VLC files. You have a third-party library:

```java
// Third-party — cannot modify
public class AdvancedMediaLibrary {
    public void playMp4(String filename)  { System.out.println("Playing MP4 via AdvancedLib: " + filename); }
    public void playVlc(String filename)  { System.out.println("Playing VLC via AdvancedLib: " + filename); }
}
```

**How do you extend `AudioPlayer` to support MP4/VLC without modifying it?**

### Your Task
1. Create `MediaAdapter` that adapts `AdvancedMediaLibrary` to `MediaPlayer`
2. Upgrade `AudioPlayer` to delegate MP4/VLC calls to the adapter
3. `AudioPlayer.play("video.mp4")` should work transparently

### Starter Code

```java
public class MediaAdapter implements MediaPlayer {
    private final AdvancedMediaLibrary advancedLib = new AdvancedMediaLibrary();
    private final String type;  // "mp4" or "vlc"

    public MediaAdapter(String type) {
        this.type = type;
    }

    @Override
    public void play(String filename) {
        // TODO: call the right method based on type
    }
}

// Upgraded AudioPlayer — delegates to adapter for unsupported formats
public class AudioPlayer implements MediaPlayer {
    @Override
    public void play(String filename) {
        if (filename.endsWith(".mp3")) {
            System.out.println("Playing MP3: " + filename);
        } else if (filename.endsWith(".mp4") || filename.endsWith(".vlc")) {
            // TODO: create the right adapter and delegate
            String ext = filename.substring(filename.lastIndexOf('.') + 1);
            MediaAdapter adapter = new MediaAdapter(ext);
            adapter.play(filename);
        } else {
            System.out.println("Unsupported format: " + filename);
        }
    }
}

// Test:
// AudioPlayer player = new AudioPlayer();
// player.play("song.mp3");    → Playing MP3: song.mp3
// player.play("movie.mp4");   → Playing MP4 via AdvancedLib: movie.mp4
// player.play("clip.vlc");    → Playing VLC via AdvancedLib: clip.vlc
```
