package com.lld.phase6.patterns.behavioral.observer.problem1;

import java.util.ArrayList;
import java.util.List;

public class WeatherStation {

    private List<WeatherObserver> observers = new ArrayList<>();
    private double temperature;
    private double humidity;
    private double pressure;

    public void subscribe(WeatherObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(WeatherObserver observer) {
        observers.remove(observer);
    }

    private void notifyAllObservers() {
        observers.forEach(weatherObserver -> weatherObserver.update(temperature, humidity, pressure));
    }

    public void setMeasurements(double temperature, double humidity, double pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        System.out.println("--- Weather updated ---");
        notifyAllObservers();
    }
}
