package com.lld.phase6.patterns.behavioral.observer.problem1.impl;

import com.lld.phase6.patterns.behavioral.observer.problem1.WeatherObserver;

public class CurrentConditionsDisplay implements WeatherObserver {
    @Override
    public void update(double temperature, double humidity, double pressure) {
        System.out.println("Current conditions: " + temperature + "F degrees and " + humidity + "% humidity");
    }
}
