package com.lld.phase2.solid.srp.problem3;

import com.lld.phase2.solid.stubs.BCrypt;
import com.lld.phase2.solid.stubs.DatabaseConnection;

// TODO: SRP VIOLATION — domain object is doing 4 things:
//   1. Modeling user state        (OK — this belongs here)
//   2. Saving itself to DB        (should be in UserRepository)
//   3. Serializing itself to JSON (should be in UserDtoMapper)
//   4. Validating its own email   (should be in UserValidator)
//
// Your task: strip User down to pure domain model + domain behavior only.
// Create: UserRepository, UserDtoMapper, UserValidator as separate classes.

public class User {

    private final String id;
    private final String name;
    private final String email;
    private final String passwordHash;

    public User(String id, String name, String email, String passwordHash) {
        this.id           = id;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
    }

    // Domain behavior — fine to keep here
    public boolean checkPassword(String rawPassword) {
        return BCrypt.checkpw(rawPassword, this.passwordHash);
    }

    // VIOLATION: data access logic in domain object — couples domain to infrastructure
    public void save() {
        DatabaseConnection.getInstance().execute(
            "INSERT INTO users (id, name, email, password_hash) VALUES (?, ?, ?, ?)",
            id, name, email, passwordHash
        );
    }

    // VIOLATION: serialization logic in domain object — couples domain to API contract
    public String toJson() {
        return String.format("{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\"}", id, name, email);
    }

    // VIOLATION: validation logic in domain object — couples domain to validation rules
    public boolean isValidEmail() {
        return email != null && email.contains("@") && email.contains(".");
    }

    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
}
