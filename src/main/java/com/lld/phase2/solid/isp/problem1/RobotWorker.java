package com.lld.phase2.solid.isp.problem1;

// TODO: ISP VIOLATION — RobotWorker is forced to implement methods it cannot fulfill.
//
// eat(), sleep(), submitTimesheet() throw UnsupportedOperationException.
//
// Any caller passing RobotWorker to a method expecting Worker
// will crash at runtime if those methods are called:
//
//   void breakTime(Worker worker) { worker.eat(); }   // crashes for RobotWorker
//   void runPayroll(Worker worker) { worker.submitTimesheet(); }  // crashes

public class RobotWorker implements Worker {

    private final String robotId;

    public RobotWorker(String robotId) { this.robotId = robotId; }

    @Override
    public void work() {
        System.out.println("Robot " + robotId + " processing task...");
    }

    @Override
    public void eat() {
        // VIOLATION: robots don't eat — forced by fat interface
        throw new UnsupportedOperationException("Robots do not eat");
    }

    @Override
    public void sleep() {
        // VIOLATION: robots don't sleep — forced by fat interface
        throw new UnsupportedOperationException("Robots do not sleep");
    }

    @Override
    public void attendMeeting() {
        System.out.println("Robot " + robotId + " connecting to meeting API...");
    }

    @Override
    public void submitTimesheet() {
        // VIOLATION: robots don't track hours — forced by fat interface
        throw new UnsupportedOperationException("Robots do not submit timesheets");
    }
}
