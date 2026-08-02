# DIP — Problem 3: The Concrete Analytics Dependency

## Your Task

Look at `ProductService.java`. The Mixpanel SDK is `new`-ed directly inside business logic.

1. If the company switches from Mixpanel to Amplitude, how many files change? What's wrong with that?
2. Apply DIP: define an `AnalyticsTracker` abstraction. `ProductService` should never know which vendor is active.

## Hints
- `AnalyticsTracker` interface: `track(String eventName, Map<String, Object> properties)`
- `MixpanelAnalyticsTracker` → `@ConditionalOnProperty(name = "analytics.provider", havingValue = "mixpanel")`
- `AmplitudeAnalyticsTracker` → `@ConditionalOnProperty(..., havingValue = "amplitude")`
- `NoOpAnalyticsTracker` → `@Profile("test")` — used in unit tests, no HTTP calls
- Switching provider = change one line in `application.yaml`, zero Java changes

## Expected Output
Create new files in this directory:
- `AnalyticsTracker.java` (interface)
- `MixpanelAnalyticsTracker.java` (@Component, conditional)
- `AmplitudeAnalyticsTracker.java` (@Component, conditional) ← new vendor, zero changes to service
- `NoOpAnalyticsTracker.java` (@Profile("test"))
- `ProductService.java` (refactored — depends only on `AnalyticsTracker`)
