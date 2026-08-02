# ISP — Problem 2: The Bloated Repository Interface

## Your Task

Look at `ProductRepository.java` and `ProductCatalogService.java`.

The repository has 10 methods across 4 different concerns.
`ProductCatalogService` uses only 3 of them but is forced to depend on all 10.

1. Group the 10 methods by role: read / write / analytics / admin.
2. Split into focused interfaces. Wire `ProductCatalogService` to depend **only on what it uses**.

## Hints
- `ProductReadRepository` — safe for all read-only services
- `ProductWriteRepository` — mutation operations only
- `ProductAnalyticsRepository` — reporting/aggregation queries
- `ProductAdminRepository` — privileged bulk operations
- One `JpaProductRepository` can implement all four — split is at the **interface** level

## Expected Output
Create new files in this directory:
- `ProductReadRepository.java`
- `ProductWriteRepository.java`
- `ProductAnalyticsRepository.java`
- `ProductAdminRepository.java`
- `JpaProductRepository.java` (implements all four)
- `ProductCatalogService.java` (refactored — depends on `ProductReadRepository` only)
- `ProductAdminService.java` (depends on admin + write interfaces only)
