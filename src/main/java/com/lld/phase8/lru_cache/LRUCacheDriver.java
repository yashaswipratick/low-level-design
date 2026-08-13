package com.lld.phase8.lru_cache;

import com.lld.phase8.lru_cache.impl.LRUCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LRUCacheDriver {

    public static void main(String[] args) throws InterruptedException {

        // ── Eviction listener ──────────────────────────────────────────────
        EvictionListener<Integer, String> evictionLogger =
                (key, value) -> System.out.println("  [EVICTED] key=" + key + " value=" + value);

        // ══════════════════════════════════════════════════════════════════
        // Test 1: Basic get / put
        // ══════════════════════════════════════════════════════════════════
        System.out.println("=== Test 1: Basic get/put ===");
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.getListeners().add(evictionLogger);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        System.out.println("get(1) = " + cache.get(1));   // Optional[one]
        System.out.println("get(2) = " + cache.get(2));   // Optional[two]
        System.out.println("get(9) = " + cache.get(9));   // Optional.empty
        System.out.println("size  = " + cache.size());    // 3

        // ══════════════════════════════════════════════════════════════════
        // Test 2: LRU eviction — access order matters
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n=== Test 2: LRU eviction ===");
        LRUCache<Integer, String> cache2 = new LRUCache<>(3);
        cache2.getListeners().add(evictionLogger);

        cache2.put(1, "one");    // list: [1]
        cache2.put(2, "two");    // list: [2, 1]
        cache2.put(3, "three");  // list: [3, 2, 1]

        cache2.get(1);           // access 1 → list: [1, 3, 2]  (2 is now LRU)
        cache2.get(3);           // access 3 → list: [3, 1, 2]  (2 is still LRU)

        System.out.println("Adding key=4 — should evict key=2 (LRU):");
        cache2.put(4, "four");   // evicts 2 → list: [4, 3, 1]

        System.out.println("get(2) = " + cache2.get(2));  // Optional.empty — evicted
        System.out.println("get(1) = " + cache2.get(1));  // Optional[one]  — still present
        System.out.println("get(3) = " + cache2.get(3));  // Optional[three] — still present
        System.out.println("get(4) = " + cache2.get(4));  // Optional[four]  — just added

        // ══════════════════════════════════════════════════════════════════
        // Test 3: Update existing key — updates value AND moves to front
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n=== Test 3: Update existing key ===");
        LRUCache<Integer, String> cache3 = new LRUCache<>(2);
        cache3.getListeners().add(evictionLogger);

        cache3.put(1, "one");
        cache3.put(2, "two");
        cache3.put(1, "ONE-updated");  // update key=1, should move to front

        System.out.println("Adding key=3 — should evict key=2 (LRU), NOT key=1 (just updated):");
        cache3.put(3, "three");        // evicts 2 (LRU)

        System.out.println("get(1) = " + cache3.get(1));  // Optional[ONE-updated]
        System.out.println("get(2) = " + cache3.get(2));  // Optional.empty — evicted
        System.out.println("get(3) = " + cache3.get(3));  // Optional[three]

        // ══════════════════════════════════════════════════════════════════
        // Test 4: Cache miss returns Optional.empty — no exception
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n=== Test 4: Cache miss ===");
        LRUCache<String, Integer> cache4 = new LRUCache<>(2);
        cache4.put("a", 1);
        System.out.println("get(a)       = " + cache4.get("a"));        // Optional[1]
        System.out.println("get(missing) = " + cache4.get("missing"));  // Optional.empty
        System.out.println("containsKey(a)       = " + cache4.containsKey("a"));       // true
        System.out.println("containsKey(missing) = " + cache4.containsKey("missing")); // false

        // ══════════════════════════════════════════════════════════════════
        // Test 5: clear() resets everything
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n=== Test 5: clear() ===");
        LRUCache<Integer, String> cache5 = new LRUCache<>(3);
        cache5.put(1, "one");
        cache5.put(2, "two");
        System.out.println("size before clear = " + cache5.size());  // 2
        cache5.clear();
        System.out.println("size after clear  = " + cache5.size());  // 0
        System.out.println("get(1) after clear = " + cache5.get(1)); // Optional.empty
        cache5.put(1, "one-after-clear");  // should work fine after clear
        System.out.println("get(1) after re-put = " + cache5.get(1)); // Optional[one-after-clear]

        // ══════════════════════════════════════════════════════════════════
        // Test 6: Thread safety — 10 concurrent threads
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n=== Test 6: Thread safety (10 threads) ===");
        LRUCache<Integer, String> cache6 = new LRUCache<>(5);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            final int id = i;
            executor.submit(() -> {
                cache6.put(id, "value-" + id);
                cache6.get(id);
                cache6.put(id + 1, "updated-" + (id + 1));
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("No exceptions — thread safety OK");
        System.out.println("Final cache size (max 5): " + cache6.size());
    }
}

