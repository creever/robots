package hu.creever.robots;

import hu.creever.robots.controllers.RobotController;
import hu.creever.robots.models.Factory;

public class Main {

    public static void main(String[] args) {

        String configFile = null;

        if(args.length != 0)
            configFile = args[0];

        /*
        * Initiate the Factory and start the production
        * */
        Factory.getInstance().init(configFile).start();
    }
}
