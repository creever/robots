package hu.creever.robots.controllers;

import hu.creever.robots.models.FactoryConfig;

public abstract class BaseController {

    abstract public RobotController initiate(FactoryConfig config);
    abstract public void start();
    abstract public void stop();
    abstract public void abort();

}
