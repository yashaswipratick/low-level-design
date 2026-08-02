package com.lld.phase6.patterns.behavioral.strategy.problem2.service.impl;

import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Location;
import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Route;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.RouteStrategy;

public class ScenicRoutingStrategy implements RouteStrategy {


    @Override
    public Route calculate(Location to, Location from) {
        double distance = 95.0;
        return new Route("Via Jungle", distance, 85);
    }
}
