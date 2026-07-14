package com.lld;

/**
 * Entry point for the LLD Mastery Course.
 *
 * Run this to verify your Maven setup is working.
 * As you progress through phases, each phase will have its own runner.
 *
 * How to run:
 *   mvn compile exec:java
 *
 * How to run a specific phase class:
 *   mvn exec:java -Dexec.mainClass="com.lld.phase1.oop.EncapsulationDemo"
 *
 * How to run all tests:
 *   mvn test
 *
 * How to run tests for one phase only:
 *   mvn test -Dtest="com.lld.phase1.*"
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     LLD Mastery Course — Environment Ready   ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Java Version : " + System.getProperty("java.version") + "                        ║");
        System.out.println("║  Author       : Your Name Here               ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Phases:                                      ║");
        System.out.println("║   Phase 1 → OOP Fundamentals                 ║");
        System.out.println("║   Phase 2 → SOLID Principles                 ║");
        System.out.println("║   Phase 3 → Clean Code                       ║");
        System.out.println("║   Phase 4 → Object Relationships             ║");
        System.out.println("║   Phase 5 → UML                              ║");
        System.out.println("║   Phase 6 → Design Patterns (23)             ║");
        System.out.println("║   Phase 7 → Refactoring                      ║");
        System.out.println("║   Phase 8 → Real LLD Problems (25+)          ║");
        System.out.println("║   Phase 9 → Advanced Topics                  ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
}
