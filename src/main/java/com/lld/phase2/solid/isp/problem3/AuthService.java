package com.lld.phase2.solid.isp.problem3;

// TODO: ISP VIOLATION — 9 methods across 3 distinct concerns in one interface.
//
// Concern 1 — Token operations (used by JwtAuthFilter, API gateway):
//   login, logout, validateToken
//
// Concern 2 — Credential management (used by AccountController):
//   resetPassword, changePassword
//
// Concern 3 — Two-factor auth (used by SecuritySettingsController):
//   enable2FA, disable2FA, generate2FACode, verify2FACode
//
// JwtAuthFilter uses ONLY validateToken() but is forced to depend on all 9 methods.
// In tests, mocking AuthService for JwtAuthFilter requires stubbing 8 irrelevant methods.
// Any change to the 2FA methods forces recompilation of JwtAuthFilter — unnecessary coupling.
//
// Your task: split into TokenService, CredentialService, TwoFactorService.
// JwtAuthFilter should depend on TokenService only (~3 methods, uses 1).

public interface AuthService {

    // Token operations
    String  login(String username, String password);  // returns JWT
    void    logout(String token);
    boolean validateToken(String token);

    // Credential management
    void resetPassword(String email);
    void changePassword(String userId, String oldPassword, String newPassword);

    // Two-factor authentication
    void    enable2FA(String userId);
    void    disable2FA(String userId);
    String  generate2FACode(String userId);
    boolean verify2FACode(String userId, String code);
}
