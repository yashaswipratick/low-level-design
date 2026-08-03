package com.lld.phase6.patterns.behavioral.state.problem3;

public class SelectItemState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine vm, double amt) {
        System.out.println("Amount already inserted. Please select an item.");
    }

    @Override
    public void selectItem(VendingMachine vm, String item) {
        System.out.println("Item already selected " + item + " Press dispense");
    }

    @Override
    public void dispenseItem(VendingMachine vm) {
        System.out.println("Item selected");
        vm.setState(new DispensingState());
    }

    @Override
    public void cancelAndRefund(VendingMachine vm) {
        System.out.println("Item selected");
        vm.refundMoney();
        vm.setState(new IdleState());
    }
}
