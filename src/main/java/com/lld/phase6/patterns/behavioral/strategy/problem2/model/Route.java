package com.lld.phase6.patterns.behavioral.strategy.problem2.model;

import java.time.LocalDateTime;

public class Route {

    private String id;
    private double distance;
    private int eta;

    public Route(String id, double distance, int eta) {
        this.id = id;
        this.distance = distance;
        this.eta = eta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", distance=" + distance +
                ", eta=" + eta +
                '}';
    }
}
