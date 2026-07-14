# Flyweight — Practice Problems
> Goal: Share common data across thousands of objects to save memory.

---

## Key Intuition
**Flyweight = shared textbook in a library.** 100 students need "Java Programming." Instead of printing 100 books, the library has 5 copies — students SHARE them. Each student has a bookmark (their own position) but shares the book itself.

**Intrinsic state** = shared, immutable, stored in the flyweight (the book content)
**Extrinsic state** = unique, passed in at call time (the student's bookmark/position)

---

## Problem 1: Chess Game — Share Piece Types

### Scenario
A chess game has 32 pieces. But there are only 6 types: King, Queen, Rook, Bishop, Knight, Pawn.

Each piece type has:
- **Intrinsic state** (same for all pieces of that type): name, symbol, movement rules, image
- **Extrinsic state** (unique per piece): current position (row, col), color (WHITE/BLACK)

Without Flyweight: 32 separate `ChessPiece` objects, each holding the image and movement data (heavy).
With Flyweight: 12 shared `PieceType` objects (6 types × 2 colors). 32 lightweight `PiecePosition` wrappers.

### Your Task
1. `PieceType` (Flyweight) — holds intrinsic state: name, symbol, image path
2. `PieceTypeFactory` — creates and caches `PieceType` objects (max 12: 6 types × 2 colors)
3. `ChessPiece` — the lightweight object with position + reference to shared `PieceType`

<details>
<summary>🔍 Hint 1</summary>

The factory uses a `Map<String, PieceType>` as the pool. Key = "WHITE_QUEEN" or "BLACK_PAWN". If the key exists in the map, return the existing object. Otherwise create one.

</details>

<details>
<summary>🔍 Hint 2</summary>

32 `ChessPiece` objects exist on the board. But they all SHARE 12 `PieceType` objects. `ChessPiece` stores row, col, and a REFERENCE to a shared `PieceType`.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Flyweight**

Why: 32 pieces but only 6 types. Image data (typically 50KB each) would be 32×50KB = 1.6MB without Flyweight. With Flyweight: 12×50KB = 600KB. Savings grow with more pieces (e.g., particles in a game: 10,000 bullets).

</details>

### Starter Code

```java
import java.util.HashMap;
import java.util.Map;

// Flyweight: intrinsic state — SHARED, IMMUTABLE
public class PieceType {
    private final String name;       // "Queen", "Pawn", etc.
    private final String symbol;     // "♛", "♙"
    private final String imagePath;  // "/assets/white-queen.png"
    private final String color;      // "WHITE" or "BLACK"

    public PieceType(String name, String symbol, String color, String imagePath) {
        this.name = name;
        this.symbol = symbol;
        this.color = color;
        this.imagePath = imagePath;
        System.out.println("Created PieceType: " + color + " " + name);  // should print only once per type
    }

    public String getSymbol() { return symbol; }
    public String getName()   { return name; }
    public String getColor()  { return color; }
}

// Factory: creates and caches PieceType objects
public class PieceTypeFactory {
    private static final Map<String, PieceType> cache = new HashMap<>();

    public static PieceType getPieceType(String color, String name) {
        String key = color + "_" + name;
        if (!cache.containsKey(key)) {
            String symbol = getSymbol(color, name);
            cache.put(key, new PieceType(name, symbol, color, "/assets/" + key.toLowerCase() + ".png"));
        }
        return cache.get(key);
    }

    private static String getSymbol(String color, String name) {
        return switch (name + "_" + color) {
            case "KING_WHITE"   -> "♔"; case "KING_BLACK"   -> "♚";
            case "QUEEN_WHITE"  -> "♕"; case "QUEEN_BLACK"  -> "♛";
            case "ROOK_WHITE"   -> "♖"; case "ROOK_BLACK"   -> "♜";
            case "BISHOP_WHITE" -> "♗"; case "BISHOP_BLACK" -> "♝";
            case "KNIGHT_WHITE" -> "♘"; case "KNIGHT_BLACK" -> "♞";
            case "PAWN_WHITE"   -> "♙"; case "PAWN_BLACK"   -> "♟";
            default -> "?";
        };
    }

    public static int getCacheSize() { return cache.size(); }
}

// Lightweight object: extrinsic state — UNIQUE PER PIECE
public class ChessPiece {
    private int row;
    private int col;
    private final PieceType type;  // shared reference — NOT a copy!

    public ChessPiece(int row, int col, PieceType type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public void moveTo(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return type.getSymbol() + " " + type.getColor() + " " + type.getName()
               + " at (" + row + "," + col + ")";
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        // Set up all 32 chess pieces
        // 8 white pawns
        ChessPiece[] pawns = new ChessPiece[8];
        PieceType whitePawn = PieceTypeFactory.getPieceType("WHITE", "PAWN");
        for (int i = 0; i < 8; i++) {
            pawns[i] = new ChessPiece(6, i, whitePawn);  // all share same PieceType!
        }

        // White heavy pieces
        PieceType whiteRook   = PieceTypeFactory.getPieceType("WHITE", "ROOK");
        PieceType whiteKnight = PieceTypeFactory.getPieceType("WHITE", "KNIGHT");
        PieceType whiteQueen  = PieceTypeFactory.getPieceType("WHITE", "QUEEN");
        PieceTypeFactory.getPieceType("WHITE", "ROOK");   // same rook — no new object!
        PieceTypeFactory.getPieceType("WHITE", "KNIGHT"); // same knight — no new object!

        System.out.println("\nCache size: " + PieceTypeFactory.getCacheSize()); // should be 4 (types created so far)

        System.out.println(pawns[0]);  // ♙ WHITE PAWN at (6,0)
        System.out.println(pawns[3]);  // ♙ WHITE PAWN at (6,3)
        System.out.println("Same PieceType object? " + (pawns[0] == pawns[3] || 
            pawns[0].toString().split(" at")[0].equals(pawns[3].toString().split(" at")[0])));
    }
}
```

---

## Problem 2: Forest — Tree Types (Classic Flyweight Example)

### Scenario
You're building a forest simulation. There are 1,000,000 trees. Each tree has:
- **Intrinsic** (shared): species name, texture data (1MB image), color range
- **Extrinsic** (unique): x position, y position, age

Without Flyweight: 1,000,000 × 1MB = 1TB of memory. Impossible.
With Flyweight: 5 tree species × 1MB = 5MB shared. 1,000,000 tiny position objects.

### Your Task
1. `TreeType` (Flyweight): name, texture (simulate as String), color
2. `TreeTypeFactory`: creates and caches tree types
3. `Tree` (lightweight): x, y, age + reference to shared `TreeType`

### Starter Code

```java
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class TreeType {
    private final String species;
    private final String color;
    private final String texture;   // simulate heavy data

    public TreeType(String species, String color, String texture) {
        this.species = species;
        this.color = color;
        this.texture = texture;
        System.out.println("TreeType created: " + species);  // should only print once per species
    }

    public void draw(int x, int y, int age) {
        System.out.printf("Drawing %s at (%d,%d) age=%d color=%s%n", species, x, y, age, color);
    }
}

public class TreeTypeFactory {
    private static final Map<String, TreeType> types = new HashMap<>();

    public static TreeType getTreeType(String species, String color, String texture) {
        // TODO: return cached TreeType or create new one
        return types.computeIfAbsent(species, k -> new TreeType(species, color, texture));
    }

    public static int uniqueTypeCount() { return types.size(); }
}

public class Tree {
    private final int x, y, age;
    private final TreeType type;  // shared, not copied

    public Tree(int x, int y, int age, TreeType type) {
        this.x = x; this.y = y; this.age = age;
        this.type = type;
    }

    public void draw() { type.draw(x, y, age); }
}

public class Forest {
    private final List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, int age, String species, String color, String texture) {
        TreeType type = TreeTypeFactory.getTreeType(species, color, texture);
        trees.add(new Tree(x, y, age, type));
    }

    public void draw() { trees.forEach(Tree::draw); }
}

// Test:
class Main {
    public static void main(String[] args) {
        Forest forest = new Forest();

        // Plant 1000 trees of 3 types — only 3 TreeType objects should be created
        for (int i = 0; i < 500; i++)
            forest.plantTree(i*10, i*5, 10+i, "Oak", "green", "oak-texture-data");
        for (int i = 0; i < 300; i++)
            forest.plantTree(i*8, i*7, 5+i, "Pine", "dark-green", "pine-texture-data");
        for (int i = 0; i < 200; i++)
            forest.plantTree(i*12, i*3, 20+i, "Maple", "red", "maple-texture-data");

        System.out.println("Total trees: " + 1000);
        System.out.println("Unique TreeType objects: " + TreeTypeFactory.uniqueTypeCount());  // should be 3!

        // Draw first 3 trees
        forest.draw();  // (shows all 1000, use first 3 for demo)
    }
}
```

---

## Problem 3: Particle System

### Scenario
A game renders 10,000 particles (bullets, sparks, explosions). Each particle has:
- **Intrinsic** (shared per type): sprite image, color, explosion radius
- **Extrinsic** (unique per particle): x, y velocity, lifetime remaining

There are only 4 particle types: Bullet, Spark, Smoke, Explosion.

### Your Task
1. `ParticleType` (Flyweight): sprite, color, radius
2. `ParticleFactory`: creates/caches up to 4 `ParticleType` objects
3. `Particle` (lightweight): position (x,y), velocity, lifetime + shared `ParticleType`

```java
public class ParticleType {
    private final String name;
    private final String sprite;  // heavy image data
    private final String color;

    public ParticleType(String name, String sprite, String color) {
        this.name = name; this.sprite = sprite; this.color = color;
        System.out.println("ParticleType loaded: " + name);  // costly — should happen once
    }

    public void draw(double x, double y) {
        System.out.printf("[%s/%s] at (%.0f,%.0f)%n", name, color, x, y);
    }
}

public class Particle {
    private double x, y;
    private double vx, vy;      // velocity
    private int lifetime;       // frames remaining
    private final ParticleType type;

    public Particle(double x, double y, double vx, double vy, int lifetime, ParticleType type) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.lifetime = lifetime;
        this.type = type;
    }

    public void update() {
        x += vx; y += vy;
        lifetime--;
    }

    public void draw() { type.draw(x, y); }
    public boolean isDead() { return lifetime <= 0; }
}

// TODO: ParticleFactory with caching
// TODO: ParticleSystem managing a list of Particle, spawning and updating them
```
