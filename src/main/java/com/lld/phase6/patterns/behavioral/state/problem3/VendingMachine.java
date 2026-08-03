package com.lld.phase6.patterns.behavioral.state.problem3;

import java.util.HashMap;
import java.util.Map;

public class VendingMachine {

    private VendingMachineState state;
    private Map<String, Double> itemPriceMap;
    private double insertedAmount = 0;
    private String selectedItem = null;

    public VendingMachine(Map<String, Double> itemPriceMap) {
        this.state = new IdleState();
        this.itemPriceMap = itemPriceMap;
    }

    public void setState(VendingMachineState state) {
        this.state = state;
    }

    public Map<String, Double> getItemPriceMap() {
        return itemPriceMap;
    }

    public void setItemPriceMap(Map<String, Double> itemPriceMap) {
        this.itemPriceMap = itemPriceMap;
    }

    public double getInsertedAmount()            { return insertedAmount; }

    public void setInsertedAmount(double insertedAmount) {
        this.insertedAmount = insertedAmount;
    }

    public void addMoney(double amount)          { insertedAmount += amount; }
    public void refundMoney() {
        System.out.println("Refunding: $" + insertedAmount);
        insertedAmount = 0;
    }
    public void setSelectedItem(String item)     { selectedItem = item; }
    public String getSelectedItem()              { return selectedItem; }

    public void insertMoney(double amt) {
        state.insertMoney(this, amt);
    }

    public void selectItem(String item) {
        state.selectItem(this, item);
    }

    public void dispenseItem() {
        state.dispenseItem(this);
    }

    public void cancelAndRefund() {
        state.cancelAndRefund(this);
    }
}
