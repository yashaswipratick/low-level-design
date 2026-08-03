package com.lld.phase6.patterns.behavioral.observer.problem1.impl;

import com.lld.phase6.patterns.behavioral.observer.problem1.WeatherObserver;

public class ForecastDisplay implements WeatherObserver {
    private double pressure = 30.0;

    @Override
    public void update(double temperature, double humidity, double pressure) {
        String forecast = "";
        if (pressure > this.pressure) {
            forecast = "Improving weather on the way!";
        } else if (pressure < this.pressure) {
            forecast = "Watch out for cooler, rainy weather.";
        } else {
            forecast = "More of the same.";
        }
        this.pressure = pressure;
        System.out.println("[Forecast] " + forecast);
    }
}
