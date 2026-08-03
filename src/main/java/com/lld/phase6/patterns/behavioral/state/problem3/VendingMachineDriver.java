package com.lld.phase6.patterns.behavioral.state.problem3;

import java.util.Map;

public class VendingMachineDriver {

    public static void main(String[] args) {
        Map<String, Double> items = Map.of(
                "Coke",   1.50,
                "Chips",  2.00,
                "Water",  1.00
        );

        VendingMachine vm = new VendingMachine(items);

        System.out.println("=== Happy Path: exact change ===");
        vm.insertMoney(1.50);
        vm.selectItem("Coke");
        vm.dispenseItem();     // change = 0.0

        System.out.println("\n=== Happy Path: with change ===");
        vm.insertMoney(5.00);
        vm.selectItem("Chips");
        vm.dispenseItem();     // change = 3.00

        System.out.println("\n=== Insufficient funds: top-up and retry ===");
        vm.insertMoney(0.50);
        vm.selectItem("Water");   // 0.50 < 1.00 — should stay in HasMoneyState
        vm.insertMoney(0.50);     // now 1.00 total
        vm.selectItem("Water");   // should succeed
        vm.dispenseItem();

        System.out.println("\n=== Cancel after inserting money ===");
        vm.insertMoney(2.00);
        vm.cancelAndRefund();     // refund 2.00, back to Idle

        System.out.println("\n=== Cancel after item selected ===");
        vm.insertMoney(2.00);
        vm.selectItem("Chips");
        vm.cancelAndRefund();     // refund 2.00, back to Idle

        System.out.println("\n=== Invalid item ===");
        vm.insertMoney(2.00);
        vm.selectItem("Pizza");   // not in map → error, back to Idle
        vm.insertMoney(2.00);     // re-insert after reset

        System.out.println("\n=== Operations out of order ===");
        vm.cancelAndRefund();     // leftover money from above
        vm.selectItem("Coke");    // no money — error
        vm.dispenseItem();        // no item — error
    }
}

