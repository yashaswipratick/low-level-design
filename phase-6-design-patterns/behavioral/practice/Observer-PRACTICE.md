# Observer — Practice Problems
> Goal: Recognize when multiple objects need to REACT to a change in one object.

---

## Key Intuition
**Observer = YouTube subscriptions.** A YouTuber (Subject/Observable) posts a video. All subscribers (Observers) get notified. The YouTuber doesn't know WHO subscribed — it just notifies everyone on its list.

**When to think Observer:** "When X changes, multiple things need to know about it."

---

## Problem 1: Weather Station

### Scenario
You're building a weather monitoring system. A `WeatherStation` measures temperature, humidity, and pressure.

Multiple "display boards" show this data:
- `CurrentConditionsDisplay` — shows current temp and humidity
- `StatisticsDisplay` — shows min/max/average temperature
- `ForecastDisplay` — shows weather forecast based on pressure trend

When the weather station gets new data, ALL displays must update automatically.

**How do you notify all displays without the weather station knowing their concrete types?**

### Your Task
1. `WeatherObserver` interface: `void update(double temp, double humidity, double pressure)`
2. `WeatherStation` (Subject): `subscribe()`, `unsubscribe()`, `setMeasurements()` triggers notification
3. `CurrentConditionsDisplay`, `StatisticsDisplay` implement `WeatherObserver`

<details>
<summary>🔍 Hint</summary>

`WeatherStation` keeps a `List<WeatherObserver>`. When `setMeasurements()` is called, it loops through all observers and calls `observer.update(temp, humidity, pressure)`. It never knows the concrete types.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Observer**

Why: Multiple displays need to react to weather data changes. The station doesn't know about displays — it just notifies a list of generic observers. New display types can be added without touching the station.

</details>

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

// Observer interface
public interface WeatherObserver {
    void update(double temperature, double humidity, double pressure);
}

// Subject (Observable)
public class WeatherStation {
    private final List<WeatherObserver> observers = new ArrayList<>();
    private double temperature;
    private double humidity;
    private double pressure;

    public void subscribe(WeatherObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(WeatherObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        observers.forEach(o -> o.update(temperature, humidity, pressure));
    }

    public void setMeasurements(double temperature, double humidity, double pressure) {
        this.temperature = temperature;
        this.humidity    = humidity;
        this.pressure    = pressure;
        System.out.println("--- Weather updated ---");
        notifyObservers();  // all displays get notified automatically
    }
}

// Concrete Observer 1
public class CurrentConditionsDisplay implements WeatherObserver {
    @Override
    public void update(double temp, double humidity, double pressure) {
        System.out.printf("[Current] Temp: %.1f°C | Humidity: %.0f%%%n", temp, humidity);
    }
}

// Concrete Observer 2
public class StatisticsDisplay implements WeatherObserver {
    private double minTemp = Double.MAX_VALUE;
    private double maxTemp = Double.MIN_VALUE;
    private double totalTemp = 0;
    private int count = 0;

    @Override
    public void update(double temp, double humidity, double pressure) {
        totalTemp += temp;
        count++;
        if (temp < minTemp) minTemp = temp;
        if (temp > maxTemp) maxTemp = temp;
        System.out.printf("[Statistics] Min: %.1f | Max: %.1f | Avg: %.1f%n",
            minTemp, maxTemp, totalTemp / count);
    }
}

// TODO: ForecastDisplay — predicts weather based on pressure (rising=improving, falling=rain)

// Test:
class Main {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();

        WeatherObserver current    = new CurrentConditionsDisplay();
        WeatherObserver statistics = new StatisticsDisplay();
        // WeatherObserver forecast = new ForecastDisplay();

        station.subscribe(current);
        station.subscribe(statistics);

        station.setMeasurements(25.0, 65.0, 1013.0);
        station.setMeasurements(27.5, 70.0, 1012.5);
        station.setMeasurements(22.0, 80.0, 1008.0);

        System.out.println("\n-- Removing statistics display --");
        station.unsubscribe(statistics);
        station.setMeasurements(20.0, 75.0, 1005.0);  // only current display notified
    }
}
```

---

## Problem 2: Stock Price Alert

### Scenario
A `StockMarket` tracks prices of stocks (AAPL, GOOGL, etc.).

Investors subscribe to alerts:
- `PortfolioTracker` — recalculates portfolio value on every price change
- `PriceAlertNotifier` — sends SMS when price crosses a user-set threshold
- `TradingBot` — automatically buys/sells based on price movement

When AAPL's price changes from $150 → $160, ALL three react instantly.

### Your Task
1. `StockObserver` interface: `void onPriceChange(String ticker, double oldPrice, double newPrice)`
2. `StockMarket` (Subject): tracks prices per ticker, notifies observers on change
3. `PortfolioTracker` — calculates total value (assume user holds 10 shares of each tracked stock)
4. `PriceAlertNotifier` — alerts when price crosses a threshold

### Starter Code

```java
import java.util.*;

public interface StockObserver {
    void onPriceChange(String ticker, double oldPrice, double newPrice);
}

public class StockMarket {
    private final Map<String, Double> prices = new HashMap<>();
    private final List<StockObserver> observers = new ArrayList<>();

    public void subscribe(StockObserver observer)   { observers.add(observer); }
    public void unsubscribe(StockObserver observer) { observers.remove(observer); }

    public void updatePrice(String ticker, double newPrice) {
        double oldPrice = prices.getOrDefault(ticker, newPrice);
        prices.put(ticker, newPrice);
        if (oldPrice != newPrice) {
            observers.forEach(o -> o.onPriceChange(ticker, oldPrice, newPrice));
        }
    }
}

public class PortfolioTracker implements StockObserver {
    private final Map<String, Double> holdings;  // ticker → shares
    private final Map<String, Double> prices = new HashMap<>();

    public PortfolioTracker(Map<String, Double> holdings) {
        this.holdings = holdings;
    }

    @Override
    public void onPriceChange(String ticker, double oldPrice, double newPrice) {
        prices.put(ticker, newPrice);
        // TODO: calculate and print total portfolio value
        double total = holdings.entrySet().stream()
            .mapToDouble(e -> e.getValue() * prices.getOrDefault(e.getKey(), 0.0))
            .sum();
        System.out.printf("[Portfolio] %s: $%.2f → $%.2f | Total value: $%.2f%n",
            ticker, oldPrice, newPrice, total);
    }
}

public class PriceAlertNotifier implements StockObserver {
    private final String ticker;
    private final double threshold;
    private final String direction;  // "ABOVE" or "BELOW"

    public PriceAlertNotifier(String ticker, double threshold, String direction) {
        this.ticker = ticker;
        this.threshold = threshold;
        this.direction = direction;
    }

    @Override
    public void onPriceChange(String t, double oldPrice, double newPrice) {
        if (!t.equals(ticker)) return;
        boolean triggered = direction.equals("ABOVE") ? newPrice > threshold : newPrice < threshold;
        if (triggered) {
            System.out.printf("[ALERT] %s crossed %s $%.2f — current: $%.2f%n",
                ticker, direction, threshold, newPrice);
        }
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        StockMarket market = new StockMarket();

        market.subscribe(new PortfolioTracker(Map.of("AAPL", 10.0, "GOOGL", 5.0)));
        market.subscribe(new PriceAlertNotifier("AAPL", 155.0, "ABOVE"));

        market.updatePrice("AAPL", 150.0);
        market.updatePrice("GOOGL", 2800.0);
        market.updatePrice("AAPL", 158.0);  // should trigger ABOVE alert
    }
}
```

---

## Problem 3: Shopping Cart Events

### Scenario
A `ShoppingCart` generates events when:
- An item is added
- An item is removed
- The cart is cleared

Multiple components react:
- `PriceSummaryUpdater` — recalculates and displays the total price
- `ItemCountBadge` — updates the item count shown on the cart icon
- `SavingsCalculator` — shows how much the user saved vs. original price

### Your Task
1. `CartObserver` interface: `void onCartChange(CartEvent event, String itemName, double price)`
2. `ShoppingCart` (Subject): notifies on add/remove/clear
3. Implement all three observer classes

### Starter Code

```java
public enum CartEvent { ITEM_ADDED, ITEM_REMOVED, CART_CLEARED }

public interface CartObserver {
    void onCartChange(CartEvent event, String itemName, double price);
}

public class ShoppingCart {
    private final List<CartObserver> observers = new ArrayList<>();
    private final Map<String, Double> items = new HashMap<>();

    public void subscribe(CartObserver o)   { observers.add(o); }

    private void notify(CartEvent event, String name, double price) {
        observers.forEach(o -> o.onCartChange(event, name, price));
    }

    public void addItem(String name, double price) {
        items.put(name, price);
        notify(CartEvent.ITEM_ADDED, name, price);
    }

    public void removeItem(String name) {
        double price = items.remove(name);
        notify(CartEvent.ITEM_REMOVED, name, price);
    }

    public void clear() {
        items.clear();
        notify(CartEvent.CART_CLEARED, "", 0);
    }
}

public class PriceSummaryUpdater implements CartObserver {
    private double total = 0;

    @Override
    public void onCartChange(CartEvent event, String itemName, double price) {
        total = switch (event) {
            case ITEM_ADDED   -> total + price;
            case ITEM_REMOVED -> total - price;
            case CART_CLEARED -> 0;
        };
        System.out.printf("[Price] Cart total: $%.2f%n", total);
    }
}

// TODO: ItemCountBadge — tracks count, prints "Items in cart: N"
// TODO: SavingsCalculator — assume 20% discount on all items, shows savings

// Test:
// cart.addItem("Laptop", 999.99);
// cart.addItem("Mouse", 29.99);
// cart.removeItem("Mouse");
```
