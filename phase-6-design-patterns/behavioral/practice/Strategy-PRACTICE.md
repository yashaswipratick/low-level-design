# Strategy — Practice Problems
> Goal: Recognize when an algorithm needs to be SWAPPABLE at runtime.

---

## Key Intuition
**Strategy = interchangeable tools.** A carpenter has a toolbox. For screws: screwdriver. For nails: hammer. The carpenter (context) doesn't change — only the TOOL (strategy) swaps out.

**When to think Strategy:** You see a `switch/if-else` on type → replace with Strategy.

---

## Problem 1: Product Sorting

### Scenario
You're building an e-commerce product listing page. Users can sort products by:
- **Price** (low to high or high to low)
- **Name** (A-Z)
- **Rating** (highest first)
- **Relevance** (default)

A junior dev wrote:

```java
public List<Product> sort(List<Product> products, String sortType) {
    if (sortType.equals("PRICE_ASC"))  products.sort(Comparator.comparingDouble(Product::getPrice));
    else if (sortType.equals("NAME"))  products.sort(Comparator.comparing(Product::getName));
    else if (sortType.equals("RATING")) products.sort(Comparator.comparingDouble(Product::getRating).reversed());
    // Adding "TRENDING" requires editing this method
    return products;
}
```

**Every new sort type requires modifying this method. How do you fix it?**

### Your Task
1. Which pattern makes sorting extensible without modifying existing code?
2. Create `SortStrategy` interface: `List<Product> sort(List<Product> products)`
3. Implement `PriceAscStrategy`, `NameStrategy`, `RatingStrategy`
4. `ProductService` accepts a `SortStrategy` and applies it

<details>
<summary>🔍 Hint</summary>

Each sorting algorithm becomes a separate class. `ProductService` doesn't know WHICH one — it just calls `strategy.sort(products)`. Adding "TRENDING" means adding one new class, zero changes to `ProductService`.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Strategy**

Why: The algorithm (sorting) varies independently of the context (ProductService). Strategy encapsulates each algorithm as a replaceable object.

</details>

### Starter Code

```java
import java.util.*;

public record Product(String name, double price, double rating) {}

// Strategy interface
public interface SortStrategy {
    List<Product> sort(List<Product> products);
}

// Concrete strategies
public class PriceAscendingStrategy implements SortStrategy {
    @Override
    public List<Product> sort(List<Product> products) {
        return products.stream()
            .sorted(Comparator.comparingDouble(Product::price))
            .toList();
    }
}

// TODO: NameAlphabeticStrategy and RatingDescendingStrategy

// Context
public class ProductService {
    private SortStrategy sortStrategy;

    public ProductService(SortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    // Strategy can be changed at runtime
    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    public List<Product> getProducts(List<Product> products) {
        return sortStrategy.sort(products);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        List<Product> products = List.of(
            new Product("Laptop", 999.99, 4.5),
            new Product("Apple", 1.99, 4.8),
            new Product("Book", 12.99, 4.2)
        );

        ProductService service = new ProductService(new PriceAscendingStrategy());
        System.out.println("By price: " + service.getProducts(products));

        service.setSortStrategy(new NameAlphabeticStrategy());
        System.out.println("By name: " + service.getProducts(products));

        service.setSortStrategy(new RatingDescendingStrategy());
        System.out.println("By rating: " + service.getProducts(products));
    }
}
```

---

## Problem 2: Navigation Route Calculator

### Scenario
A maps app calculates routes between two points. Users choose:
- **Fastest** — minimize travel time
- **Shortest** — minimize distance
- **Avoid Tolls** — longer but free
- **Scenic** — passes tourist spots

The route-finding algorithm is complex but the CHOICE of which one to use is decided at runtime.

### Your Task
1. `RouteStrategy` interface: `Route calculate(Location from, Location to)`
2. Implement `FastestRouteStrategy`, `ShortestRouteStrategy`, `AvoidTollsStrategy`
3. `Navigator` class holds a `RouteStrategy` and can switch it mid-trip

### Starter Code

```java
public record Location(String name, double lat, double lon) {}
public record Route(String description, double distanceKm, int estimatedMinutes) {}

public interface RouteStrategy {
    Route calculate(Location from, Location to);
}

public class FastestRouteStrategy implements RouteStrategy {
    @Override
    public Route calculate(Location from, Location to) {
        // Simulate fastest route calculation
        double distance = 45.0;
        return new Route("Via Highway (fastest)", distance, 30);
    }
}

public class ShortestRouteStrategy implements RouteStrategy {
    @Override
    public Route calculate(Location from, Location to) {
        return new Route("Via City Center (shortest)", 38.0, 45);
    }
}

// TODO: AvoidTollsStrategy, ScenicRouteStrategy

public class Navigator {
    private RouteStrategy strategy;

    public Navigator(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
        System.out.println("Strategy changed to: " + strategy.getClass().getSimpleName());
    }

    public void navigate(Location from, Location to) {
        Route route = strategy.calculate(from, to);
        System.out.println("Route: " + route.description());
        System.out.println("Distance: " + route.distanceKm() + " km | ETA: " + route.estimatedMinutes() + " min");
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Location home   = new Location("Home", 40.7128, -74.0060);
        Location office = new Location("Office", 40.7580, -73.9855);

        Navigator nav = new Navigator(new FastestRouteStrategy());
        nav.navigate(home, office);

        System.out.println();
        nav.setStrategy(new ShortestRouteStrategy());
        nav.navigate(home, office);
    }
}
```

---

## Problem 3: Tax Calculator

### Scenario
Your e-commerce platform operates in multiple countries. Each country has different tax rules:
- **India**: 18% GST
- **USA**: varies by state (5-10%), simplify to 8%
- **Germany**: 19% VAT
- **UK**: 20% VAT

Without Strategy, every new country means modifying the `OrderService.calculateTax()` method.

### Your Task
1. `TaxStrategy` interface: `double calculateTax(double amount)`
2. Implement `IndiaTaxStrategy`, `UsaTaxStrategy`, `GermanyTaxStrategy`, `UkTaxStrategy`
3. `OrderService` accepts a `TaxStrategy` at construction time

### Starter Code

```java
public interface TaxStrategy {
    double calculateTax(double amount);
    String getCountry();
}

public class IndiaTaxStrategy implements TaxStrategy {
    private static final double GST_RATE = 0.18;

    @Override
    public double calculateTax(double amount) { return amount * GST_RATE; }

    @Override
    public String getCountry() { return "India"; }
}

// TODO: UsaTaxStrategy (8%), GermanyTaxStrategy (19%), UkTaxStrategy (20%)

public class OrderService {
    private final TaxStrategy taxStrategy;

    public OrderService(TaxStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
    }

    public void printOrderSummary(String item, double basePrice) {
        double tax   = taxStrategy.calculateTax(basePrice);
        double total = basePrice + tax;
        System.out.printf("[%s] %s: Base=%.2f Tax=%.2f Total=%.2f%n",
            taxStrategy.getCountry(), item, basePrice, tax, total);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        new OrderService(new IndiaTaxStrategy()).printOrderSummary("Laptop", 1000.0);
        new OrderService(new UsaTaxStrategy()).printOrderSummary("Laptop", 1000.0);
        new OrderService(new GermanyTaxStrategy()).printOrderSummary("Laptop", 1000.0);
    }
}
```

---

## Problem 4: File Compression

### Scenario
Users can compress files using: **ZIP**, **GZIP**, or **BZIP2**. Each uses a completely different algorithm.

### Your Task
Quick practice: `CompressionStrategy` interface with `byte[] compress(byte[] data)`. Implement three strategies (can be simulated). `FileCompressor` accepts the strategy.

```java
public interface CompressionStrategy {
    byte[] compress(byte[] data);
    String getAlgorithmName();
}

public class ZipCompression implements CompressionStrategy {
    @Override
    public byte[] compress(byte[] data) {
        System.out.println("Compressing " + data.length + " bytes using ZIP...");
        return new byte[data.length / 2];  // simulate 50% compression
    }

    @Override
    public String getAlgorithmName() { return "ZIP"; }
}

// TODO: GzipCompression (simulated 60% ratio), Bzip2Compression (simulated 70% ratio)

public class FileCompressor {
    private CompressionStrategy strategy;

    public FileCompressor(CompressionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(CompressionStrategy s) { this.strategy = s; }

    public void compressFile(String filename, byte[] data) {
        byte[] compressed = strategy.compress(data);
        System.out.printf("[%s] %s: %d bytes → %d bytes%n",
            strategy.getAlgorithmName(), filename, data.length, compressed.length);
    }
}
```
