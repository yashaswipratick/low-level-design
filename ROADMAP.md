# LLD Mastery Roadmap — 3 Months to Interview-Ready
> **Profile:** 10 years Java + Spring Boot | SDE2 → Senior → Staff target
> **Timeline:** 12 weeks | 9 phases | 40+ real-world problems
> **Goal:** Walk into any FAANG / Walmart / startup LLD interview and dominate

---

## How This Works

| Rule | Detail |
|------|--------|
| Experience level | 10 years — we skip hello-world. We go deep, fast. |
| Teach style | Intuition → Analogy → Problem → Theory → Code → UML → Trade-offs |
| Language | Java 21 (records, sealed classes, pattern matching) |
| Move forward | Only after you answer questions correctly |
| Wrong answer | Hints first. No freebies. |
| Practice | 5 Easy · 5 Medium · 5 Hard after every topic |
| Mock Interview | After every major topic. Increasing difficulty. |
| Spaced Repetition | Weekly quizzes on past topics |

---

## The 3-Month Battle Plan

```
Month 1 → Foundations Sprint    (Weeks 1–4)
Month 2 → Pattern Mastery       (Weeks 5–8)
Month 3 → Real Problems + Mocks (Weeks 9–12)
```

---

## MONTH 1 — FOUNDATIONS SPRINT

### Week 1 — OOP Deep Dive (Accelerated for 10yr Engineers)
> You know syntax. Now we fix the THINKING.

**Day 1–2: OOP Revisited with Production Eyes**
- [ ] Encapsulation — why `public` fields are a design crime
- [ ] Abstraction — `abstract class` vs `interface` — the real decision tree
- [ ] Inheritance — when IS-A becomes a trap (Square/Rectangle problem)
- [ ] Polymorphism — runtime dispatch, method hiding vs overriding

**Day 3–4: Object Relationships (the ones interviews ask about)**
- [ ] Association vs Aggregation vs Composition — with memory ownership
- [ ] Dependency — the silent coupling killer
- [ ] How Spring Boot's `@Autowired` relates to Dependency Injection

**Day 5–7: Code Smells (22 smells, recognize all)**
- [ ] God Class, Feature Envy, Primitive Obsession
- [ ] Inappropriate Intimacy, Shotgun Surgery, Divergent Change
- [ ] Long Method, Long Parameter List, Data Clumps
- **Homework:** Find 5 smells in a real Spring Boot codebase you've worked on

---

### Week 2 — SOLID Principles (The Backbone of Every Interview)
> Every interviewer asks SOLID. Every candidate gives textbook answers. You won't.

**Day 1: S — Single Responsibility Principle**
- [ ] Not just "one class, one job" — it's "one reason to change"
- [ ] Classic Spring Boot violation: `UserService` doing auth + email + persistence
- [ ] Bad code → Refactored code → Why it matters in production

**Day 2: O — Open/Closed Principle**
- [ ] Strategy pattern is OCP in action
- [ ] Classic violation: giant `if/else` for payment types
- [ ] Bad code → Refactored code

**Day 3: L — Liskov Substitution Principle (trickiest)**
- [ ] The Square-Rectangle trap — deep explanation
- [ ] The Bird-Penguin trap — deep explanation
- [ ] How to identify LSP violations in interfaces
- [ ] Contract-based thinking

**Day 4: I — Interface Segregation Principle**
- [ ] Fat interfaces vs lean interfaces
- [ ] `Comparable` vs `Comparator` as a real Java example

**Day 5–7: D — Dependency Inversion Principle**
- [ ] High-level modules must NOT depend on low-level modules
- [ ] How Spring IoC container implements DIP
- [ ] Constructor injection vs field injection — which and WHY
- **Mock Interview #1:** Full SOLID round (30 min)

---

### Week 3 — Design Patterns: Creational + Structural
> Don't just know the pattern. Know WHEN to use it and WHEN NOT to.

**Creational Patterns (Day 1–3)**
- [ ] **Singleton** — 6 implementations, thread safety, enum singleton (interview favorite)
- [ ] **Factory Method** — vs Simple Factory (many confuse these)
- [ ] **Abstract Factory** — UI toolkit example
- [ ] **Builder** — telescoping constructor problem, fluent API
- [ ] **Prototype** — deep vs shallow copy, `Cloneable` pitfalls in Java

**Structural Patterns (Day 4–7)**
- [ ] **Adapter** — legacy system integration (classic interview scenario)
- [ ] **Facade** — your `@Service` layer IS a facade (connection to Spring)
- [ ] **Decorator** — Java I/O streams, how it beats inheritance
- [ ] **Proxy** — Virtual, Protection, Logging proxy | Spring AOP is a proxy
- [ ] **Composite** — file system, UI component trees
- [ ] **Bridge** — abstraction vs implementation axis
- [ ] **Flyweight** — `String.intern()`, `Integer.valueOf()` — Java already uses this
- **Mock Interview #2:** Pick a pattern, design on the fly (30 min)

---

### Week 4 — Design Patterns: Behavioral + UML
> Behavioral patterns are what interviewers love to ask for real problems.

**Behavioral Patterns (Day 1–5)**
- [ ] **Strategy** — algorithm family, replaces switch statements
- [ ] **Observer** — event systems, Spring events, pub/sub
- [ ] **Command** — undo/redo, task queues, request encapsulation
- [ ] **State** — Order state machine, elevator states, vending machine
- [ ] **Template Method** — Hollywood Principle, abstract base classes
- [ ] **Chain of Responsibility** — filter chains, middleware, Spring Security
- [ ] **Iterator** — custom iterators, `Iterable<T>` in Java
- [ ] **Mediator** — chat room, air traffic control
- [ ] **Memento** — snapshot/restore, undo systems
- [ ] **Visitor** — double dispatch, AST traversal
- [ ] **Interpreter** — expression parsing (rare but asked)

**UML (Day 6–7)**
- [ ] Class Diagram — read and draw in 5 minutes
- [ ] Sequence Diagram — method call flows
- [ ] State Diagram — object lifecycle
- [ ] How to draw UML on a whiteboard in an interview
- **Mock Interview #3:** Design Patterns round (45 min)

---

## MONTH 2 — PATTERN MASTERY + REAL PROBLEMS

### Week 5 — Pattern Combinations + Clean Code
> Real systems use patterns together. Interviews test if you know this.

- [ ] **Pattern combos:** Observer + Strategy (Notification System)
- [ ] **Pattern combos:** State + Command (Order Workflow)
- [ ] **Pattern combos:** Decorator + Chain of Responsibility (Middleware)
- [ ] **Pattern combos:** Factory + Strategy (Payment Processing)
- [ ] Clean Code naming, methods, classes (the stuff 10-yr engineers still get wrong)
- [ ] Refactoring legacy code — identify smell → choose pattern → apply
- **Homework:** Take a production class you've written. Find 3 improvements.

---

### Week 6 — Real LLD Problems Batch 1 (Infrastructure & Utilities)

| Problem | Key Patterns | Difficulty |
|---------|-------------|------------|
| **LRU Cache** | Strategy, Decorator | Medium |
| **Rate Limiter** (Token Bucket + Leaky Bucket) | Strategy, Decorator | Hard |
| **Logger Framework** (like Log4j) | Singleton, Chain of Responsibility, Strategy | Medium |
| **Vending Machine** | State, Strategy | Medium |
| **Coffee Machine** | State, Builder | Easy |
| **Parking Lot** | Strategy, Factory, Observer | Hard |

- **Mock Interview #4:** LRU Cache live design (45 min)

---

### Week 7 — Real LLD Problems Batch 2 (Games & Simulations)

| Problem | Key Patterns | Difficulty |
|---------|-------------|------------|
| **Chess** | Factory, Command, State | Hard |
| **Snake and Ladder** | Observer, Strategy | Medium |
| **Elevator System** | State, Strategy, Observer | Hard |
| **Traffic Signal** | State, Observer | Medium |
| **ATM Machine** | State, Command, Strategy | Hard |

- **Mock Interview #5:** Elevator System live design (45 min)

---

### Week 8 — Real LLD Problems Batch 3 (Booking & Commerce)

| Problem | Key Patterns | Difficulty |
|---------|-------------|------------|
| **BookMyShow** | Factory, Strategy, Observer | Hard |
| **Hotel Booking** | Strategy, Observer, State | Hard |
| **Library Management** | Strategy, Observer | Medium |
| **Splitwise** (Expense Sharing) | Strategy, Observer | Hard |
| **Payment Gateway** | Strategy, Chain of Responsibility, Decorator | Hard |

- **Mock Interview #6:** BookMyShow live design (60 min)

---

## MONTH 3 — REAL PROBLEMS + ADVANCED + MOCK INTERVIEWS

### Week 9 — Real LLD Problems Batch 4 (Platform & Marketplace)

| Problem | Key Patterns | Difficulty |
|---------|-------------|------------|
| **Uber / Ride Sharing** | Strategy, Observer, State, Factory | Hard |
| **Swiggy / Food Delivery** | Observer, Strategy, State | Hard |
| **Notification System** | Observer, Strategy, Chain of Responsibility, Decorator | Hard |
| **Amazon / Flipkart E-commerce** | Factory, Strategy, Observer, State | Hard |
| **Inventory Management** | Observer, Strategy | Medium |

- **Mock Interview #7:** Uber live design (60 min)

---

### Week 10 — Walmart-Specific Real World Problems
> Problems modeled exactly like real Walmart engineering interviews

| Problem | Key Patterns | Difficulty |
|---------|-------------|------------|
| **Low Stock Alert System** | Observer, Strategy, Chain of Responsibility, Decorator | Hard |
| **Flash Sale / Deal Engine** | Strategy, Observer, State, Decorator | Hard |
| **Cart Service with Pricing Rules** | Strategy, Decorator, Chain of Responsibility | Hard |
| **Order State Machine** | State, Command, Observer | Hard |
| **Coupon / Promo Code Engine** | Strategy, Chain of Responsibility | Hard |
| **Price Drop Alert System** | Observer, Strategy, Decorator | Hard |
| **Fulfillment Center Slot Allocation** | Strategy, Factory | Hard |
| **Return & Refund Workflow** | State, Command, Chain of Responsibility | Hard |
| **Search Autocomplete** | Trie + Design Patterns | Hard |
| **Product Review & Rating System** | Observer, Strategy | Medium |

- **Mock Interview #8:** Low Stock Alert System live design (60 min)

---

### Week 11 — Advanced Topics (Staff Engineer Territory)

- [ ] **Thread Safety in OOP** — synchronized, volatile, atomic, immutable objects
- [ ] **Concurrency patterns** — Producer-Consumer, Read-Write Lock
- [ ] **Domain Driven Design** — Entity, Value Object, Aggregate, Repository
- [ ] **CQRS** — Command Query Responsibility Segregation
- [ ] **Event Driven Design** — Event sourcing, eventual consistency
- [ ] **Exception Design** — checked vs unchecked, custom exception hierarchy
- [ ] **API Design** — interface design principles, backward compatibility
- [ ] **Testability** — how good LLD = easily testable code
- [ ] **Scalability from LLD perspective** — where does OOP end and system design begin?
- **Mock Interview #9:** Advanced topics round (45 min)

---

### Week 12 — Final Sprint: Mock Interviews + Gap Analysis

- [ ] Full 90-min mock interview (pick any unseen problem)
- [ ] Full 90-min mock interview (Walmart-style problem)
- [ ] Review all weak areas identified in previous mocks
- [ ] Spaced repetition quiz — all 9 phases
- [ ] **Final Score Card** — where you started vs where you are now
- [ ] Top 10 interview mistakes to avoid

---

## Mock Interview Schedule

| # | Week | Topic | Duration |
|---|------|-------|----------|
| 1 | 2 | SOLID deep-dive | 30 min |
| 2 | 3 | Creational + Structural patterns | 30 min |
| 3 | 4 | Behavioral patterns | 45 min |
| 4 | 6 | LRU Cache live design | 45 min |
| 5 | 7 | Elevator System live design | 45 min |
| 6 | 8 | BookMyShow live design | 60 min |
| 7 | 9 | Uber live design | 60 min |
| 8 | 10 | Low Stock Alert System | 60 min |
| 9 | 11 | Advanced topics | 45 min |
| 10 | 12 | Full mock (unknown problem) | 90 min |
| 11 | 12 | Full mock (Walmart-style) | 90 min |

---

## Scoring Rubric (Every Mock Interview)

| Dimension | Weight | What we look for |
|-----------|--------|-----------------|
| Requirement Gathering | 10% | Right clarifying questions before designing |
| Design Thinking | 15% | Identify entities, relationships, responsibilities |
| OOP Concepts | 10% | Encapsulation, abstraction, polymorphism in code |
| SOLID Principles | 10% | No violations, justify every decision |
| Design Patterns | 15% | Correct pattern, correct reason, correct trade-off |
| Extensibility | 10% | Adding features shouldn't break existing code |
| Scalability | 10% | Awareness of where design breaks at scale |
| Naming & Clean Code | 5% | Reads like prose, self-documenting |
| Code Quality | 10% | Production-grade, handles edge cases |
| Communication | 5% | Think out loud, structured explanation |
| **Hire / No Hire** | **Binary** | Would a Staff Engineer approve this? |

---

## Real-World Problem Template (use for every problem)

```
1. Understand the problem (5 min)
   - What are the core entities?
   - What are the relationships?
   - What changes frequently? (Encapsulate that.)

2. Clarifying questions (5 min)
   - Scale: how many users/products/events?
   - Extensibility: what new features might come?
   - Constraints: sync/async? single/multi-threaded?

3. Identify design patterns (5 min)
   - What varies? → Strategy
   - Who needs to know? → Observer
   - Complex object creation? → Builder/Factory
   - Add behavior at runtime? → Decorator

4. Draw class diagram (10 min)
5. Write core interfaces first (10 min)
6. Implement key classes (15 min)
7. Discuss trade-offs (5 min)
```

---

## Directory Structure

```
low-level-design/
├── ROADMAP.md                          ← You are here
├── PROBLEMS.md                         ← Full problem bank (40+)
├── pom.xml                             ← Maven build
├── phase-1-oop/
├── phase-2-solid/
├── phase-3-clean-code/
├── phase-4-object-relationships/
├── phase-5-uml/
├── phase-6-design-patterns/
│   ├── creational/
│   ├── structural/
│   └── behavioral/
├── phase-7-refactoring/
├── phase-8-lld-problems/
│   ├── walmart/                        ← Walmart-specific problems
│   ├── classics/                       ← Parking lot, BookMyShow, etc.
│   └── advanced-systems/              ← Rate limiter, logger, etc.
├── phase-9-advanced/
└── src/
    ├── main/java/com/lld/
    └── test/java/com/lld/
```

---

## The 10-Year Engineer's Honest Self-Assessment

> "I know Java. But can I DESIGN in Java?"

After 10 years, most engineers can code anything. The gap is **design vocabulary** — knowing *why* you make each decision, naming the pattern you're applying, and defending trade-offs under pressure.

That's exactly what we're fixing in 12 weeks.

---

*"The measure of a Senior Engineer is not how fast they write code. It's how clearly they think before they write any."*
