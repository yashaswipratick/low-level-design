# Visitor — Practice Problems
> Goal: Add new operations to objects WITHOUT modifying their classes.

---

## Key Intuition
**Visitor = tax inspector.** A tax inspector visits different businesses (Restaurant, Shop, Hotel). Each has different tax rules. You don't modify the business — the inspector carries the logic and applies it per business type.

Visitor lets you add NEW operations (reports, exports, calculations) to a class hierarchy without touching any of those classes.

---

## Problem 1: Shopping Cart — Tax Calculation

### Scenario
A shopping cart has different item types:
- `FoodItem` — 0% tax (essential goods)
- `ElectronicsItem` — 18% GST
- `LuxuryItem` — 28% tax + 12% cess

You need to:
1. Calculate total tax for the cart
2. Generate a tax breakdown report
3. Export items as a receipt

Without Visitor: you'd add `calculateTax()`, `generateReport()`, `export()` to EVERY item class. Every new operation means modifying ALL classes.

With Visitor: each new operation is ONE visitor class. Existing item classes never change.

### Your Task
1. `CartItem` interface with `accept(CartVisitor visitor)`
2. `FoodItem`, `ElectronicsItem`, `LuxuryItem` implement `CartItem`
3. `CartVisitor` interface with `visit(FoodItem)`, `visit(ElectronicsItem)`, `visit(LuxuryItem)`
4. `TaxCalculatorVisitor` — calculates and totals tax per item

<details>
<summary>🔍 Hint</summary>

The key is "double dispatch":
- Cart calls `item.accept(taxVisitor)`
- Inside `accept()`, the item calls `visitor.visit(this)` — `this` is the concrete type
- Java picks the right overload: `visit(FoodItem)` vs `visit(ElectronicsItem)`

This ensures the visitor gets the CONCRETE type, not just `CartItem`.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Visitor**

Why: Multiple operations (tax, report, export) on multiple object types (Food, Electronics, Luxury). Adding a new operation = one new visitor class, zero changes to item classes.

</details>

### Starter Code

```java
import java.util.List;

// Element interface — must accept a visitor
public interface CartItem {
    String getName();
    double getPrice();
    void accept(CartVisitor visitor);
}

// Concrete elements
public class FoodItem implements CartItem {
    private final String name;
    private final double price;

    public FoodItem(String name, double price) {
        this.name = name; this.price = price;
    }

    @Override public String getName()  { return name; }
    @Override public double getPrice() { return price; }

    @Override
    public void accept(CartVisitor visitor) {
        visitor.visit(this);  // double dispatch — passes concrete FoodItem
    }
}

public class ElectronicsItem implements CartItem {
    private final String name;
    private final double price;

    public ElectronicsItem(String name, double price) {
        this.name = name; this.price = price;
    }

    @Override public String getName()  { return name; }
    @Override public double getPrice() { return price; }

    @Override
    public void accept(CartVisitor visitor) {
        visitor.visit(this);  // passes concrete ElectronicsItem
    }
}

// TODO: LuxuryItem (28% tax + 12% cess on tax amount)

// Visitor interface
public interface CartVisitor {
    void visit(FoodItem item);
    void visit(ElectronicsItem item);
    void visit(LuxuryItem item);
}

// Concrete Visitor 1: Tax Calculator
public class TaxCalculatorVisitor implements CartVisitor {
    private double totalTax = 0;

    @Override
    public void visit(FoodItem item) {
        double tax = 0;  // 0% tax on food
        totalTax += tax;
        System.out.printf("%-20s ₹%.2f  Tax(0%%): ₹%.2f%n", item.getName(), item.getPrice(), tax);
    }

    @Override
    public void visit(ElectronicsItem item) {
        double tax = item.getPrice() * 0.18;  // 18% GST
        totalTax += tax;
        System.out.printf("%-20s ₹%.2f  Tax(18%%): ₹%.2f%n", item.getName(), item.getPrice(), tax);
    }

    @Override
    public void visit(LuxuryItem item) {
        double baseTax = item.getPrice() * 0.28;
        double cess    = baseTax * 0.12;
        double tax     = baseTax + cess;
        totalTax += tax;
        System.out.printf("%-20s ₹%.2f  Tax(28%%+12%%cess): ₹%.2f%n", item.getName(), item.getPrice(), tax);
    }

    public double getTotalTax() { return totalTax; }
}

// TODO: ReceiptPrinterVisitor — prints a formatted receipt
// TODO: CartExportVisitor — returns CSV string of all items

// Test:
class Main {
    public static void main(String[] args) {
        List<CartItem> cart = List.of(
            new FoodItem("Rice 5kg", 200.0),
            new ElectronicsItem("Bluetooth Speaker", 1500.0),
            new LuxuryItem("Designer Watch", 25000.0),
            new FoodItem("Milk 1L", 60.0),
            new ElectronicsItem("USB Cable", 300.0)
        );

        TaxCalculatorVisitor taxCalc = new TaxCalculatorVisitor();
        cart.forEach(item -> item.accept(taxCalc));
        System.out.printf("%nTotal Tax: ₹%.2f%n", taxCalc.getTotalTax());
    }
}
```

---

## Problem 2: Document Export

### Scenario
A document has elements: `Paragraph`, `Image`, `Table`, `Heading`.

You need to export to:
1. **HTML** — `<h1>`, `<p>`, `<img>`, `<table>`
2. **Markdown** — `#`, plain text, `![](url)`, `|col|col|`
3. **PlainText** — strip all formatting

Without Visitor: add `toHtml()`, `toMarkdown()`, `toPlainText()` to every element class.
With Visitor: `HtmlExportVisitor`, `MarkdownExportVisitor`, `PlainTextExportVisitor`.

### Starter Code

```java
import java.util.List;

public interface DocumentElement {
    void accept(DocumentVisitor visitor);
}

public record Paragraph(String text) implements DocumentElement {
    @Override public void accept(DocumentVisitor visitor) { visitor.visit(this); }
}

public record Heading(String text, int level) implements DocumentElement {
    @Override public void accept(DocumentVisitor visitor) { visitor.visit(this); }
}

public record Image(String url, String altText) implements DocumentElement {
    @Override public void accept(DocumentVisitor visitor) { visitor.visit(this); }
}

public record Table(List<List<String>> rows) implements DocumentElement {
    @Override public void accept(DocumentVisitor visitor) { visitor.visit(this); }
}

public interface DocumentVisitor {
    void visit(Paragraph p);
    void visit(Heading h);
    void visit(Image img);
    void visit(Table table);
}

public class HtmlExportVisitor implements DocumentVisitor {
    private final StringBuilder html = new StringBuilder();

    @Override
    public void visit(Paragraph p) {
        html.append("<p>").append(p.text()).append("</p>\n");
    }

    @Override
    public void visit(Heading h) {
        html.append("<h").append(h.level()).append(">")
            .append(h.text())
            .append("</h").append(h.level()).append(">\n");
    }

    @Override
    public void visit(Image img) {
        html.append("<img src='").append(img.url())
            .append("' alt='").append(img.altText()).append("'/>\n");
    }

    @Override
    public void visit(Table table) {
        html.append("<table>\n");
        for (List<String> row : table.rows()) {
            html.append("  <tr>");
            row.forEach(cell -> html.append("<td>").append(cell).append("</td>"));
            html.append("</tr>\n");
        }
        html.append("</table>\n");
    }

    public String getHtml() { return html.toString(); }
}

// TODO: MarkdownExportVisitor
// Heading → "# text" (one # per level)
// Paragraph → text + blank line
// Image → "![altText](url)"
// Table → "| col | col |\n|---|---|\n| val | val |"

// Test:
class Main {
    public static void main(String[] args) {
        List<DocumentElement> doc = List.of(
            new Heading("Introduction", 1),
            new Paragraph("This is a sample document."),
            new Image("photo.jpg", "A photo"),
            new Table(List.of(
                List.of("Name", "Age"),
                List.of("Alice", "30"),
                List.of("Bob", "25")
            ))
        );

        HtmlExportVisitor htmlExporter = new HtmlExportVisitor();
        doc.forEach(el -> el.accept(htmlExporter));
        System.out.println("=== HTML ===");
        System.out.println(htmlExporter.getHtml());
    }
}
```

---

## Problem 3: Shape Area + Perimeter Calculator

### Scenario
You have shapes: `Circle`, `Rectangle`, `Triangle`. You want to calculate both area AND perimeter — but you don't want to modify the shape classes (they come from a library).

### Starter Code

```java
public interface Shape {
    void accept(ShapeVisitor visitor);
}

public record Circle(double radius) implements Shape {
    @Override public void accept(ShapeVisitor v) { v.visit(this); }
}

public record Rectangle(double width, double height) implements Shape {
    @Override public void accept(ShapeVisitor v) { v.visit(this); }
}

public record Triangle(double a, double b, double c) implements Shape {
    @Override public void accept(ShapeVisitor v) { v.visit(this); }
}

public interface ShapeVisitor {
    void visit(Circle c);
    void visit(Rectangle r);
    void visit(Triangle t);
}

public class AreaCalculator implements ShapeVisitor {
    private double totalArea = 0;

    @Override
    public void visit(Circle c) {
        double area = Math.PI * c.radius() * c.radius();
        totalArea += area;
        System.out.printf("Circle area: %.2f%n", area);
    }

    @Override
    public void visit(Rectangle r) {
        double area = r.width() * r.height();
        totalArea += area;
        System.out.printf("Rectangle area: %.2f%n", area);
    }

    @Override
    public void visit(Triangle t) {
        // Heron's formula
        double s = (t.a() + t.b() + t.c()) / 2;
        double area = Math.sqrt(s * (s-t.a()) * (s-t.b()) * (s-t.c()));
        totalArea += area;
        System.out.printf("Triangle area: %.2f%n", area);
    }

    public double getTotalArea() { return totalArea; }
}

// TODO: PerimeterCalculator visitor
// Circle: 2πr, Rectangle: 2(w+h), Triangle: a+b+c
```
