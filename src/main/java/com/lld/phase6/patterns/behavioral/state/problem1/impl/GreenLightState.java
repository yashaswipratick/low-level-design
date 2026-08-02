package com.lld.phase6.patterns.behavioral.state.problem1.impl;

import com.lld.phase6.patterns.behavioral.state.problem1.TrafficLight;
import com.lld.phase6.patterns.behavioral.state.problem1.TrafficLightState;

public class GreenLightState implements TrafficLightState {
    @Override
    public void next(TrafficLight trafficLight) {
        System.out.println("GREEN -> YELLOW");
        trafficLight.setState(new YellowLightState());
    }

    @Override
    public String getColor() {
        return "GREEN";
    }

    @Override
    public int getDurationSeconds() {
        return 45;
    }
}
