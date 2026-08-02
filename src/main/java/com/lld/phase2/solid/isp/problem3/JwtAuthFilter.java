package com.lld.phase2.solid.isp.problem3;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// TODO: ISP VIOLATION — JwtAuthFilter depends on the full AuthService (9 methods)
// but uses EXACTLY ONE: validateToken().
//
// Problems:
//   1. Tests must mock 8 irrelevant methods to test this filter
//   2. AuthService changes for 2FA reasons force recompilation of this filter
//   3. The dependency is deceptive — this filter has no business knowing about
//      password reset, 2FA setup, or credential changes
//
// Your task: change the field type from AuthService to a narrow TokenService interface.
// TokenService should contain: generate(userId), validate(token), invalidate(token).

public class JwtAuthFilter implements Filter {

    // VIOLATION: depends on full AuthService (9 methods) for only 1 use
    private final AuthService authService;

    public JwtAuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpReq = (HttpServletRequest)  request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String authHeader = httpReq.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        // Uses only 1 of 9 methods on the fat interface
        if (!authService.validateToken(token)) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}
