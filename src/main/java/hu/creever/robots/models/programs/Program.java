package hu.creever.robots.models.programs;

import hu.creever.robots.helpers.Log;
import hu.creever.robots.models.Robot;

public abstract class Program implements Runnable {

    protected Robot robot;

    public Robot getRobot() {
        return robot;
    }

    public Program setRobot(Robot robot) {
        this.robot = robot;
        return this;
    }

    public void info() {
        Log.info(this.toString());
    }

}
