package com.lld.phase6.patterns.behavioral.state.problem3;

import com.lld.phase6.patterns.behavioral.state.problem2.ATM;

public interface VendingMachineState {

    void insertMoney(VendingMachine vm, double amt);
    void selectItem(VendingMachine vm, String item);
    void dispenseItem(VendingMachine vm);
    void cancelAndRefund(VendingMachine vm);
}
