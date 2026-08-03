package com.lld.phase6.patterns.behavioral.state.problem2;

public interface ATMState {

    void insertCard(ATM atm);
    void enterPin(ATM atm, int pinNumber);
    void withdraw(ATM atm, double amt);
    void ejectCard(ATM atm);
}
