package com.lld.phase2.solid.stubs;

/** Stub — represents a legacy static DB connection (used in SRP p3 violation). */
public final class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    public static DatabaseConnection getInstance() { return INSTANCE; }
    public void execute(String sql, Object... params) { /* stub */ }
    private DatabaseConnection() {}
}
