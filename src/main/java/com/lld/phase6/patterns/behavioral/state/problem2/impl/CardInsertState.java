package com.lld.phase6.patterns.behavioral.state.problem2.impl;

import com.lld.phase6.patterns.behavioral.state.problem2.ATM;
import com.lld.phase6.patterns.behavioral.state.problem2.ATMState;

public class CardInsertState implements ATMState {
    private static final int CORRECT_PIN = 1234;


    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card already inserted. Please enter your PIN.");
    }

    @Override
    public void enterPin(ATM atm, int pinNumber) {
        System.out.println("Verifying pin...");
        if (pinNumber == CORRECT_PIN) {
            System.out.println("PIN is correct. You can now withdraw money.");
            atm.setState(new PinVerifiedState());
        } else {
            System.out.println("Incorrect PIN. Please try again.");
            atm.setState(new IdleState());
        }
    }

    @Override
    public void withdraw(ATM atm, double amt) {
        System.out.println("Please enter your PIN before withdrawing money.");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Please eject your card");
        atm.setState(new IdleState());
    }
}
