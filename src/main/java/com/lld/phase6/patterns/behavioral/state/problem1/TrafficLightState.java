package com.lld.phase6.patterns.behavioral.state.problem1;

public interface TrafficLightState {

    void next(TrafficLight trafficLight);
    String getColor();
    int getDurationSeconds();
}
