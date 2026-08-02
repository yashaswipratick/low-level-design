package com.lld.phase2.solid.lsp.problem1.fix;

import com.lld.phase2.solid.stubs.User;


public interface WritableUserRepository {

    void save(User user);

    void delete(String id);
}
