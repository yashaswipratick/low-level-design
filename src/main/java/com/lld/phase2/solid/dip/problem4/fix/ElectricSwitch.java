package com.lld.phase2.solid.dip.problem4.fix;

public class ElectricSwitch {

    private final Switchable switchable;

    public ElectricSwitch(Switchable switchable) {
        this.switchable = switchable;
    }

    public void operate(boolean on) {
        if (on) switchable.turnOn();
        else    switchable.turnOff();
    }
}
