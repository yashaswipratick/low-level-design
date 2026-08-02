package com.lld.phase2.solid.isp.problem1;

// TODO: ISP VIOLATION — fat interface forces ALL implementors to handle ALL methods.
//
// RobotWorker cannot eat, sleep, or submit a timesheet.
// It is FORCED to throw UnsupportedOperationException for those methods.
//
// Result:
//   - runPayroll(Worker worker) crashes at runtime if passed a RobotWorker
//   - The interface lies: it promises capabilities the implementor can't deliver
//   - Type system provides no compile-time protection against misuse
//
// Your task: split this interface by capability.
// Each implementor should only implement what it CAN actually do — no throws anywhere.
//
// After the fix: runPayroll(Trackable worker) won't even COMPILE with a RobotWorker.

public interface Worker {

    // Professional capability
    void work();

    // Biological need — robots don't have this
    void eat();

    // Biological need — robots don't have this
    void sleep();

    // Collaboration — robots CAN do this (via API)
    void attendMeeting();

    // Admin / HR process — robots don't track hours
    void submitTimesheet();
}
