package com.lld.phase2.solid.lsp.problem1.fix.impl;

import com.lld.phase2.solid.lsp.problem1.fix.ReadableUserRepository;
import com.lld.phase2.solid.stubs.User;

import java.util.Optional;

public class UserProfileService {

    private final ReadableUserRepository readableUserRepository;

    public UserProfileService(ReadableUserRepository readableUserRepository) {
        this.readableUserRepository = readableUserRepository;
    }

    public Optional<User> getUser(String id) {
        return readableUserRepository.findById(id);
    }
}
