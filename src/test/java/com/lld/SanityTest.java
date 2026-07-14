package com.lld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sanity test — confirms the test framework (JUnit 5 + AssertJ) is wired up correctly.
 * Delete this once you start writing real phase tests.
 */
@DisplayName("Environment Sanity Check")
class SanityTest {

    @Test
    @DisplayName("Maven + JUnit 5 + AssertJ setup is working")
    void setupIsWorking() {
        String message = "LLD course is ready";
        assertThat(message).isNotNull().contains("LLD");
    }
}
