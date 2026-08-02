package com.lld.phase2.solid.isp.problem1;

/** Human employee — legitimately implements all Worker capabilities. */
public class HumanEmployee implements Worker {

    private final String name;

    public HumanEmployee(String name) { this.name = name; }

    @Override public void work()            { System.out.println(name + " is working..."); }
    @Override public void eat()             { System.out.println(name + " is eating lunch..."); }
    @Override public void sleep()           { System.out.println(name + " is resting..."); }
    @Override public void attendMeeting()   { System.out.println(name + " joined the standup..."); }
    @Override public void submitTimesheet() { System.out.println(name + " submitted timesheet."); }
}
