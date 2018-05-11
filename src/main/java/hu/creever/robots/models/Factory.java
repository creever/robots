package hu.creever.robots.models;

import hu.creever.robots.controllers.RobotController;
import hu.creever.robots.exceptions.MissingFactoryConfigPropertyException;
import hu.creever.robots.exceptions.NoPhaseAvailableException;
import hu.creever.robots.exceptions.NoRobotAvailableException;
import hu.creever.robots.exceptions.NotInitializedFactoryException;
import hu.creever.robots.helpers.Log;

import java.io.IOException;
import java.util.ArrayList;

public class Factory {
    private static Factory ourInstance = new Factory();

    public static Factory getInstance() {
        return ourInstance;
    }

    private FactoryConfig config;

    private String defaultConfigFile = "./config.json";
    private RobotController controller;

    private Factory() {
    }

    public String getProductFolder() {
        return this.getClass().getPackage().getName() + ".products.";
    }

    public Factory init(String configFile) {
        Log.info("Start factory initialization");

        if(configFile == null)
            configFile = this.defaultConfigFile;

        try {
            this.config = new FactoryConfig(configFile);
            this.controller = new RobotController();

            Log.info("Factory has been successfully initialized...");
            Log.info("Robots: " + config.getRobots());
            Log.info("Phases: " + config.getPhases());
        }
        catch (MissingFactoryConfigPropertyException e) {
            Log.error("Missing config property:" + e.getMessage());
        }
        catch (IOException e) {
            Log.error("Config file does not found at " + configFile);
        }

        return this;
    }

    public void start() throws NoRobotAvailableException, NoPhaseAvailableException {

        if(this.config == null)
            throw new NotInitializedFactoryException();

        if(this.getRobots().size() == 0)
            throw new NoRobotAvailableException();

        if(this.getRobots().size() == 0)
            throw new NoRobotAvailableException();

        this.controller.initiate(this.config).start();
    }

    public ArrayList<Robot> getRobots() {
        return this.config.getRobots();
    }
}
