package com.lld.phase6.patterns.behavioral.observer.problem1;

import com.lld.phase6.patterns.behavioral.observer.problem1.impl.CurrentConditionsDisplay;
import com.lld.phase6.patterns.behavioral.observer.problem1.impl.ForecastDisplay;
import com.lld.phase6.patterns.behavioral.observer.problem1.impl.StatisticsDisplay;

public class WeatherStationDriver {

    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();

        WeatherObserver current    = new CurrentConditionsDisplay();
        WeatherObserver statistics = new StatisticsDisplay();
        WeatherObserver forecast = new ForecastDisplay();

        station.subscribe(current);
        station.subscribe(statistics);
        station.subscribe(forecast);

        station.setMeasurements(25.0, 65.0, 1013.0);
        station.setMeasurements(27.5, 70.0, 1012.5);
        station.setMeasurements(22.0, 80.0, 1008.0);

        System.out.println("\n-- Removing statistics display --");
        station.unsubscribe(statistics);
        station.setMeasurements(20.0, 75.0, 1005.0);  // only current display notified
    }
}
