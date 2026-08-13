package com.lld.phase8.lru_cache;

import java.util.Optional;

public interface Cache<K, V> {
    Optional<V> get(K key);
    void put(K key, V value);
    int size();
    boolean containsKey(K key);
    void clear();
}
