package com.lld.phase6.patterns.behavioral.strategy.problem2.service.impl;

import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Location;
import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Route;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.RouteStrategy;

public class FastestRoutingStrategy implements RouteStrategy {


    @Override
    public Route calculate(Location to, Location from) {
        // Simulate fastest route calculation
        double distance = 45.0;
        return new Route("Via Highway (fastest)", distance, 30);
    }
}
