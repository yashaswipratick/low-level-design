package com.lld.phase2.solid.lsp.problem1.fix;

import com.lld.phase2.solid.stubs.User;

import java.util.List;
import java.util.Optional;

public interface ReadableUserRepository {

    Optional<User> findById(String id);

    List<User> findAll();

    List<User> findByEmail(String email);
}
