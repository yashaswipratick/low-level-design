package com.lld.phase6.patterns.behavioral.strategy.problem2;

import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Location;
import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Route;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.impl.FastestRoutingStrategy;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.impl.NoTollsRoutingStrategy;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.impl.ScenicRoutingStrategy;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.impl.ShortestRoutingStrategy;

public class NavigatorDriver {

    public static void main(String[] args) {
        Location home   = new Location("home",   40.7128, -74.0060);
        Location office = new Location("office", 40.7580, -73.9855);

        Navigator navigator = new Navigator(new FastestRoutingStrategy());

        System.out.println("=== Fastest Route ===");
        print(navigator.calculateRoute(home, office));

        System.out.println("\n=== Shortest Route ===");
        navigator.setRouteStrategy(new ShortestRoutingStrategy());
        print(navigator.calculateRoute(home, office));

        System.out.println("\n=== Avoid Tolls ===");
        navigator.setRouteStrategy(new NoTollsRoutingStrategy());
        print(navigator.calculateRoute(home, office));

        System.out.println("\n=== Scenic Route ===");
        navigator.setRouteStrategy(new ScenicRoutingStrategy());
        print(navigator.calculateRoute(home, office));
    }

    private static void print(Route route) {
        System.out.println("Description : " + route.getId());
        System.out.println("Distance    : " + route.getDistance() + " km");
        System.out.println("ETA         : " + route.getEta() + " min");
    }
}

