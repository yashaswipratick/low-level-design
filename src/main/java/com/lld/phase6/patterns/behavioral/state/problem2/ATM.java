package com.lld.phase6.patterns.behavioral.state.problem2;

import com.lld.phase6.patterns.behavioral.state.problem2.impl.IdleState;

public class ATM {

    private ATMState state;
    private double balance;

    public ATM() {
        this.state = new IdleState();
    }

    public void setState(ATMState state) {
        this.state = state;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void insertCard() {
        state.insertCard(this);
    }

    public void enterPin(int pinNumber) {
        state.enterPin(this, pinNumber);
    }

    public void withdraw(double amt) {
        state.withdraw(this, amt);
    }

    public void ejectCard() {
        state.ejectCard(this);
    }

    public void loadCash(double amt) {
        this.balance += amt;
        System.out.println("ATM loaded with cash. Current balance: " + this.balance);
    }
}
