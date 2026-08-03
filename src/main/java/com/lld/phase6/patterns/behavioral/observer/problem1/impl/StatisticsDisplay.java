package com.lld.phase6.patterns.behavioral.observer.problem1.impl;

import com.lld.phase6.patterns.behavioral.observer.problem1.WeatherObserver;

public class StatisticsDisplay implements WeatherObserver {

    private double minTemp = Double.MAX_VALUE;
    private double maxTemp = Double.NEGATIVE_INFINITY;
    private double totalTemp = 0;
    private int count = 0;

    @Override
    public void update(double temperature, double humidity, double pressure) {
        totalTemp += temperature;
        count++;
        if (temperature < minTemp) minTemp = temperature;
        if (temperature > maxTemp) maxTemp = temperature;
        System.out.printf("[Statistics] Min: %.1f | Max: %.1f | Avg: %.1f%n",
                minTemp, maxTemp, totalTemp / count);
    }
}
