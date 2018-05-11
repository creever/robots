package hu.creever.robots.controllers;

import hu.creever.robots.helpers.Log;
import hu.creever.robots.models.Factory;
import hu.creever.robots.models.FactoryConfig;
import hu.creever.robots.models.Product;
import hu.creever.robots.models.Robot;
import hu.creever.robots.models.programs.ProduceCar;
import hu.creever.robots.models.programs.ProducePaintedBody;
import hu.creever.robots.models.programs.ProduceWheel;
import hu.creever.robots.models.robot.Phase;
import hu.creever.robots.models.robot.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RobotController extends BaseController {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);
    private final Random rnd = new Random();

    private int minExecutionTime = 1000; // in miliseconds
    private int maxExecutionTime = 2000; // in miliseconds

    private FactoryConfig config;

    private ScheduledFuture controllerThread;

    private Runnable executionCheck = () -> {
        Log.info("------------------");
        Log.info("Execution check...");
        Log.info("------------------");

        // Allow to step into the next phase
        this.config.getRobots().stream().filter((Robot robot) -> this.isAllowToStopIntoTheNextPhase.test(robot)).forEach(this::nextPhase);

        // Give missing product to robots
        this.config.getRobots().stream().filter((Robot robot) -> (robot.getStatus() == Status.WAITING_FOR_PARTS)).forEach(robot -> {
            Product newProduct = this.generateProduct.apply(robot.getMissingProduct());
            if(newProduct != null) {
                robot.store(newProduct);
                Log.info(robot.getName() + " get a brand new product: " + newProduct.getName());
            }
        });

        // Stop Robot if finished
        this.config.getRobots().stream().filter((Robot robot) -> robot.getCurrentPhase() == Phase.PHASE_STOP && robot.getStatus() != Status.STOPPED).forEach(Robot::stop);

        if (this.config.getRobots().stream().allMatch(robot -> robot.getThread().isDone() || robot.getThread().isCancelled())) {
            Log.info("=========================================================");
            Log.info("The Production was successful!");

            this.config.getRobots().forEach(robot -> {
                Log.info("---------------------------");
                Log.info(robot.getName() + " storage:");
                Log.info("---------------------------");
                robot.getProductStorage().forEach((name, products) -> Log.info(name + ": " + products.size() + " piece(s)"));
            });

            Log.info("-----------------------------------------------------");
            Log.info("Thank you for your attention!");

            this.stop();
        }
    };

    private Predicate<Robot> isAllowToStopIntoTheNextPhase = robot -> {
        if(robot.getStatus() != Status.DONE || robot.getCurrentPhase() == Phase.PHASE_STOP)
            return false;

        return robot.getCurrentPhase().getNextPhaseCondition()
                .entrySet()
                .stream()
                .allMatch(e -> robot.getAvailableProduct(e.getKey()) >= e.getValue());
    };

    public RobotController initiate(FactoryConfig config) {
        this.config = config;

        Phase.PHASE_ONE.setProgram(ProduceWheel.class);
        Phase.PHASE_TWO.setProgram(ProducePaintedBody.class);
        Phase.PHASE_THREE.setProgram(ProduceCar.class);

        return this;
    }

    public void start() {
        Log.info("Start production");
        Log.info("================");

        /*
         * Start all available robots in separated thread
         */
        this.config.getRobots().stream().peek(robot -> robot.setPhase(Phase.PHASE_ONE))
                .forEach((Robot robot) -> {
                    ScheduledFuture task = scheduler.scheduleAtFixedRate(robot.getProgram(), 0, this.getExecutionCheckPeriod(), TimeUnit.MILLISECONDS);
                    robot.setThread(task);
                });

        /*
         * Start robot controller in separated thread
         */
        this.controllerThread = scheduler.scheduleAtFixedRate (this.executionCheck, 0, this.getExecutionCheckPeriod(), TimeUnit.MILLISECONDS);
    }

    private void nextPhase(Robot robot) {
        this.switchPhase(robot, Phase.getNextPhase(robot.getCurrentPhase()));
    }

    private void switchPhase(Robot robot, Phase phase) {
        Log.info(robot.getName() + " is ready to step into the '" + phase.name() + "' phase");
        robot.setPhase(phase);

        if(robot.getCurrentPhase() == Phase.PHASE_STOP)
            robot.stop();
    }

    public void stop() {
        this.controllerThread.cancel(false);
        Log.info("Robot Controller has been successfully stopped.");
    }

    public void abort() {

    }

    private Function<String, Product> generateProduct = productName -> {
        Product product;

        try {
            Class productClass = Class.forName(Factory.getInstance().getProductFolder() + productName);
            product = (Product) productClass.newInstance();
            product.setName(productName);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return null;
        }

        return product;
    };

    private long getExecutionCheckPeriod() {
        return this.rnd.nextInt(this.maxExecutionTime - this.minExecutionTime) + this.minExecutionTime;
    }
}
