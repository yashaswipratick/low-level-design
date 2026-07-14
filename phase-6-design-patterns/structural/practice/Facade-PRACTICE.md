# Facade — Practice Problems
> Goal: Recognize when a system is too complex for callers. Hide it behind a simple interface.

---

## Key Intuition
**Facade = hotel concierge.** You say "I want a taxi to the airport." The concierge calls the taxi company, arranges payment, notifies reception — you do ONE thing, the concierge handles the rest.

---

## Problem 1: Home Theater System

### Scenario
You have a home theater with separate components:

```java
class TV          { void turnOn(); void setInput(String input); void turnOff(); }
class SoundSystem { void turnOn(); void setVolume(int level); void setMode(String mode); void turnOff(); }
class Lights      { void dim(int percent); void turnOn(); }
class Streaming   { void login(String username); void playMovie(String movieId); void stop(); }
```

Every time you want to watch a movie, you do:
```java
lights.dim(30);
tv.turnOn();
tv.setInput("HDMI-1");
soundSystem.turnOn();
soundSystem.setVolume(60);
soundSystem.setMode("SURROUND");
streaming.login("user");
streaming.playMovie("movie-123");
```

That's 8 method calls. For "end movie":
```java
streaming.stop();
soundSystem.turnOff();
tv.turnOff();
lights.turnOn();
```

**You want: `homeTheater.watchMovie("user", "movie-123")` and `homeTheater.endMovie()`**

### Your Task
1. Which pattern reduces complexity for the caller?
2. Create `HomeTheaterFacade` with `watchMovie(String user, String movieId)` and `endMovie()`
3. The facade calls all the subsystems — caller doesn't need to know any of them

<details>
<summary>🔍 Hint</summary>

The caller should only know about `HomeTheaterFacade`. All four subsystem objects are hidden inside the facade. The facade is the "simple front door" to the complex system.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Facade**

Why: Multiple complex subsystems need to be orchestrated. The caller wants a simple interface (`watchMovie`). Facade hides the complexity without eliminating it.

</details>

### Starter Code

```java
// Subsystems (don't modify these)
class TV {
    public void turnOn()              { System.out.println("TV: ON"); }
    public void setInput(String input){ System.out.println("TV: Input → " + input); }
    public void turnOff()             { System.out.println("TV: OFF"); }
}

class SoundSystem {
    public void turnOn()              { System.out.println("Sound: ON"); }
    public void setVolume(int level)  { System.out.println("Sound: Volume → " + level); }
    public void setMode(String mode)  { System.out.println("Sound: Mode → " + mode); }
    public void turnOff()             { System.out.println("Sound: OFF"); }
}

class Lights {
    public void dim(int percent)      { System.out.println("Lights: Dimmed to " + percent + "%"); }
    public void turnOn()              { System.out.println("Lights: Full brightness"); }
}

class StreamingService {
    public void login(String user)    { System.out.println("Streaming: Logged in as " + user); }
    public void playMovie(String id)  { System.out.println("Streaming: Playing " + id); }
    public void stop()                { System.out.println("Streaming: Stopped"); }
}

// FACADE — the simple front door
public class HomeTheaterFacade {
    private final TV tv;
    private final SoundSystem sound;
    private final Lights lights;
    private final StreamingService streaming;

    public HomeTheaterFacade() {
        this.tv        = new TV();
        this.sound     = new SoundSystem();
        this.lights    = new Lights();
        this.streaming = new StreamingService();
    }

    // TODO: implement watchMovie and endMovie
    public void watchMovie(String user, String movieId) {
        System.out.println("--- Starting movie experience ---");
        // call subsystems in the right order
    }

    public void endMovie() {
        System.out.println("--- Ending movie experience ---");
        // shut everything down
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        HomeTheaterFacade homeTheater = new HomeTheaterFacade();
        homeTheater.watchMovie("alice", "movie-inception");
        System.out.println();
        homeTheater.endMovie();
    }
}
```

---

## Problem 2: Computer Startup

### Scenario
A computer startup involves multiple components:

```java
class CPU    { void freeze(); void jump(long position); void execute(); }
class Memory { void load(long position, byte[] data); }
class HardDrive { byte[] read(long sector, int size); }
```

The boot sequence:
1. `cpu.freeze()`
2. `memory.load(BOOT_SECTOR, hardDrive.read(BOOT_SECTOR, SECTOR_SIZE))`
3. `cpu.jump(BOOT_SECTOR)`
4. `cpu.execute()`

Nobody should need to know this sequence. You want: `computer.start()`

### Your Task
Build `ComputerFacade` with a single `start()` method. All boot sequence logic lives inside the facade.

### Starter Code

```java
class CPU {
    public void freeze() { System.out.println("CPU: Frozen"); }
    public void jump(long pos) { System.out.println("CPU: Jumping to " + pos); }
    public void execute() { System.out.println("CPU: Executing"); }
}

class Memory {
    public void load(long pos, byte[] data) {
        System.out.println("Memory: Loaded " + data.length + " bytes at position " + pos);
    }
}

class HardDrive {
    public byte[] read(long sector, int size) {
        System.out.println("HardDrive: Reading sector " + sector);
        return new byte[size];  // simulated data
    }
}

public class ComputerFacade {
    private static final long BOOT_SECTOR = 0L;
    private static final int SECTOR_SIZE  = 512;

    private final CPU cpu;
    private final Memory memory;
    private final HardDrive hardDrive;

    public ComputerFacade() {
        this.cpu       = new CPU();
        this.memory    = new Memory();
        this.hardDrive = new HardDrive();
    }

    // TODO: implement start() — encapsulate the entire boot sequence
    public void start() { }

    // TODO: implement shutdown() — reverse sequence
    public void shutdown() { }
}

// Test:
// new ComputerFacade().start();
// Expected output: CPU frozen, disk read, memory loaded, CPU executes
```

---

## Problem 3: Hotel Booking Facade

### Scenario
Booking a hotel involves several services:

```java
class RoomAvailabilityService { boolean isAvailable(String roomType, LocalDate from, LocalDate to); }
class PaymentService          { String charge(String customerId, double amount); }
class RoomAssignmentService   { String assignRoom(String roomType); }
class EmailService            { void sendConfirmation(String email, String bookingId); }
```

Without a Facade:
```java
boolean available = roomService.isAvailable("DELUXE", checkIn, checkOut);
if (!available) throw new RoomNotAvailableException();
String bookingId = paymentService.charge(customerId, 200.0);
String roomNumber = roomAssignment.assignRoom("DELUXE");
emailService.sendConfirmation(customer.email(), bookingId);
```

With a Facade:
```java
hotelFacade.book(customerId, "DELUXE", checkIn, checkOut);
```

### Your Task
Implement `HotelBookingFacade`. If the room is not available, throw `RoomNotAvailableException`. Otherwise complete the full booking flow.

### Starter Code

```java
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class RoomAvailabilityService {
    public boolean isAvailable(String type, LocalDate from, LocalDate to) {
        System.out.println("Checking availability for " + type);
        return true; // simulate available
    }
}

class PaymentService {
    public String charge(String customerId, double amount) {
        System.out.println("Charged $" + amount + " to customer " + customerId);
        return "BKG-" + System.currentTimeMillis();
    }
}

class RoomAssignmentService {
    public String assignRoom(String type) {
        System.out.println("Assigning " + type + " room");
        return type.equals("DELUXE") ? "Room 501" : "Room 201";
    }
}

class EmailService {
    public void sendConfirmation(String email, String bookingId, String room) {
        System.out.println("Email sent to " + email + ": Booking " + bookingId + " | " + room);
    }
}

public class HotelBookingFacade {
    private final RoomAvailabilityService availability = new RoomAvailabilityService();
    private final PaymentService payment = new PaymentService();
    private final RoomAssignmentService assignment = new RoomAssignmentService();
    private final EmailService email = new EmailService();

    private static final double NIGHTLY_RATE = 200.0;

    public String book(String customerId, String customerEmail, String roomType,
                       LocalDate checkIn, LocalDate checkOut) {
        // TODO: implement the full booking flow
        // 1. Check availability
        // 2. Charge payment (nightly rate * nights)
        // 3. Assign room
        // 4. Send confirmation email
        // 5. Return booking ID
        return null;
    }
}
```
