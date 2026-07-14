# Mediator — Practice Problems
> Goal: Centralize communication between objects so they don't reference each other directly.

---

## Key Intuition
**Mediator = air traffic control tower.** Planes don't talk to each other directly ("Hey Flight 101, clear the runway!"). They ALL talk to the tower. The tower coordinates everything. If planes spoke directly, it would be chaos.

**When to think Mediator:** Many objects interact with each other in complex ways → extract all communication logic into one central object.

---

## Problem 1: Chat Room

### Scenario
You're building a group chat. Users send messages.

Without Mediator:
```java
user1.sendMessageTo(user2, "Hi");
user1.sendMessageTo(user3, "Hi");
user1.sendMessageTo(user4, "Hi");
```
Each user knows ALL other users → tightly coupled. Adding a user means updating all others.

With Mediator (ChatRoom):
```java
user1.send("Hi everyone");  // → ChatRoom notifies all other users
```
Users only know about the ChatRoom.

### Your Task
1. `ChatMediator` interface: `void sendMessage(String message, ChatUser from)`, `void addUser(ChatUser user)`
2. `ChatRoom` implements `ChatMediator`
3. `ChatUser` — has a name and a reference to `ChatMediator`; calls mediator to send

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Mediator**

Why: Each user should not know about other users. The ChatRoom (mediator) holds the user list and routes messages. Adding/removing users only changes the mediator — no user code changes.

</details>

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

// Mediator interface
public interface ChatMediator {
    void sendMessage(String message, ChatUser from);
    void addUser(ChatUser user);
}

// Concrete Mediator
public class ChatRoom implements ChatMediator {
    private final List<ChatUser> users = new ArrayList<>();

    @Override
    public void addUser(ChatUser user) {
        users.add(user);
        System.out.println(user.getName() + " joined the chat");
    }

    @Override
    public void sendMessage(String message, ChatUser from) {
        System.out.println("[" + from.getName() + "]: " + message);
        users.stream()
             .filter(u -> !u.equals(from))  // don't send back to sender
             .forEach(u -> u.receive(message, from.getName()));
    }
}

// User — only knows about the mediator, not other users
public class ChatUser {
    private final String name;
    private final ChatMediator mediator;

    public ChatUser(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public String getName() { return name; }

    public void send(String message) {
        mediator.sendMessage(message, this);
    }

    public void receive(String message, String from) {
        System.out.println("    " + name + " received from " + from + ": " + message);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        ChatMediator chatRoom = new ChatRoom();

        ChatUser alice = new ChatUser("Alice", chatRoom);
        ChatUser bob   = new ChatUser("Bob",   chatRoom);
        ChatUser carol = new ChatUser("Carol", chatRoom);

        chatRoom.addUser(alice);
        chatRoom.addUser(bob);
        chatRoom.addUser(carol);

        System.out.println();
        alice.send("Hello everyone!");
        System.out.println();
        bob.send("Hey Alice!");
    }
}
```

---

## Problem 2: Air Traffic Control

### Scenario
Multiple planes want to land at an airport. Only ONE plane can land at a time.

Without Mediator: planes would directly check each other's status.
With Mediator (ControlTower): planes register with the tower; the tower grants/denies landing permission.

### Your Task
1. `AirTrafficMediator` interface: `boolean requestLanding(Aircraft aircraft)`, `void notifyLanded(Aircraft aircraft)`
2. `ControlTower` — tracks if runway is occupied; grants permission when free
3. `Aircraft` — registers with tower; calls `requestLanding()` before landing

### Starter Code

```java
import java.util.ArrayDeque;
import java.util.Queue;

public interface AirTrafficMediator {
    boolean requestLanding(Aircraft aircraft);
    void notifyLanded(Aircraft aircraft);
}

public class ControlTower implements AirTrafficMediator {
    private boolean runwayOccupied = false;
    private final Queue<Aircraft> waitingQueue = new ArrayDeque<>();

    @Override
    public boolean requestLanding(Aircraft aircraft) {
        if (!runwayOccupied) {
            runwayOccupied = true;
            System.out.println("[Tower] " + aircraft.getFlightNumber() + ": CLEARED for landing");
            return true;
        } else {
            waitingQueue.offer(aircraft);
            System.out.println("[Tower] " + aircraft.getFlightNumber() + ": Runway busy. Please hold.");
            return false;
        }
    }

    @Override
    public void notifyLanded(Aircraft aircraft) {
        System.out.println("[Tower] " + aircraft.getFlightNumber() + " has landed. Runway free.");
        runwayOccupied = false;

        // Clear next waiting aircraft
        Aircraft next = waitingQueue.poll();
        if (next != null) {
            System.out.println("[Tower] Calling " + next.getFlightNumber() + " to land...");
            next.land();
        }
    }
}

public class Aircraft {
    private final String flightNumber;
    private final AirTrafficMediator tower;

    public Aircraft(String flightNumber, AirTrafficMediator tower) {
        this.flightNumber = flightNumber;
        this.tower = tower;
    }

    public String getFlightNumber() { return flightNumber; }

    public void land() {
        boolean cleared = tower.requestLanding(this);
        if (cleared) {
            System.out.println(flightNumber + ": Landing now...");
            // simulate landing
            tower.notifyLanded(this);
        }
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        ControlTower tower = new ControlTower();

        Aircraft a1 = new Aircraft("AI-101", tower);
        Aircraft a2 = new Aircraft("BA-202", tower);
        Aircraft a3 = new Aircraft("SQ-303", tower);

        a1.land();  // lands immediately
        a2.land();  // waits — runway busy
        a3.land();  // waits
        // When a1 lands, tower calls a2; when a2 lands, tower calls a3
    }
}
```

---

## Problem 3: Auction House

### Scenario
An auction has: **Bidders** and an **Auctioneer** (mediator).

Bidders make bids. The Auctioneer:
1. Announces the current highest bid to all other bidders
2. When bidding stops (no higher bid after 3 rounds), declares the winner

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

public interface AuctionMediator {
    void placeBid(Bidder bidder, double amount);
    void registerBidder(Bidder bidder);
}

public class Auctioneer implements AuctionMediator {
    private final List<Bidder> bidders = new ArrayList<>();
    private double currentHighestBid = 0;
    private Bidder currentWinner = null;

    @Override
    public void registerBidder(Bidder bidder) {
        bidders.add(bidder);
    }

    @Override
    public void placeBid(Bidder bidder, double amount) {
        if (amount > currentHighestBid) {
            currentHighestBid = amount;
            currentWinner = bidder;
            System.out.printf("[Auctioneer] New highest bid: $%.0f by %s%n", amount, bidder.getName());
            // Notify all OTHER bidders
            bidders.stream()
                   .filter(b -> !b.equals(bidder))
                   .forEach(b -> b.onNewHighestBid(amount, bidder.getName()));
        } else {
            System.out.printf("[Auctioneer] Bid $%.0f by %s is too low (current: $%.0f)%n",
                amount, bidder.getName(), currentHighestBid);
        }
    }

    public void announceWinner() {
        if (currentWinner != null) {
            System.out.printf("[Auctioneer] SOLD! Winner: %s for $%.0f%n",
                currentWinner.getName(), currentHighestBid);
        }
    }
}

public class Bidder {
    private final String name;
    private final AuctionMediator auctioneer;
    private double maxBudget;

    public Bidder(String name, double maxBudget, AuctionMediator auctioneer) {
        this.name = name;
        this.maxBudget = maxBudget;
        this.auctioneer = auctioneer;
    }

    public String getName() { return name; }

    public void bid(double amount) {
        if (amount <= maxBudget) {
            auctioneer.placeBid(this, amount);
        } else {
            System.out.println(name + ": Exceeds my budget, I'm out.");
        }
    }

    public void onNewHighestBid(double amount, String leader) {
        System.out.printf("  [%s] %s is leading at $%.0f%n", name, leader, amount);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Auctioneer auctioneer = new Auctioneer();
        Bidder alice = new Bidder("Alice", 500, auctioneer);
        Bidder bob   = new Bidder("Bob", 750, auctioneer);
        Bidder carol = new Bidder("Carol", 600, auctioneer);

        auctioneer.registerBidder(alice);
        auctioneer.registerBidder(bob);
        auctioneer.registerBidder(carol);

        alice.bid(200);
        bob.bid(350);
        carol.bid(400);
        alice.bid(550);  // over budget — she drops out
        bob.bid(600);
        carol.bid(800);  // over budget — she drops out

        auctioneer.announceWinner();  // Bob wins at $600
    }
}
```
