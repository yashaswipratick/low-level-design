# LLD Mastery Roadmap — 3 Months to Interview-Ready
> **Profile:** 10 years Java + Spring Boot | SDE2 → Senior → Staff target
> **Timeline:** 12 weeks | 9 phases | 40+ real-world problems
> **Goal:** Walk into any FAANG / Walmart / startup LLD interview and dominate

---

## 🗺️ Recommended Path (Wibey-Advised — Updated Aug 2026)

> This is the **actual sequence to follow** based on current progress.

```
Step 1 → ✅ SOLID — ALL DONE (SRP, OCP, LSP, ISP, DIP all coded)
Step 2 → ⏱️ 15-min mental check: Composition vs Aggregation (Phase 4 — before Decorator/Composite)
Step 3 → 🟢 Phase 6 — Design Patterns in THIS order:
            Behavioral first: Strategy → State → Observer → CoR → Command (then rest)
            Creational next:  Singleton → Builder → Factory → Abstract Factory
            Structural last:  Adapter → Facade → Decorator (then rest)
Step 4 → Phase 5 — UML ALONGSIDE patterns (draw class diagram per pattern, not a separate week)
Step 5 → Phase 3 — Clean Code AFTER patterns (refactor your own pattern code — double learning)
```

**Why Behavioral first? (Your instinct is correct)**
- Strategy, State, Observer, CoR, Command appear in **every single Phase 8 problem**
- Learning Creational first (old textbook order) means learning patterns you won't USE for weeks
- Behavioral first = you can start Phase 8 problems much earlier
- Creational/Structural are simpler — easier to pick up after your brain is warmed up

---

## 📊 Current Progress (as of Aug 2, 2026)

| Phase | Theory | Java Code | Status |
|-------|--------|-----------|--------|
| Phase 1 — OOP | ✅ GUIDE.md | ❌ | ✅ Theory done |
| Phase 2 — SOLID | ✅ GUIDE.md | ✅ SRP, OCP, LSP, ISP, DIP — all done | ✅ Complete |
| Phase 3 — Clean Code | ✅ GUIDE.md | ❌ | Pending (do after patterns) |
| Phase 4 — Object Relationships | ✅ GUIDE.md | ❌ | 15-min mental check before patterns |
| Phase 5 — UML | ✅ GUIDE.md | — | Learn alongside Phase 6 |
| Phase 6 — Design Patterns | ✅ All 3 GUIDEs | ❌ | **🟢 START HERE** |
| Phase 7 — Refactoring | ❌ | ❌ | Not started |
| Phase 8 — LLD Problems | ✅ All PROBLEM.mds | ❌ | Pending |
| Phase 9 — Advanced | ✅ GUIDE.md + all PROBLEM.mds | ❌ | Pending |

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
> ⚠️ **Reordered** from original plan — follow the Recommended Path sequence above.

---

### Week 1 — OOP Deep Dive ✅ DONE (Theory)
> Guide is complete. No code needed at this stage — revisit as needed.

**Day 1–2: OOP Revisited with Production Eyes**
- [x] Encapsulation — why `public` fields are a design crime
- [x] Abstraction — `abstract class` vs `interface` — the real decision tree
- [x] Inheritance — when IS-A becomes a trap (Square/Rectangle problem)
- [x] Polymorphism — runtime dispatch, method hiding vs overriding

**Day 3–4: Object Relationships (covered briefly)**
- [x] Association vs Aggregation vs Composition — with memory ownership
- [x] Dependency — the silent coupling killer
- [x] How Spring Boot's `@Autowired` relates to Dependency Injection

**Day 5–7: Code Smells (22 smells, recognize all)**
- [x] God Class, Feature Envy, Primitive Obsession
- [x] Inappropriate Intimacy, Shotgun Surgery, Divergent Change
- [x] Long Method, Long Parameter List, Data Clumps

---

### Week 2 — SOLID Principles 🔄 IN PROGRESS
> Every interviewer asks SOLID. Every candidate gives textbook answers. You won't.

**S — Single Responsibility Principle** ✅
- [x] Not just "one class, one job" — it's "one reason to change"
- [x] Classic Spring Boot violation: `UserService` doing auth + email + persistence
- [x] Bad code → Refactored code → Why it matters in production

**O — Open/Closed Principle** ✅
- [x] Strategy pattern is OCP in action
- [x] Classic violation: giant `if/else` for payment types
- [x] Bad code → Refactored code

**L — Liskov Substitution Principle** ✅
- [x] The Square-Rectangle trap — deep explanation
- [x] The Bird-Penguin trap — deep explanation
- [x] How to identify LSP violations in interfaces
- [x] Contract-based thinking

**I — Interface Segregation Principle** ✅
- [x] Fat interfaces vs lean interfaces
- [x] `Comparable` vs `Comparator` as a real Java example

**D — Dependency Inversion Principle** 🔄 REMAINING
- [ ] Finish DIP practice problems (problems 1–4 in `phase-2-solid/dip/`)
- [ ] High-level modules must NOT depend on low-level modules
- [ ] How Spring IoC container implements DIP
- [ ] Constructor injection vs field injection — which and WHY
- [ ] Review analytics problem (problem-3) and field injection problem (problem-2)
- **Mock Interview #1:** Full SOLID round (30 min) — do after DIP is complete

---

### Week 2.5 — Phase 4 Quick Sprint: Composition vs Aggregation (1 hour only)
> ⏱️ **Timebox: 60 minutes max.** You only need ONE concept from Phase 4 right now.

**The one thing you must deeply understand before patterns:**
- [ ] **Composition** — child cannot exist without parent (Order → OrderItems). Parent owns the lifecycle.
- [ ] **Aggregation** — child can exist independently (Team → Players). Parent references, doesn't own.
- [ ] Why Composite pattern = Composition. Why Decorator = wraps without owning.
- [ ] Why Spring `@Autowired` beans are Aggregation (Spring owns lifecycle, not your class).
- [ ] Sketch: draw 3 examples of Composition and 3 of Aggregation from systems you've built.

> **Do NOT do the full Phase 4 guide now.** Come back to it in Week 5 with pattern context.

---

### Week 2.5 — Phase 4 Quick Sprint: Composition vs Aggregation (15 min mental check)
> ⏱️ **Timebox: 15 minutes.** DIP is done. You just need to lock down this one concept before Decorator and Composite patterns.

**The one thing you must deeply understand before patterns:**
- [ ] **Composition** — child cannot exist without parent (Order → OrderItems). Parent owns the lifecycle.
- [ ] **Aggregation** — child can exist independently (Team → Players). Parent references, doesn't own.
- [ ] Why `Composite` pattern uses Composition. Why `Decorator` wraps (aggregates) without owning.
- [ ] Why Spring `@Autowired` beans are Aggregation (Spring owns lifecycle, not your class).

> **Done in 15 min? Move on.** Full Phase 4 review happens in Week 5 with pattern context.

---

### Week 3 — Phase 6: Design Patterns — Behavioral (HIGH PRIORITY)
> ⚡ **Your order, your instinct is right.** Behavioral patterns appear in 80% of all LLD interview problems. Learn these first.
> *(Phase 5 UML alongside — draw a class diagram for EVERY pattern)*

**📐 UML Rule:** After each pattern, draw its class diagram. Takes 5 min. Embeds the structure permanently.

**Priority 5 — The Patterns That Drive Every Real-World System**

- [ ] **1. Strategy** — algorithm family, replaces if/else chains, pluggable behaviour
  - 📐 *UML: Context → Strategy (interface) ← ConcreteStrategyA / B / C*
  - 🔗 *Used in: every payment system, pricing engine, sorting, routing*

- [ ] **2. State** — object behaves differently based on internal state, eliminates giant switch
  - 📐 *UML: Context → State (interface) ← ConcreteState(A/B/C) + State Diagram*
  - 🔗 *Used in: Order lifecycle, Vending Machine, Elevator, ATM*

- [ ] **3. Observer** — one change, many listeners; decouples event source from handlers
  - 📐 *UML: Subject → Observer (interface) ← ConcreteObservers + sequence diagram*
  - 🔗 *Used in: Notification system, Stock alerts, Spring events, pub/sub*

- [ ] **4. Chain of Responsibility** — pass request down a handler chain until one handles it
  - 📐 *UML: Handler (abstract) with next: Handler → ConcreteHandlers*
  - 🔗 *Used in: Spring Security filters, middleware, validation pipelines, fraud checks*

- [ ] **5. Command** — encapsulate request as object; enables undo/redo, queuing, logging
  - 📐 *UML: Command (interface) ← ConcreteCommand → Receiver + Invoker*
  - 🔗 *Used in: Order operations, undo/redo editors, task queues, audit logs*

**Remaining Behavioral (cover after the priority 5)**
- [ ] **Template Method** — define algorithm skeleton, let subclasses fill in steps
- [ ] **Iterator** — traverse collection without exposing internals (`Iterable<T>` in Java)
- [ ] **Mediator** — central hub for object communication (chat room, ATC)
- [ ] **Memento** — snapshot + restore (undo systems)
- [ ] **Visitor** — double dispatch, add operations to objects without modifying them
- [ ] **Interpreter** — expression parsing (rare, but asked for Rule Engine problems)

- **Mock Interview #2:** Design a Notification System using Observer + Strategy + CoR (45 min)

---

### Week 4 — Phase 6: Design Patterns — Creational + Structural

**Creational (Day 1–3) — Your Order**

- [ ] **1. Singleton** — 6 implementations, thread safety, enum singleton (interview favorite)
  - 📐 *UML: private constructor + static getInstance()*
  - ⚠️ *Know: eager vs lazy vs double-checked vs enum — and WHY enum is best*

- [ ] **2. Builder** — telescoping constructor problem, fluent API, immutable objects
  - 📐 *UML: Builder inner class + chained setters + build()*
  - 🔗 *Used in: every complex object (Order, Notification, HTTP request)*

- [ ] **3. Factory Method** — delegate object creation to subclasses
  - 📐 *UML: Creator (abstract) ← ConcreteCreator → Product*
  - ⚠️ *Know the difference: Simple Factory ≠ Factory Method ≠ Abstract Factory*

- [ ] **4. Abstract Factory** — create families of related objects without specifying concrete classes
  - 📐 *UML: two product families across two factories*

- [ ] **Prototype** — deep vs shallow copy, `Cloneable` pitfalls (lower priority, know it exists)

**Structural (Day 4–7) — Your Order**

- [ ] **1. Adapter** — make incompatible interfaces work together (legacy system integration)
  - 📐 *UML: Target interface + Adaptee + Adapter (wraps Adaptee)*
  - 🔗 *Used in: integrating third-party SDKs, legacy code, payment providers*

- [ ] **2. Facade** — simplified interface over a complex subsystem
  - 📐 *UML: Facade hiding 3–5 subsystem classes*
  - 🔗 *Your `@Service` layer in Spring Boot IS a Facade*

- [ ] **3. Decorator** — add behaviour at runtime by wrapping (beats inheritance for this)
  - 📐 *UML: Component (interface) ← ConcreteComponent + Decorator (has-a Component) ← ConcreteDecorators*
  - 🔗 *Used in: Java I/O streams, retry wrappers, logging wrappers, auth decorators*

**Remaining Structural (know the concept, not deep-dive required)**
- [ ] **Proxy** — control access to an object (Spring AOP is a proxy)
- [ ] **Composite** — tree structures where leaf and composite are treated the same (File System)
- [ ] **Bridge** — separate abstraction from implementation (two independent class hierarchies)
- [ ] **Flyweight** — share common state across many objects (`String.intern()`, `Integer.valueOf()`)

- **Mock Interview #3:** Design a Logger Framework using Singleton + CoR + Strategy + Decorator (45 min)

---

## MONTH 2 — PATTERN MASTERY + REAL PROBLEMS

### Week 5 — Phase 3 Clean Code + Pattern Combinations
> Clean Code AFTER patterns = you refactor your own pattern code. Double learning.

**Phase 3 — Clean Code (Day 1–2)**
- [ ] Naming: classes, methods, variables — the rules that 10-yr engineers still get wrong
- [ ] Method design: do one thing, one level of abstraction
- [ ] Class design: small, cohesive, tell-don't-ask
- [ ] Error handling: exceptions as domain vocabulary
- [ ] **Exercise:** Take your Week 3 Decorator implementation. Apply all Clean Code rules. Count improvements.

**Pattern Combinations (Day 3–5)**
- [ ] **Observer + Strategy** — Notification System (who to notify + how to notify)
- [ ] **State + Command** — Order Workflow (state decides if command allowed)
- [ ] **Decorator + Chain of Responsibility** — Middleware pipeline
- [ ] **Factory + Strategy** — Payment Processing (factory creates right strategy)

**Phase 4 Full Review (Day 6–7)**
- [ ] Now revisit Phase 4 in full with pattern context
- [ ] Association, Aggregation, Composition, Dependency — examples using patterns you just learned
- [ ] UML notation for all 4 relationship types
- **Homework:** Take a production class you've written. Find 3 improvements using Clean Code + patterns.

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

| # | When | Topic | Duration | Status |
|---|------|-------|----------|--------|
| 1 | After Week 2 (DIP done) | Full SOLID round | 30 min | ⏳ Pending |
| 2 | After Week 3 | Creational + Structural patterns | 30 min | ⏳ Pending |
| 3 | After Week 4 | Behavioral patterns | 45 min | ⏳ Pending |
| 4 | Week 6 | LRU Cache live design | 45 min | ⏳ Pending |
| 5 | Week 7 | Elevator System live design | 45 min | ⏳ Pending |
| 6 | Week 8 | BookMyShow live design | 60 min | ⏳ Pending |
| 7 | Week 9 | Uber live design | 60 min | ⏳ Pending |
| 8 | Week 10 | Low Stock Alert System | 60 min | ⏳ Pending |
| 9 | Week 11 | Advanced topics | 45 min | ⏳ Pending |
| 10 | Week 12 | Full mock (unknown problem) | 90 min | ⏳ Pending |
| 11 | Week 12 | Full mock (Walmart-style) | 90 min | ⏳ Pending |

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
