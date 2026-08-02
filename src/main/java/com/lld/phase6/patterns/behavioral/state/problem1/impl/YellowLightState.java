package com.lld.phase6.patterns.behavioral.state.problem1.impl;

import com.lld.phase6.patterns.behavioral.state.problem1.TrafficLight;
import com.lld.phase6.patterns.behavioral.state.problem1.TrafficLightState;

public class YellowLightState implements TrafficLightState {
    @Override
    public void next(TrafficLight trafficLight) {
        System.out.println("YELLOW -> RED");
        trafficLight.setState(new RedLightState());
    }

    @Override
    public String getColor() {
        return "YELLOW";
    }

    @Override
    public int getDurationSeconds() {
        return 5;
    }
}
