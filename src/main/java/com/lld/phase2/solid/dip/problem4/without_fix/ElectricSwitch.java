package com.lld.phase2.solid.dip.problem4.without_fix;

// VIOLATION: high-level class directly depends on low-level class
// ElectricSwitch changes if you swap LightBulb for Fan
public class ElectricSwitch {

    private LightBulb bulb = new LightBulb();  // ← hardwired concrete

    public void operate(boolean on) {
        if (on) bulb.turnOn();
        else    bulb.turnOff();
    }
}
