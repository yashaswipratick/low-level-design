package com.lld.phase2.solid.lsp.problem1.fix.impl;

import com.lld.phase2.solid.lsp.problem1.fix.UserRepository;
import com.lld.phase2.solid.stubs.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JpaUserRepository implements UserRepository {

    private final Map<String, User> cache = new ConcurrentHashMap<>();

    public JpaUserRepository(Map<String, User> initialData) {
        this.cache.putAll(initialData);
    }


    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<User> findAll() {
        return cache.values().stream().toList();
    }

    @Override
    public List<User> findByEmail(String email) {
        return cache.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .toList();
    }

    @Override
    public void save(User user) {
        cache.put(user.getId(), user);
    }

    @Override
    public void delete(String id) {
        cache.remove(id);
    }
}
