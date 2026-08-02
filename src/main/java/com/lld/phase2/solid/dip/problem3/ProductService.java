package com.lld.phase2.solid.dip.problem3;

import com.lld.phase2.solid.stubs.MixpanelAnalyticsClient;
import com.lld.phase2.solid.stubs.Product;
import com.lld.phase2.solid.stubs.ProductNotFoundException;
import com.lld.phase2.solid.stubs.ProductRepository;
import com.lld.phase2.stubs.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

// TODO: DIP VIOLATION — high-level business service directly depends on a specific
// third-party analytics vendor (Mixpanel).
//
// Problems:
//   1. Switching to Amplitude requires modifying this class (business logic shouldn't change)
//   2. `new MixpanelAnalyticsClient(...)` bypasses Spring IoC
//   3. The API token is hardcoded — security risk
//   4. Unit tests make real HTTP calls to Mixpanel — slow, flaky, requires internet
//
// If the company switches from Mixpanel to Amplitude:
//   → This class changes (business logic modified for an infrastructure reason — wrong!)
//   → Every other class that also newed up MixpanelAnalyticsClient changes
//
// Your task:
//   1. Define AnalyticsTracker interface: track(String eventName, Map<String, Object> props)
//   2. MixpanelAnalyticsTracker → @Component (use @ConditionalOnProperty in real code)
//   3. AmplitudeAnalyticsTracker → @Component (new vendor = new class, zero changes here)
//   4. NoOpAnalyticsTracker → use in tests (@Profile("test"))
//   5. ProductService constructor accepts AnalyticsTracker — never knows the vendor

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // VIOLATION: hardwired to a concrete vendor SDK with token in source
    private final MixpanelAnalyticsClient mixpanel = new MixpanelAnalyticsClient(
        "mp-token-hardcoded-123"
    );

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProduct(String productId) {
        return productRepository.findById(productId);
    }

    public void viewProduct(String productId, String userId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        // Core business logic
        product.incrementViewCount();
        productRepository.save(product);

        // VIOLATION: business logic is coupled to a specific analytics vendor
        // Switching to Amplitude = modify this class
        mixpanel.track("product_viewed", Map.of(
            "product_id", productId,
            "user_id",    userId,
            "category",   product.getCategory(),
            "view_count", product.getViewCount()
        ));
    }

    public void purchaseProduct(String productId, String userId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        product.decrementStock(quantity);
        productRepository.save(product);

        // VIOLATION: again coupled to Mixpanel — same problem, second occurrence
        mixpanel.track("product_purchased", Map.of(
            "product_id", productId,
            "user_id",    userId,
            "quantity",   quantity
        ));
    }
}
