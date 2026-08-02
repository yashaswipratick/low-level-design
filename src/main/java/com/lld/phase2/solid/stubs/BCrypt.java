package com.lld.phase2.solid.stubs;

/** Stub for org.mindrot.jbcrypt.BCrypt — avoids adding jbcrypt dependency. */
public final class BCrypt {
    public static String hashpw(String password, String salt) { return "$2a$10$stub." + password; }
    public static boolean checkpw(String plaintext, String hashed) { return true; }
    public static String gensalt() { return "$2a$10$stubsaltstubsaltstubs."; }
    private BCrypt() {}
}
