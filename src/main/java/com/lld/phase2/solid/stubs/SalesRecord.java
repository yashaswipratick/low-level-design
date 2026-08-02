package com.lld.phase2.solid.stubs;

/**
 * Stub data record for export problems (OCP p3).
 * Named SalesRecord to avoid shadowing java.lang.Record (abstract base for record types, Java 16+).
 */
public class SalesRecord {
    private final String id;
    private final String name;

    public SalesRecord(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    public String getId()   { return id; }
    public String getName() { return name; }
}
