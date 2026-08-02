# OCP — Problem 3: The Hardcoded Export Format

## Your Task

Look at `DataExporter.java`. Adding XLSX, Parquet, or PDF requires modifying the existing method each time.

1. Why is modifying an existing `if/else` in production code dangerous vs. adding a new class?
2. Refactor so `DataExporter.export()` is **permanently closed for modification**.

## Hints
- Define `RecordExporter` interface with `format()` and `export(List<Record>)` methods
- Each format = one `@Component` implementing `RecordExporter`
- `DataExporter` collects all exporters at startup and dispatches by format key
- Throw `UnsupportedFormatException` if format not found

## Expected Output
Create new files in this directory:
- `RecordExporter.java` (interface)
- `CsvExporter.java`
- `JsonExporter.java`
- `XlsxExporter.java` ← new format, zero changes to DataExporter
- `DataExporter.java` (refactored)
