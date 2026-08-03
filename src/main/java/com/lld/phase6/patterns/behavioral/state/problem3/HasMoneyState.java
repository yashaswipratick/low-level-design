package com.lld.phase6.patterns.behavioral.state.problem3;

public class HasMoneyState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine vm, double amt) {
        System.out.println("Amount already inserted. Please select an item.");
    }

    @Override
    public void selectItem(VendingMachine vm, String item) {
        if (vm.getItemPriceMap().containsKey(item)) {
            double price = vm.getItemPriceMap().get(item);
            if (vm.getInsertedAmount() >= price) {
                vm.setSelectedItem(item);
                System.out.println("Item selected: " + item);
                vm.setState(new SelectItemState());
            } else {
                System.out.println("Insufficient funds. Please insert more money.");
                vm.setState(new HasMoneyState());
            }
        } else {
            System.out.println("Invalid item selected.");
            vm.setState(new IdleState());
        }
    }

    @Override
    public void dispenseItem(VendingMachine vm) {
        System.out.println("PLease select Item");
    }

    @Override
    public void cancelAndRefund(VendingMachine vm) {
        System.out.println("Refund money...");
        vm.refundMoney();
        vm.setState(new IdleState());
    }
}
