package com.lld.phase6.patterns.behavioral.state.problem1;

import com.lld.phase6.patterns.behavioral.state.problem1.impl.RedLightState;

public class TrafficLightDriver {

    public static void main(String[] args) {
        TrafficLight light = new TrafficLight();

        System.out.println("=== Full cycle (RED → GREEN → YELLOW → RED) ===");
        light.display();   // RED (60s)
        light.next();
        light.display();   // GREEN (45s)
        light.next();
        light.display();   // YELLOW (5s)
        light.next();
        light.display();   // RED (60s) — cycled back

        System.out.println("\n=== One more cycle to confirm it keeps working ===");
        light.next();
        light.display();   // GREEN
        light.next();
        light.display();   // YELLOW
        light.next();
        light.display();   // RED
    }
}

