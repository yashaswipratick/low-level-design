package com.lld.phase6.patterns.behavioral.state.problem1;

import com.lld.phase6.patterns.behavioral.state.problem1.impl.RedLightState;

public class TrafficLight {

    private TrafficLightState state;

    public TrafficLight() {
        this.state = new RedLightState();
    }

    public void setState(TrafficLightState state) {
        this.state = state;
    }

    public void next() {
        state.next(this);
    }

    public void display() {
        System.out.println("Current: " + state.getColor() + " (" + state.getDurationSeconds() + "s)");
    }
}
