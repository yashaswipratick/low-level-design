package com.lld.phase8.lru_cache.impl;

import com.lld.phase8.lru_cache.Cache;
import com.lld.phase8.lru_cache.EvictionListener;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail =  new Node<>(null, null);
    private final List<EvictionListener<K, V>> listeners = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<EvictionListener<K, V>> getListeners() {
        return listeners;
    }

    @Override
    public Optional<V> get(K key) {
        lock.writeLock().lock();
        try {
            if (cache.containsKey(key)) {
                Node<K, V> node = cache.get(key);
                moveToFront(node);
                return Optional.ofNullable(node.value);
            } else {
                return Optional.empty();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (cache.containsKey(key)) {
                Node<K, V> node = cache.get(key);
                node.value = value;
                cache.put(key, node);
                moveToFront(node);
            } else {
                Node<K, V> node = new Node<>(key, value);
                cache.put(key, node);
                moveToFront(node);
                if (cache.size() > capacity) {
                    evict();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean containsKey(K key) {
        lock.readLock().lock();
        try {
            return cache.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            head.next = tail;
            tail.prev = head;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void moveToFront(Node<K, V> node) {
        removeNode(node);
        addToFront(node);
    }

    private void addToFront(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        if (head.next != null) {
            head.next.prev = node;
        }
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

    private void evict() {
        Node<K, V> lru = tail.prev;       // LRU node is just before dummy tail
        if (lru == head) return;          // list is empty (head ↔ tail only)

        removeNode(lru);                  // unlink from doubly linked list
        cache.remove(lru.key);            // remove from HashMap

        for (EvictionListener<K, V> listener : listeners) {
            listener.onEviction(lru.key, lru.value);  // notify observers
        }
    }
}
