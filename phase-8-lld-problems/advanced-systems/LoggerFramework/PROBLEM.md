# Problem: Logger Framework (like Log4j / SLF4J)
> Domain: Infrastructure | Difficulty: Medium | Est. Time: 45 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design a Logger Framework similar to Log4j/SLF4J.

Requirements:
1. Support log levels: TRACE, DEBUG, INFO, WARN, ERROR, FATAL (in order of severity)
2. Only messages at or above the configured level get logged
3. Multiple appenders — console, file, database, remote — can be active simultaneously
4. Messages can be formatted differently per appender (plain text, JSON, XML)
5. Logger instances are per-class and share a global configuration
6. Thread-safe logging under high concurrency
7. Appenders can be chained — a message goes to all appenders that accept it

---

## Clarifying Questions to Ask

- Is the log level configurable at runtime (hot reload) or startup only?
- Is a logger per-class, per-module, or global?
- Does each appender have its own level threshold independently?
- Is async (non-blocking) logging required?
- Should rolling file appenders be in scope (size or date-based rotation)?
- Is structured logging (key-value pairs, JSON) needed?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Singleton** — `LogManager` is a singleton; individual `Logger` instances cached by class name
- **Chain of Responsibility** — each appender decides whether to accept the message and can pass it along
- **Strategy** — formatting is a strategy (`PlainTextFormatter`, `JsonFormatter`, `XmlFormatter`)
- **Decorator** — add async buffering, filtering, or rate limiting on top of any appender

</details>

---

## Class Design Starting Point

```
LogManager (Singleton)
  └── getLogger(Class<?>) → Logger

Logger
  ├── log(Level, String message)
  ├── info(String), warn(String), error(String, Throwable)
  └── List<Appender> appenders  (inherited from config)

Appender (interface)
  ├── append(LogEvent event)
  └── Formatter formatter

LogEvent (record)
  ├── Level level
  ├── String loggerName
  ├── String message
  ├── Throwable throwable (nullable)
  └── Instant timestamp

Formatter (interface)
  └── String format(LogEvent event)
```

---

## Your Task

1. Implement `Logger`, `LogManager`, `Appender`, `Formatter` hierarchy
2. Implement `ConsoleAppender` and `FileAppender`
3. Implement `PlainTextFormatter` and `JsonFormatter`
4. Show Chain of Responsibility: a log event flows through multiple appenders
5. Demonstrate Decorator: wrap any appender with `AsyncAppender` that queues events to a background thread
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/logger/`

---

## Edge Cases

- What if the file appender's disk is full?
- What if logger level is set to OFF — should any appenders still run?
- Recursive logging: a log statement inside an appender causes infinite loop
- Log message with `null` throwable vs intentional `null` message
- Thread A and Thread B log simultaneously to same `FileAppender` — corruption?

---

## Extension Points

- New appender (Kafka, Elasticsearch) → implement `Appender`
- New format (Logfmt) → implement `Formatter`
- New filter (suppress repetitive messages) → `DecoratorAppender`
- Async logging → `AsyncAppender` decorates any `Appender`
