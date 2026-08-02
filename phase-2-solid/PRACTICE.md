# SOLID Principles — Practice Problems
> 2–3 problems per principle. Identify the violation → Fix it.

---

## S — Single Responsibility Principle

### Problem S-1: The God Report Service

```java
@Service
public class ReportService {

    private final DataSource dataSource;
    private final JavaMailSender mailSender;

    // Fetches raw data from DB
    public List<Map<String, Object>> fetchSalesData(LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM orders WHERE created_at BETWEEN ? AND ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            // ... parse result set into list
            return new ArrayList<>();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Formats data into an HTML table
    public String formatAsHtml(List<Map<String, Object>> data) {
        StringBuilder html = new StringBuilder("<table>");
        for (Map<String, Object> row : data) {
            html.append("<tr>");
            row.forEach((k, v) -> html.append("<td>").append(v).append("</td>"));
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    // Sends the report via email
    public void emailReport(String toAddress, String htmlContent) {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(toAddress);
            helper.setSubject("Weekly Sales Report");
            helper.setText(htmlContent, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Your task:**
1. How many reasons does `ReportService` have to change? Name each one.
2. Refactor into properly scoped classes. Each class should have exactly one reason to change.

---

<details>
<summary>✅ Fix — S-1</summary>

```java
// Reason 1: DB query logic changes  → SalesDataRepository changes
@Repository
public class SalesDataRepository {
    private final DataSource dataSource;

    public List<Map<String, Object>> fetchSalesData(LocalDate from, LocalDate to) {
        // SQL logic here — only changes when schema/query changes
    }
}

// Reason 2: HTML format changes → ReportFormatter changes
@Component
public class ReportFormatter {
    public String toHtml(List<Map<String, Object>> data) {
        // Formatting logic — only changes when report layout changes
    }
}

// Reason 3: Email config/template changes → ReportEmailSender changes
@Component
public class ReportEmailSender {
    private final JavaMailSender mailSender;

    public void send(String to, String htmlContent) {
        // Email logic — only changes when email provider/template changes
    }
}

// Orchestrator: changes only when the pipeline itself changes
@Service
public class ReportService {
    private final SalesDataRepository repo;
    private final ReportFormatter formatter;
    private final ReportEmailSender emailSender;

    public void generateAndSend(String to, LocalDate from, LocalDate to) {
        List<Map<String, Object>> data = repo.fetchSalesData(from, to);
        String html = formatter.toHtml(data);
        emailSender.send(to, html);
    }
}
```

**Why:** Before the fix, changing the email provider, the HTML layout, OR the SQL query all required touching `ReportService`. Now each change is isolated to one class.
</details>

---

### Problem S-2: The Overloaded Order Controller

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    // Placing an order
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest req) {
        // 1. Validate stock
        if (req.getQuantity() > stockRepository.getAvailable(req.getProductId())) {
            throw new InsufficientStockException();
        }
        // 2. Calculate price with tax
        BigDecimal tax = req.getPrice().multiply(BigDecimal.valueOf(0.18));
        BigDecimal total = req.getPrice().add(tax);

        // 3. Save order
        Order order = new Order(req.getProductId(), req.getQuantity(), total);
        orderRepository.save(order);

        // 4. Send confirmation email
        String body = "Your order #" + order.getId() + " is confirmed. Total: " + total;
        emailClient.send(req.getEmail(), "Order Confirmed", body);

        return ResponseEntity.ok(order);
    }
}
```

**Your task:**
1. The controller is doing 4 distinct things. List each responsibility.
2. Which of these belong in a controller vs. a service vs. a domain object?
3. Refactor so the controller's only job is HTTP in/out.

---

<details>
<summary>✅ Fix — S-2</summary>

```java
// Domain object handles its own construction
public class Order {
    public static Order create(String productId, int qty, BigDecimal total) {
        return new Order(productId, qty, total);
    }
}

// Service owns business logic — the only place that changes when business rules change
@Service
public class OrderService {
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final OrderEmailSender emailSender;

    public Order placeOrder(OrderRequest req) {
        stockRepository.assertSufficientStock(req.getProductId(), req.getQuantity());
        BigDecimal total = PricingCalculator.withTax(req.getPrice());
        Order order = Order.create(req.getProductId(), req.getQuantity(), total);
        orderRepository.save(order);
        emailSender.sendConfirmation(req.getEmail(), order);
        return order;
    }
}

// Controller: HTTP only — changes ONLY when API contract changes
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest req) {
        return ResponseEntity.ok(orderService.placeOrder(req));
    }
}
```

**Rule of thumb:** If your controller has `if` statements for business logic, SRP is broken.
</details>

---

### Problem S-3: The Multi-Tool User Class

```java
public class User {
    private String id;
    private String name;
    private String email;
    private String passwordHash;

    // Data access
    public void save() {
        DatabaseConnection.getInstance().execute(
            "INSERT INTO users VALUES (?, ?, ?)", id, name, email
        );
    }

    // Auth logic
    public boolean checkPassword(String raw) {
        return BCrypt.checkpw(raw, this.passwordHash);
    }

    // Serialization
    public String toJson() {
        return String.format("{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\"}", id, name, email);
    }

    // Validation
    public boolean isValidEmail() {
        return email != null && email.contains("@");
    }
}
```

**Your task:**
1. A `User` object changing its own password, saving itself to DB, and serializing itself — why is this problematic?
2. Refactor using the principle that a domain object should only model state and domain behavior.

---

<details>
<summary>✅ Fix — S-3</summary>

```java
// Pure domain object — only changes when User's business concept changes
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final String passwordHash;

    public boolean matchesPassword(String raw) {
        return BCrypt.checkpw(raw, this.passwordHash);  // domain behavior: belongs here
    }

    // Getters only — no DB, no JSON, no validation framework
}

// Persistence: changes when schema changes
@Repository
public class UserRepository {
    public void save(User user) { /* JDBC/JPA logic */ }
}

// Serialization: changes when API contract changes
@Component
public class UserDtoMapper {
    public UserResponse toResponse(User user) { /* map to DTO */ }
}

// Validation: changes when business rules for valid email change
@Component
public class UserValidator {
    public void validate(RegisterRequest req) {
        if (!req.getEmail().contains("@")) throw new ValidationException("Invalid email");
    }
}
```

**Key insight:** `user.save()` couples domain to infrastructure. If you switch from JDBC to JPA, you shouldn't touch `User`.
</details>

---

---

## O — Open/Closed Principle

### Problem O-1: The Expanding Discount Engine

```java
@Service
public class DiscountService {

    public BigDecimal calculateDiscount(Order order, String customerType) {
        BigDecimal discount = BigDecimal.ZERO;

        if (customerType.equals("PREMIUM")) {
            discount = order.getTotal().multiply(BigDecimal.valueOf(0.20));
        } else if (customerType.equals("STUDENT")) {
            discount = order.getTotal().multiply(BigDecimal.valueOf(0.10));
        } else if (customerType.equals("EMPLOYEE")) {
            discount = order.getTotal().multiply(BigDecimal.valueOf(0.30));
        }
        // PM says next sprint: add SENIOR_CITIZEN (15%), VETERAN (25%)...
        // You will have to come back and modify this method every time

        return discount;
    }
}
```

**Your task:**
1. What must change in this class every time a new customer type is added?
2. Refactor using the Strategy pattern so new discount types can be added without touching `DiscountService`.

---

<details>
<summary>✅ Fix — O-1</summary>

```java
// Abstraction — never changes
public interface DiscountStrategy {
    BigDecimal apply(Order order);
}

// Adding a new type = just a new class, nothing else changes
@Component("PREMIUM")
public class PremiumDiscount implements DiscountStrategy {
    public BigDecimal apply(Order order) {
        return order.getTotal().multiply(BigDecimal.valueOf(0.20));
    }
}

@Component("STUDENT")
public class StudentDiscount implements DiscountStrategy {
    public BigDecimal apply(Order order) {
        return order.getTotal().multiply(BigDecimal.valueOf(0.10));
    }
}

@Component("EMPLOYEE")
public class EmployeeDiscount implements DiscountStrategy {
    public BigDecimal apply(Order order) {
        return order.getTotal().multiply(BigDecimal.valueOf(0.30));
    }
}

// Adding SENIOR_CITIZEN? Just add a new class. DiscountService never changes.
@Component("SENIOR_CITIZEN")
public class SeniorCitizenDiscount implements DiscountStrategy {
    public BigDecimal apply(Order order) {
        return order.getTotal().multiply(BigDecimal.valueOf(0.15));
    }
}

@Service
public class DiscountService {
    private final Map<String, DiscountStrategy> strategies;  // Spring injects by bean name

    public BigDecimal calculateDiscount(Order order, String customerType) {
        return strategies.getOrDefault(customerType, order -> BigDecimal.ZERO)
                         .apply(order);
    }
}
```

**Proof of OCP:** Adding `VETERAN` discount = write 1 new class, 0 lines changed in existing code.
</details>

---

### Problem O-2: The Rigid Notification Dispatcher

```java
@Service
public class NotificationDispatcher {

    public void dispatch(Notification notification) {
        if (notification.getChannel().equals("EMAIL")) {
            String body = "[EMAIL] " + notification.getMessage();
            emailClient.send(notification.getRecipient(), body);

        } else if (notification.getChannel().equals("SMS")) {
            String body = notification.getMessage().substring(0, Math.min(160, notification.getMessage().length()));
            smsGateway.send(notification.getPhone(), body);

        } else if (notification.getChannel().equals("PUSH")) {
            pushService.notify(notification.getDeviceToken(), notification.getMessage());
        }
        // New channel: SLACK, WHATSAPP? Modify this method again.
    }
}
```

**Your task:**
1. Why does adding a new notification channel require modifying `NotificationDispatcher`?
2. How would you use a Factory + Strategy here to comply with OCP?

---

<details>
<summary>✅ Fix — O-2</summary>

```java
// Abstraction
public interface NotificationSender {
    String channel();
    void send(Notification notification);
}

// Each channel is isolated
@Component
public class EmailNotificationSender implements NotificationSender {
    public String channel() { return "EMAIL"; }
    public void send(Notification n) {
        emailClient.send(n.getRecipient(), "[EMAIL] " + n.getMessage());
    }
}

@Component
public class SmsNotificationSender implements NotificationSender {
    public String channel() { return "SMS"; }
    public void send(Notification n) {
        String truncated = n.getMessage().substring(0, Math.min(160, n.getMessage().length()));
        smsGateway.send(n.getPhone(), truncated);
    }
}

// Adding Slack = just this new class, dispatcher untouched
@Component
public class SlackNotificationSender implements NotificationSender {
    public String channel() { return "SLACK"; }
    public void send(Notification n) { slackClient.post(n.getSlackUserId(), n.getMessage()); }
}

@Service
public class NotificationDispatcher {
    private final Map<String, NotificationSender> senders;

    public NotificationDispatcher(List<NotificationSender> all) {
        this.senders = all.stream().collect(toMap(NotificationSender::channel, identity()));
    }

    public void dispatch(Notification notification) {
        NotificationSender sender = senders.get(notification.getChannel());
        if (sender == null) throw new UnsupportedChannelException(notification.getChannel());
        sender.send(notification);
    }
}
```
</details>

---

### Problem O-3: The Hardcoded Export Format

```java
public class DataExporter {

    public byte[] export(List<Record> records, String format) {
        if (format.equals("CSV")) {
            StringBuilder sb = new StringBuilder();
            for (Record r : records) {
                sb.append(r.getId()).append(",").append(r.getName()).append("\n");
            }
            return sb.toString().getBytes();

        } else if (format.equals("JSON")) {
            // JSON serialization
            return objectMapper.writeValueAsBytes(records);

        } else if (format.equals("XML")) {
            // XML serialization
            return xmlMapper.writeValueAsBytes(records);
        }
        throw new UnsupportedFormatException(format);
    }
}
```

**Your task:**
1. Every new export format (XLSX, Parquet, PDF) requires modifying `DataExporter`. Why is this dangerous in a production system?
2. Refactor so `DataExporter` is permanently closed for modification.

---

<details>
<summary>✅ Fix — O-3</summary>

```java
// Abstraction — stable contract
public interface RecordExporter {
    String format();
    byte[] export(List<Record> records);
}

@Component
public class CsvExporter implements RecordExporter {
    public String format() { return "CSV"; }
    public byte[] export(List<Record> records) {
        StringBuilder sb = new StringBuilder();
        records.forEach(r -> sb.append(r.getId()).append(",").append(r.getName()).append("\n"));
        return sb.toString().getBytes();
    }
}

@Component
public class JsonExporter implements RecordExporter {
    private final ObjectMapper objectMapper;
    public String format() { return "JSON"; }
    public byte[] export(List<Record> records) {
        return objectMapper.writeValueAsBytes(records);
    }
}

// Adding XLSX later: write XlsxExporter, register as @Component, done.
// DataExporter never changes again.

@Service
public class DataExporter {
    private final Map<String, RecordExporter> exporters;

    public DataExporter(List<RecordExporter> all) {
        this.exporters = all.stream().collect(toMap(RecordExporter::format, identity()));
    }

    public byte[] export(List<Record> records, String format) {
        return Optional.ofNullable(exporters.get(format))
                       .orElseThrow(() -> new UnsupportedFormatException(format))
                       .export(records);
    }
}
```

**Why dangerous in production:** Each modification to the if-else risks breaking existing paths. With OCP, existing exporters can't be broken by new additions.
</details>

---

---

## L — Liskov Substitution Principle

### Problem L-1: The Read-Only Cache Breaking the Contract

```java
public interface UserRepository {
    User findById(String id);
    void save(User user);
    void delete(String id);
    List<User> findAll();
}

// In-memory read-only cache used in some read paths
public class ReadOnlyUserCache implements UserRepository {

    private final Map<String, User> cache;

    public User findById(String id) {
        return cache.get(id);  // fine
    }

    public List<User> findAll() {
        return new ArrayList<>(cache.values());  // fine
    }

    public void save(User user) {
        throw new UnsupportedOperationException("Cache is read-only");  // 💥 LSP violated
    }

    public void delete(String id) {
        throw new UnsupportedOperationException("Cache is read-only");  // 💥 LSP violated
    }
}

// Caller code — works fine with JpaUserRepository, breaks with ReadOnlyUserCache
public void updateUser(UserRepository repo, User user) {
    repo.save(user);  // throws if repo is actually ReadOnlyUserCache
}
```

**Your task:**
1. Why does `ReadOnlyUserCache` violate LSP even though it "implements" `UserRepository`?
2. Refactor the interface hierarchy so a read-only cache can exist without LSP violations.

---

<details>
<summary>✅ Fix — L-1</summary>

```java
// Split the interface by capability
public interface ReadableUserRepository {
    User findById(String id);
    List<User> findAll();
}

public interface WritableUserRepository {
    void save(User user);
    void delete(String id);
}

// Full interface for classes that do both (composes both)
public interface UserRepository extends ReadableUserRepository, WritableUserRepository { }

// Cache only implements what it CAN honor — no throws, no surprises
public class ReadOnlyUserCache implements ReadableUserRepository {
    private final Map<String, User> cache;

    public User findById(String id) { return cache.get(id); }
    public List<User> findAll() { return new ArrayList<>(cache.values()); }
}

// JPA repo implements full contract
@Repository
public class JpaUserRepository implements UserRepository {
    public User findById(String id) { ... }
    public List<User> findAll() { ... }
    public void save(User user) { ... }
    public void delete(String id) { ... }
}

// Caller is explicit about what it needs
public void updateUser(WritableUserRepository repo, User user) {
    repo.save(user);  // compiler guarantees repo supports save()
}

public List<User> listUsers(ReadableUserRepository repo) {
    return repo.findAll();  // cache can serve this safely
}
```

**LSP test:** Can you swap any `ReadableUserRepository` implementation without code breaking? Yes → LSP satisfied.
</details>

---

### Problem L-2: The Premium Account That Breaks Withdrawals

```java
public class BankAccount {
    protected BigDecimal balance;

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    // Contract: after withdraw, balance is reduced by amount
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException();
        }
        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() { return balance; }
}

// Premium accounts can go negative (overdraft), but also silently cap withdrawals
public class PremiumAccount extends BankAccount {

    private static final BigDecimal OVERDRAFT_LIMIT = BigDecimal.valueOf(-5000);

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal projected = balance.subtract(amount);
        if (projected.compareTo(OVERDRAFT_LIMIT) < 0) {
            // Silently withdraws only up to the overdraft limit — caller gets LESS than requested!
            balance = OVERDRAFT_LIMIT;
        } else {
            balance = projected;
        }
    }
}

// Generic processing code
public void processWithdrawals(List<BankAccount> accounts, BigDecimal amount) {
    for (BankAccount account : accounts) {
        account.withdraw(amount);  // Caller assumes full amount was withdrawn — WRONG for PremiumAccount
        recordTransaction(amount); // Records wrong amount
    }
}
```

**Your task:**
1. The parent's contract says: "withdraw reduces balance by `amount` or throws." How does `PremiumAccount` break this?
2. Fix the design so `PremiumAccount` can support overdraft without violating LSP.

---

<details>
<summary>✅ Fix — L-2</summary>

```java
// Make the contract explicit and return actual withdrawn amount
public abstract class BankAccount {
    protected BigDecimal balance;

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Positive amount required");
        balance = balance.add(amount);
    }

    // Returns the actual amount withdrawn — contract: return value <= requested amount, never negative
    public abstract BigDecimal withdraw(BigDecimal amount);

    public BigDecimal getBalance() { return balance; }
}

public class StandardAccount extends BankAccount {
    @Override
    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) throw new InsufficientFundsException();
        balance = balance.subtract(amount);
        return amount;  // contract honored: full amount withdrawn
    }
}

public class PremiumAccount extends BankAccount {
    private static final BigDecimal OVERDRAFT_LIMIT = BigDecimal.valueOf(-5000);

    @Override
    public BigDecimal withdraw(BigDecimal amount) {
        BigDecimal projected = balance.subtract(amount);
        BigDecimal actual = projected.compareTo(OVERDRAFT_LIMIT) < 0
            ? balance.subtract(OVERDRAFT_LIMIT)  // max available
            : amount;
        balance = balance.subtract(actual);
        return actual;  // contract honored: return reflects what actually happened
    }
}

// Caller code now works correctly for ALL BankAccount subtypes
public void processWithdrawals(List<BankAccount> accounts, BigDecimal amount) {
    for (BankAccount account : accounts) {
        BigDecimal actual = account.withdraw(amount);
        recordTransaction(actual);  // records the real amount
    }
}
```

**Root cause of original violation:** `PremiumAccount` weakened the postcondition — caller expected full `amount` to be withdrawn, got a partial withdrawal with no indication.
</details>

---

### Problem L-3: The Logging Repository That Doesn't Persist

```java
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
}

// Used in tests and supposedly in "dry-run" mode
public class LoggingOrderRepository implements OrderRepository {

    public void save(Order order) {
        System.out.println("Would save: " + order.getId());
        // Does NOT actually save. Callers assume data persists — it doesn't.
    }

    public Optional<Order> findById(String id) {
        // Nothing was ever saved, so always returns empty
        return Optional.empty();
    }
}

// Real usage in some toggle-based code
OrderRepository repo = featureFlag.isDryRun()
    ? new LoggingOrderRepository()
    : new JpaOrderRepository();

repo.save(order);
Order found = repo.findById(order.getId()).orElseThrow();  // throws in dry-run — nobody expected this
```

**Your task:**
1. Is `LoggingOrderRepository` a valid substitution for `OrderRepository`? Why not?
2. How should "dry-run" behavior be designed to avoid this trap?

---

<details>
<summary>✅ Fix — L-3</summary>

```java
// Option A: Decorator pattern — wraps a real repo, adds logging, honors full contract
public class LoggingOrderRepository implements OrderRepository {
    private final OrderRepository delegate;  // real repo underneath

    public LoggingOrderRepository(OrderRepository delegate) {
        this.delegate = delegate;
    }

    public void save(Order order) {
        log.info("Saving order: {}", order.getId());
        delegate.save(order);  // actual save happens — contract honored
        log.info("Saved order: {}", order.getId());
    }

    public Optional<Order> findById(String id) {
        log.info("Finding order: {}", id);
        return delegate.findById(id);  // delegates, returns real result
    }
}

// Option B: dry-run is a separate concern, not a fake repository
@Service
public class OrderService {
    private final OrderRepository repository;
    private final boolean dryRun;

    public void placeOrder(Order order) {
        if (dryRun) {
            log.info("[DRY RUN] Would save order: {}", order.getId());
            return;  // explicit early return, not a broken contract
        }
        repository.save(order);
    }
}

// Usage — LoggingOrderRepository wraps real repo, always safe to substitute
OrderRepository repo = new LoggingOrderRepository(new JpaOrderRepository(em));
```

**Principle:** If a substitute can't fulfill the method's purpose, it's not a valid substitute — model the difference explicitly instead.
</details>

---

---

## I — Interface Segregation Principle

### Problem I-1: The Fat Worker Interface

```java
// One interface to rule them all
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void submitTimesheet();
}

// Human employee — fine, does all of these
public class HumanEmployee implements Worker {
    public void work()           { System.out.println("Working..."); }
    public void eat()            { System.out.println("Eating..."); }
    public void sleep()          { System.out.println("Sleeping..."); }
    public void attendMeeting()  { System.out.println("In meeting..."); }
    public void submitTimesheet(){ System.out.println("Submitting timesheet..."); }
}

// Robot employee — forced to implement human-only methods
public class RobotWorker implements Worker {
    public void work()           { System.out.println("Processing task..."); }
    public void eat()            { throw new UnsupportedOperationException("Robots don't eat"); }
    public void sleep()          { throw new UnsupportedOperationException("Robots don't sleep"); }
    public void attendMeeting()  { System.out.println("Connecting to meeting API..."); }
    public void submitTimesheet(){ throw new UnsupportedOperationException("Robots don't track hours"); }
}
```

**Your task:**
1. Which methods does `RobotWorker` not need? Group the methods by concern.
2. Split the `Worker` interface into focused interfaces. Show how `HumanEmployee` and `RobotWorker` each implement only what they need.

---

<details>
<summary>✅ Fix — I-1</summary>

```java
// Segregated interfaces — each models one distinct capability
public interface Workable       { void work(); }
public interface Feedable       { void eat(); }
public interface Restable       { void sleep(); }
public interface MeetingCapable { void attendMeeting(); }
public interface Trackable      { void submitTimesheet(); }

// Human implements all applicable ones
public class HumanEmployee implements Workable, Feedable, Restable, MeetingCapable, Trackable {
    public void work()            { System.out.println("Working..."); }
    public void eat()             { System.out.println("Eating..."); }
    public void sleep()           { System.out.println("Sleeping..."); }
    public void attendMeeting()   { System.out.println("In meeting..."); }
    public void submitTimesheet() { System.out.println("Submitting timesheet..."); }
}

// Robot only implements what it can actually do — no throws, no empty stubs
public class RobotWorker implements Workable, MeetingCapable {
    public void work()          { System.out.println("Processing task..."); }
    public void attendMeeting() { System.out.println("Connecting to meeting API..."); }
}

// Callers request only what they need
public void assignWork(Workable worker)            { worker.work(); }
public void scheduleMeeting(MeetingCapable worker) { worker.attendMeeting(); }
public void runPayroll(Trackable worker)           { worker.submitTimesheet(); }
```

**ISP benefit:** `runPayroll(robot)` won't even compile — the type system prevents the mistake.
</details>

---

### Problem I-2: The Bloated Repository Interface

```java
public interface ProductRepository {
    Product findById(String id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    void save(Product product);
    void delete(String id);
    long count();
    List<Product> findTopSellers(int limit);      // analytics
    Map<String, Long> countByCategory();          // analytics
    List<Product> findLowStock(int threshold);    // inventory
    void bulkUpdatePrice(String category, BigDecimal factor);  // admin
}

// Public-facing catalog service — only needs read operations
@Service
public class ProductCatalogService {
    private final ProductRepository repository;  // forced to depend on write + analytics + admin methods

    public List<Product> getByCategory(String category) {
        return repository.findByCategory(category);
    }
    // Uses maybe 3 of the 10 methods — yet depends on all of them
}
```

**Your task:**
1. Group the 10 methods by role (read, write, analytics, admin). Show your grouping.
2. Create segregated interfaces and wire `ProductCatalogService` to depend only on what it uses.

---

<details>
<summary>✅ Fix — I-2</summary>

```java
// Read operations — safe for all read-only services
public interface ProductReadRepository {
    Product findById(String id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    long count();
}

// Write operations — only services that mutate products
public interface ProductWriteRepository {
    void save(Product product);
    void delete(String id);
}

// Analytics — only reporting/analytics services
public interface ProductAnalyticsRepository {
    List<Product> findTopSellers(int limit);
    Map<String, Long> countByCategory();
}

// Admin — privileged operations
public interface ProductAdminRepository {
    List<Product> findLowStock(int threshold);
    void bulkUpdatePrice(String category, BigDecimal factor);
}

// Full implementation — one class implements all
@Repository
public class JpaProductRepository implements
        ProductReadRepository, ProductWriteRepository,
        ProductAnalyticsRepository, ProductAdminRepository {
    // implement all methods
}

// Catalog service depends ONLY on what it uses
@Service
public class ProductCatalogService {
    private final ProductReadRepository repository;  // minimal dependency

    public List<Product> getByCategory(String category) {
        return repository.findByCategory(category);
    }
}

// Admin service depends only on admin interface
@Service
public class ProductAdminService {
    private final ProductAdminRepository adminRepository;
    private final ProductWriteRepository writeRepository;
}
```

**Benefit:** Swapping `ProductCatalogService`'s backing store (e.g., to Elasticsearch for reads) only requires implementing `ProductReadRepository` — not all 10 methods.
</details>

---

### Problem I-3: The All-In-One Authentication Interface

```java
public interface AuthService {
    String login(String username, String password);      // returns JWT
    void logout(String token);
    boolean validateToken(String token);
    void resetPassword(String email);
    void changePassword(String userId, String old, String newPwd);
    void enable2FA(String userId);
    void disable2FA(String userId);
    String generate2FACode(String userId);
    boolean verify2FACode(String userId, String code);
}

// API Gateway filter — only needs token validation
@Component
public class JwtAuthFilter implements Filter {
    private final AuthService authService;  // drags in all 9 methods for 1 use

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        String token = extractToken(req);
        if (!authService.validateToken(token)) {
            ((HttpServletResponse) res).setStatus(401);
            return;
        }
        chain.doFilter(req, res);
    }
}
```

**Your task:**
1. The gateway filter uses 1 of 9 methods. What's the problem with this dependency?
2. Segregate the interface. What should each split interface be named and what should it contain?

---

<details>
<summary>✅ Fix — I-3</summary>

```java
// Token operations — stateless, used by gateway/filters
public interface TokenService {
    String generate(String userId);
    boolean validate(String token);
    void invalidate(String token);  // logout
}

// Credential management — used by account/profile flows
public interface CredentialService {
    String authenticate(String username, String password);  // returns token
    void resetPassword(String email);
    void changePassword(String userId, String oldPwd, String newPwd);
}

// Two-factor authentication — used by security settings flow
public interface TwoFactorService {
    void enable(String userId);
    void disable(String userId);
    String generateCode(String userId);
    boolean verifyCode(String userId, String code);
}

// Full implementation wires everything together
@Service
public class AuthServiceImpl implements TokenService, CredentialService, TwoFactorService {
    // implement all methods
}

// Gateway filter: tiny, focused dependency
@Component
public class JwtAuthFilter implements Filter {
    private final TokenService tokenService;  // only 3 methods, uses 1

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        String token = extractToken(req);
        if (!tokenService.validate(token)) {
            ((HttpServletResponse) res).setStatus(401);
            return;
        }
        chain.doFilter(req, res);
    }
}

// Account controller: only credential concern
@RestController
public class AccountController {
    private final CredentialService credentialService;
    private final TwoFactorService twoFactorService;
}
```

**Why this matters in prod:** A fat `AuthService` interface makes mocking in tests painful — you mock 9 methods to test 1. Segregated interfaces = focused mocks.
</details>

---

---

## D — Dependency Inversion Principle

### Problem D-1: The Hardwired Notification Service

```java
@Service
public class OrderService {

    // Hardwired to a concrete implementation
    private final SendGridEmailClient emailClient = new SendGridEmailClient(
        "sg-api-key-hardcoded",
        "noreply@myapp.com"
    );

    // Also hardwired
    private final TwilioSmsClient smsClient = new TwilioSmsClient(
        "twilio-account-sid",
        "twilio-auth-token"
    );

    public void placeOrder(Order order) {
        orderRepository.save(order);
        emailClient.send(order.getCustomerEmail(), "Order confirmed: " + order.getId());
        smsClient.send(order.getCustomerPhone(), "Your order is placed!");
    }
}
```

**Your task:**
1. List every reason this code will be painful to maintain.
2. Refactor using DIP. Define the abstraction, implement it, and inject via constructor.

---

<details>
<summary>✅ Fix — D-1</summary>

```java
// Abstractions — both high-level and low-level depend on these
public interface EmailSender {
    void send(String to, String message);
}

public interface SmsSender {
    void send(String phoneNumber, String message);
}

// Low-level: SendGrid implementation
@Component
public class SendGridEmailSender implements EmailSender {
    private final String apiKey;
    private final SendGridClient client;

    public SendGridEmailSender(@Value("${sendgrid.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new SendGridClient(apiKey);
    }

    public void send(String to, String message) {
        client.sendEmail(to, "noreply@myapp.com", message);
    }
}

// Low-level: Twilio implementation
@Component
public class TwilioSmsSender implements SmsSender {
    private final TwilioClient client;

    public TwilioSmsSender(
        @Value("${twilio.account-sid}") String sid,
        @Value("${twilio.auth-token}") String token
    ) {
        this.client = new TwilioClient(sid, token);
    }

    public void send(String phoneNumber, String message) {
        client.sendSms(phoneNumber, message);
    }
}

// High-level: depends on abstractions, not concrete classes
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final EmailSender emailSender;     // abstraction
    private final SmsSender smsSender;         // abstraction

    public OrderService(
        OrderRepository orderRepository,
        EmailSender emailSender,
        SmsSender smsSender
    ) {
        this.orderRepository = orderRepository;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    public void placeOrder(Order order) {
        orderRepository.save(order);
        emailSender.send(order.getCustomerEmail(), "Order confirmed: " + order.getId());
        smsSender.send(order.getCustomerPhone(), "Your order is placed!");
    }
}

// In tests — no real HTTP calls, no API keys needed
class OrderServiceTest {
    @Test void placesOrder() {
        EmailSender mockEmail = mock(EmailSender.class);
        SmsSender mockSms = mock(SmsSender.class);
        OrderService service = new OrderService(mockRepo, mockEmail, mockSms);
        // test without any external system
    }
}
```

**Problems solved:** hardcoded credentials gone, switching providers = swap `@Component` class, unit testable with mocks.
</details>

---

### Problem D-2: The Field-Injected Mess

```java
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseClient warehouseClient;

    @Autowired
    private AuditLogger auditLogger;

    public void reserveStock(String productId, int quantity) {
        Inventory inv = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        if (inv.getAvailable() < quantity) {
            throw new InsufficientStockException();
        }

        inv.reserve(quantity);
        inventoryRepository.save(inv);
        warehouseClient.notifyReservation(productId, quantity);
        auditLogger.log("STOCK_RESERVED", productId, quantity);
    }
}
```

**Your task:**
1. Why is `@Autowired` field injection considered bad practice? Give 3 concrete reasons.
2. Refactor to constructor injection. Show why `final` matters here.

---

<details>
<summary>✅ Fix — D-2</summary>

```java
@Service
public class InventoryService {

    // final = immutable after construction, thread-safe, prevents accidental reassignment
    private final InventoryRepository inventoryRepository;
    private final WarehouseClient warehouseClient;
    private final AuditLogger auditLogger;

    // Constructor injection — dependencies are explicit and required
    public InventoryService(
        InventoryRepository inventoryRepository,
        WarehouseClient warehouseClient,
        AuditLogger auditLogger
    ) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseClient = warehouseClient;
        this.auditLogger = auditLogger;
    }

    public void reserveStock(String productId, int quantity) {
        Inventory inv = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        if (inv.getAvailable() < quantity) {
            throw new InsufficientStockException();
        }

        inv.reserve(quantity);
        inventoryRepository.save(inv);
        warehouseClient.notifyReservation(productId, quantity);
        auditLogger.log("STOCK_RESERVED", productId, quantity);
    }
}

// Test: no Spring context needed
class InventoryServiceTest {
    @Test void reservesStock() {
        var repo    = mock(InventoryRepository.class);
        var wh      = mock(WarehouseClient.class);
        var audit   = mock(AuditLogger.class);
        // 👇 this line is IMPOSSIBLE with field injection without Spring
        var service = new InventoryService(repo, wh, audit);

        when(repo.findByProductId("P1")).thenReturn(Optional.of(new Inventory("P1", 100)));
        service.reserveStock("P1", 10);
        verify(wh).notifyReservation("P1", 10);
    }
}
```

**3 reasons field injection is bad:**
1. **Hidden dependencies** — you can't tell what a class needs without reading its body
2. **Not testable** without Spring context — `new InventoryService()` leaves fields null
3. **Not immutable** — fields can be reassigned or remain unset (partial construction)
</details>

---

### Problem D-3: The Concrete Analytics Dependency

```java
@Service
public class ProductService {

    // High-level business service directly depends on a specific analytics vendor
    private final MixpanelAnalyticsClient mixpanel = new MixpanelAnalyticsClient("mp-token-xyz");

    public void viewProduct(String productId, String userId) {
        Product product = productRepository.findById(productId).orElseThrow();

        // Core business logic
        product.incrementViewCount();
        productRepository.save(product);

        // Vendor-specific analytics call hardwired into business logic
        mixpanel.track("product_viewed", Map.of(
            "product_id", productId,
            "user_id", userId,
            "category", product.getCategory()
        ));
    }
}
```

**Your task:**
1. What happens if the company switches from Mixpanel to Amplitude? How many files change?
2. Apply DIP. Define an abstraction that shields `ProductService` from knowing which analytics vendor is used.

---

<details>
<summary>✅ Fix — D-3</summary>

```java
// Abstraction — ProductService depends only on this
public interface AnalyticsTracker {
    void track(String eventName, Map<String, Object> properties);
}

// Low-level: Mixpanel adapter
@Component
@ConditionalOnProperty(name = "analytics.provider", havingValue = "mixpanel")
public class MixpanelAnalyticsTracker implements AnalyticsTracker {
    private final MixpanelAnalyticsClient client;

    public MixpanelAnalyticsTracker(@Value("${mixpanel.token}") String token) {
        this.client = new MixpanelAnalyticsClient(token);
    }

    public void track(String eventName, Map<String, Object> properties) {
        client.track(eventName, properties);
    }
}

// Adding Amplitude later — zero changes to ProductService
@Component
@ConditionalOnProperty(name = "analytics.provider", havingValue = "amplitude")
public class AmplitudeAnalyticsTracker implements AnalyticsTracker {
    public void track(String eventName, Map<String, Object> properties) {
        amplitudeClient.logEvent(eventName, properties);
    }
}

// No-op for tests
@Component
@Profile("test")
public class NoOpAnalyticsTracker implements AnalyticsTracker {
    public void track(String eventName, Map<String, Object> properties) { /* do nothing */ }
}

// High-level business logic — never changes when vendor changes
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final AnalyticsTracker analyticsTracker;

    public ProductService(ProductRepository productRepository, AnalyticsTracker analyticsTracker) {
        this.productRepository = productRepository;
        this.analyticsTracker = analyticsTracker;
    }

    public void viewProduct(String productId, String userId) {
        Product product = productRepository.findById(productId).orElseThrow();
        product.incrementViewCount();
        productRepository.save(product);

        analyticsTracker.track("product_viewed", Map.of(
            "product_id", productId,
            "user_id", userId,
            "category", product.getCategory()
        ));
    }
}
```

**Switching from Mixpanel to Amplitude:** Change 1 property in `application.yaml`. `ProductService` untouched.
</details>

---

## Quick Cheat Sheet — Smell → Principle → Fix

| Code Smell | Principle Violated | Fix |
|---|---|---|
| Class does fetch + format + email | **SRP** | Split into 3 focused classes |
| `if/else` or `switch` on type that grows | **OCP** | Strategy pattern + map dispatch |
| Override throws `UnsupportedOperationException` | **LSP** | Split interface or redesign hierarchy |
| Subclass returns empty where parent promises non-empty | **LSP** | Make contract explicit in abstraction |
| Interface with 10 methods, class uses 2 | **ISP** | Split interface by role |
| `@Autowired` private field | **DIP** | Constructor injection with `final` |
| `new ConcreteClass()` inside a service | **DIP** | Inject abstraction via constructor |
| Test requires Spring context to instantiate a service | **DIP** | Field injection → constructor injection |
