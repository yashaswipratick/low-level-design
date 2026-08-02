package com.lld.phase2.solid.stubs;

/** Stub domain User — used in LSP and DIP problems (not the violation class in SRP p3). */
public class User {
    private final String id;
    private final String email;
    private final String name;

    public User(String id, String email, String name) {
        this.id    = id;
        this.email = email;
        this.name  = name;
    }

    public String getId()    { return id; }
    public String getEmail() { return email; }
    public String getName()  { return name; }
}
