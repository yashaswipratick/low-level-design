package com.lld.phase6.patterns.behavioral.state.problem2.impl;

import com.lld.phase6.patterns.behavioral.state.problem2.ATM;
import com.lld.phase6.patterns.behavioral.state.problem2.ATMState;

public class IdleState implements ATMState {


    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card inserted. Please enter your PIN.");
        atm.setState(new CardInsertState());
    }

    @Override
    public void enterPin(ATM atm, int pinNumber) {
        System.out.println("PLease insert your card first");
    }

    @Override
    public void withdraw(ATM atm, double amt) {
        System.out.println("Please enter your PIN first");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("No card to eject");
    }
}
