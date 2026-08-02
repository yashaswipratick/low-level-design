package com.lld.phase2.solid.lsp.problem1.without_fix;

import com.lld.phase2.solid.stubs.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO: LSP VIOLATION — implements UserRepository but CANNOT honor the full contract.
//
// save() and delete() throw UnsupportedOperationException.
// Any caller that substitutes this for UserRepository fails at runtime:
//
//   void updateUser(UserRepository repo, User user) {
//       repo.save(user);  // throws if repo is ReadOnlyUserCache — LSP broken
//   }
//
// LSP Test: Can you swap ReadOnlyUserCache everywhere a UserRepository is expected
// and have the program behave correctly? NO → LSP violated.
//
// Your task: redesign so this cache is a VALID substitution for a read-only interface.

public class ReadOnlyUserCache implements UserRepository {

    private final Map<String, User> cache = new ConcurrentHashMap<>();

    public ReadOnlyUserCache(Map<String, User> initialData) {
        this.cache.putAll(initialData);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<User> findByEmail(String email) {
        return cache.values().stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .toList();
    }

    @Override
    public void save(User user) {
        // VIOLATION: cache is read-only — cannot honor this contract
        throw new UnsupportedOperationException("ReadOnlyUserCache does not support save()");
    }

    @Override
    public void delete(String id) {
        // VIOLATION: cache is read-only — cannot honor this contract
        throw new UnsupportedOperationException("ReadOnlyUserCache does not support delete()");
    }
}
