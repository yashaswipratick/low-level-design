package com.lld.phase6.patterns.behavioral.state.problem3;

public class IdleState implements VendingMachineState{
    @Override
    public void insertMoney(VendingMachine vm, double amt) {
        System.out.println("inserted amount " + amt);
        vm.addMoney(amt);
        vm.setState(new HasMoneyState());
    }

    @Override
    public void selectItem(VendingMachine vm, String item) {
        System.out.println("Item not selected");
    }

    @Override
    public void dispenseItem(VendingMachine vm) {
        System.out.println("Item not selected");
    }

    @Override
    public void cancelAndRefund(VendingMachine vm) {
        System.out.println("Item not selected");
    }
}
