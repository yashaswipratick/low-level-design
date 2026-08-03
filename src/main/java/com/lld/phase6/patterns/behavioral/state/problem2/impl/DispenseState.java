package com.lld.phase6.patterns.behavioral.state.problem2.impl;

import com.lld.phase6.patterns.behavioral.state.problem2.ATM;
import com.lld.phase6.patterns.behavioral.state.problem2.ATMState;

public class DispenseState implements ATMState {
    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card Already inserted");
    }

    @Override
    public void enterPin(ATM atm, int pinNumber) {
        System.out.println("Pin already verified");
    }

    @Override
    public void withdraw(ATM atm, double amt) {
        System.out.println("Amt Dispensing...");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Amount dispensed. Please eject your card");
        atm.setState(new IdleState());
    }
}
