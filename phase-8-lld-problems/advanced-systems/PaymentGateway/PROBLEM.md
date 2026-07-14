# Problem: Payment Gateway
> Domain: FinTech / Platform | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design a Payment Gateway that processes payments across multiple providers.

Requirements:
1. Support multiple payment providers: Stripe, PayPal, Braintree, internal wallet
2. Select the optimal provider based on currency, region, and success rate
3. Fraud detection must run before any payment is processed
4. Idempotency: retrying the same payment request must not double-charge
5. Retry failed payments with next best provider (failover)
6. Full audit trail: every attempt, every provider response, every state change
7. Payments go through states: Initiated → Processing → Authorized → Captured → Refunded / Failed

---

## Clarifying Questions to Ask

- Is payment synchronous (wait for result) or async (webhook callback)?
- Can a payment span multiple providers (split payment)?
- Is the fraud detection third-party or in-house rule engine?
- What is the idempotency key — user-provided or system-generated?
- Are partial refunds supported?
- What currencies and regions are in scope?
- Is 3D Secure (bank OTP) in scope?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — each payment provider is a strategy (`StripeProvider`, `PayPalProvider`)
- **Chain of Responsibility** — fraud check → balance check → provider routing → attempt payment
- **Decorator** — add idempotency, retry, and audit logging as decorators on any provider
- **State** — payment lifecycle state machine with valid transitions
- **Command** — each payment attempt is a command (retryable, reversible via refund)

</details>

---

## Class Design Starting Point

```
PaymentGateway
  └── PaymentResult process(PaymentRequest request)

PaymentRequest
  ├── String idempotencyKey
  ├── Money amount
  ├── PaymentMethod method (card, wallet, UPI)
  └── String customerId

PaymentProvider (interface)
  └── PaymentResult charge(PaymentRequest request)

ProviderSelector (Strategy)
  └── PaymentProvider select(PaymentRequest request, List<PaymentProvider> available)

FraudDetector (Chain node)
  └── void check(PaymentRequest request) throws FraudException

PaymentState (enum)
  INITIATED, PROCESSING, AUTHORIZED, CAPTURED, FAILED, REFUNDED

PaymentAuditLog
  └── void record(String idempotencyKey, String provider, PaymentState state, Instant ts)
```

---

## Your Task

1. Implement `PaymentGateway` with provider fallback chain
2. `StripeProvider` and `WalletProvider` as concrete strategies
3. `FraudCheckFilter` and `InsufficientBalanceFilter` as chain nodes
4. `IdempotencyDecorator` — prevents double charges on retry
5. `RetryDecorator` — tries next provider on failure
6. Payment state machine: enforce valid transitions
7. Implement in `src/main/java/com/lld/phase8/problems/advanced/payment/`

---

## Edge Cases

- Network timeout from provider — payment unknown state (not definitely failed)
- Same idempotency key, different amount — reject or process?
- Fraud check throws exception — fail open or fail closed?
- Provider returns success but network drops before client receives response
- Partial capture: authorized ₹1000, capture only ₹750 (user removed item)
- Currency conversion failure mid-payment

---

## Extension Points

- New provider (Razorpay) → implement `PaymentProvider`
- New fraud rule → add node to Chain of Responsibility
- Dynamic provider selection (ML model) → replace `ProviderSelector` strategy
- Async payment with webhook → add async state handler
