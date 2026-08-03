package com.lld.phase6.patterns.behavioral.state.problem2;

public class ATMDriver {

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.loadCash(10000.0);

        System.out.println("=== Happy Path: full transaction ===");
        atm.insertCard();
        atm.enterPin(1234);
        atm.withdraw(500.0);
        atm.ejectCard();

        System.out.println("\n=== Wrong PIN: should reset to Idle ===");
        atm.insertCard();
        atm.enterPin(9999);
        // now in Idle — withdraw should complain
        atm.withdraw(100.0);

        System.out.println("\n=== Operations before inserting card ===");
        atm.enterPin(1234);
        atm.withdraw(100.0);
        atm.ejectCard();

        System.out.println("\n=== Insufficient balance ===");
        atm.insertCard();
        atm.enterPin(1234);
        atm.withdraw(99999.0);  // more than balance

        System.out.println("\n=== Eject card without withdrawing ===");
        atm.insertCard();
        atm.enterPin(1234);
        atm.ejectCard();  // skip withdrawal, go back to Idle
        atm.withdraw(100.0);  // should complain — no card
    }
}

