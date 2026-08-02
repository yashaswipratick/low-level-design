package com.lld.phase6.patterns.behavioral.strategy.problem2.service;

import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Location;
import com.lld.phase6.patterns.behavioral.strategy.problem2.model.Route;

public interface RouteStrategy {

    Route calculate(Location from, Location to);
}
