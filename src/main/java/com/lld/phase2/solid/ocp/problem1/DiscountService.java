package com.lld.phase2.solid.ocp.problem1;

import com.lld.phase2.solid.stubs.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// TODO: OCP VIOLATION — every new customer type requires modifying this method.
//
// PM roadmap:
//   Next sprint → SENIOR_CITIZEN (15%), VETERAN (25%)
//   Q3          → AFFILIATE (12%), FIRST_TIME_BUYER (5%)
//
// Each addition requires touching and re-testing this method → risk of regression
// on existing discount logic (e.g., a typo in STUDENT branch breaks PREMIUM).
//
// Your task: refactor using the Strategy pattern.
//   1. Create DiscountStrategy interface: BigDecimal apply(Order order)
//   2. Each type = one @Component class implementing DiscountStrategy
//   3. DiscountService holds Map<String, DiscountStrategy> — injected by Spring
//   4. calculateDiscount() becomes a single map.get().apply() — never changes again

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
        // Adding SENIOR_CITIZEN? Must modify here. OCP violated.
        // Adding VETERAN?       Must modify here. OCP violated.

        return discount;
    }
}
