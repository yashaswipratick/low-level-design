package com.lld.phase6.patterns.behavioral.strategy.problem2;

import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Location;
import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Route;
import com.lld.phase6.patterns.behavioral.strategy.problem2.service.RouteStrategy;

public class Navigator {

    private RouteStrategy routeStrategy;

    public Navigator(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    public void setRouteStrategy(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    public Route calculateRoute(Location start, Location end) {
        return routeStrategy.calculate(start, end);
    }
}
