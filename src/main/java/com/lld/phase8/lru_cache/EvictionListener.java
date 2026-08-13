package com.lld.phase8.lru_cache;

public interface EvictionListener<K, V> {
    void onEviction(K key, V value);
}
