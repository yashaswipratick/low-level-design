package com.lld.phase6.patterns.behavioral.state.problem2.impl;

import com.lld.phase6.patterns.behavioral.state.problem2.ATM;
import com.lld.phase6.patterns.behavioral.state.problem2.ATMState;

public class PinVerifiedState implements ATMState {
    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card already inserted.");
    }

    @Override
    public void enterPin(ATM atm, int pinNumber) {
        System.out.println("Pin already verified");
    }

    @Override
    public void withdraw(ATM atm, double amt) {
        if (amt <= atm.getBalance()) {
            System.out.println("Withdrawing " + amt);
            atm.setBalance(atm.getBalance() - amt);
            System.out.println("Remaining balance: " + atm.getBalance());
            atm.setState(new DispenseState());
        } else {
            System.out.println("Insufficient balance. Please try again.");
            atm.setState(new IdleState());
        }
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Please eject your card");
        atm.setState(new IdleState());
    }
}
