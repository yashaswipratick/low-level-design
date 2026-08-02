package com.lld.phase2.solid.lsp.problem1.without_fix;

import com.lld.phase2.solid.stubs.User;

import java.util.List;
import java.util.Optional;

// TODO: LSP VIOLATION SETUP — this single interface forces ALL implementors
// to support both read AND write operations.
//
// ReadOnlyUserCache cannot honor save() and delete() — it throws instead.
// Any code that substitutes ReadOnlyUserCache for UserRepository breaks at runtime.
//
// Your task: split into ReadableUserRepository + WritableUserRepository.
// ReadOnlyUserCache should implement ONLY the readable side — no throws anywhere.

public interface UserRepository {

    Optional<User> findById(String id);

    List<User> findAll();

    List<User> findByEmail(String email);

    // Write operations — ReadOnlyUserCache CANNOT honor these
    void save(User user);

    void delete(String id);
}
