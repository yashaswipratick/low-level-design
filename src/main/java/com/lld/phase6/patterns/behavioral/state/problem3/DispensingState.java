package com.lld.phase6.patterns.behavioral.state.problem3;

public class DispensingState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine vm, double amt) {
        System.out.println("Amount already inserted. Please select an item.");
    }

    @Override
    public void selectItem(VendingMachine vm, String item) {
        System.out.println("Item already selected.");
    }

    @Override
    public void dispenseItem(VendingMachine vm) {
        System.out.println("Dispensing item selected.");
        double change = vm.getInsertedAmount() - vm.getItemPriceMap().get(vm.getSelectedItem());
        System.out.println("Change to " + change);
        vm.setInsertedAmount(0);
        vm.setSelectedItem(null);
        vm.setState(new IdleState());
    }

    @Override
    public void cancelAndRefund(VendingMachine vm) {
        System.out.println("Refund initiatd.");
    }
}
