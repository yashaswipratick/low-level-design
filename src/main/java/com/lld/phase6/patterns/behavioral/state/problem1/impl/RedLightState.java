package com.lld.phase6.patterns.behavioral.state.problem1.impl;

import com.lld.phase6.patterns.behavioral.state.problem1.TrafficLight;
import com.lld.phase6.patterns.behavioral.state.problem1.TrafficLightState;

public class RedLightState implements TrafficLightState {

    @Override
    public void next(TrafficLight trafficLight) {
        System.out.println("RED -> GREEN");
        trafficLight.setState(new GreenLightState());
    }

    @Override
    public String getColor() {
        return "RED";
    }

    @Override
    public int getDurationSeconds() {
        return 60;
    }
}
