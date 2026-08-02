package com.lld.phase2.solid.lsp.problem1.fix.impl;

import com.lld.phase2.solid.lsp.problem1.fix.WritableUserRepository;
import com.lld.phase2.solid.stubs.User;

public class UserRegistrationService {

    private final WritableUserRepository writableUserRepository;

    public UserRegistrationService(WritableUserRepository writableUserRepository) {
        this.writableUserRepository = writableUserRepository;
    }

    public void register(User user) {
        writableUserRepository.save(user);
    }
}
