package hu.creever.robots.models;

import hu.creever.robots.exceptions.InsufficientProductException;
import hu.creever.robots.exceptions.WrongProductClassNameException;
import hu.creever.robots.helpers.Log;
import hu.creever.robots.interfaces.StatusListener;
import hu.creever.robots.models.programs.Program;
import hu.creever.robots.models.robot.Phase;
import hu.creever.robots.models.robot.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Robot implements StatusListener {

    /*
     * Robot's name
     */
    private String name;

    /*
     * Robot's serial number
     */
    private String serialNumber;

    /*
     * Available Product collection for the process
     */
    private HashMap<String, ArrayList<Product>> productStorage = new HashMap<>();

    /*
     * Actual process phase of the robot
     */
    private Phase currentPhase;

    /*
     * Loaded program
     */
    private Program program;

    /*
    * Robot actual status
    */
    private Status status = Status.IDLE;

    /*
     * Robot's own thread
     */
    private ScheduledFuture thread;

    public Robot(String name, String serialNumber) {
        this.name = name;
        this.serialNumber = serialNumber;
    }

    public ScheduledFuture getThread() {
        return thread;
    }

    public void setThread(ScheduledFuture thread) {
        this.thread = thread;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Phase getCurrentPhase() {
        return this.currentPhase;
    }

    public HashMap<String, ArrayList<Product>> getProductStorage() {
        return this.productStorage;
    }

    public void setPhase(Phase phase) {
        this.currentPhase = phase;

        if(this.currentPhase.hasProgram()) {
            this.program = this.currentPhase.getProgram.get();
            this.program.setRobot(this);
        }
    }

    public int getAvailableProduct(String name) {
        if(!this.productStorage.containsKey(name))
            return 0;

        return this.productStorage.get(name).size();
    }

    /*
    * Only one output supported
    */
    public Supplier<Product> productSupplier = () -> {

        if(!canBuild()) {
            this.onHold();
            throw new InsufficientProductException();
        }

        this.onStart();

        // Remove products from the storage
        this.currentPhase.getInput().forEach((name, quantity) -> this.removeProductFromStorage.accept(name, quantity));

        Map.Entry<String,Integer> entry = this.currentPhase.getOutput().entrySet().iterator().next();

        try {
            Class productClass = Class.forName(Factory.getInstance().getProductFolder() + entry.getKey());
            Product product = (Product) productClass.newInstance();
            product.setName(entry.getKey());
            this.onFinished();

            return product;
        } catch (Exception e) {
            this.onFailure();
            throw new WrongProductClassNameException(entry.getKey());
        }
    };

    private BiConsumer<String, Integer> removeProductFromStorage = (productName, quantity) -> IntStream.range(0,quantity).forEach(i -> this.productStorage.get(productName).remove(0));

    private boolean canBuild() {
        return this.currentPhase.getInput()
                .entrySet()
                .stream()
                .allMatch(e -> this.getAvailableProduct(e.getKey()) >= e.getValue());
    }

    public void store(Product product) {
        String className = product.getClass().getSimpleName();
        if(this.productStorage.containsKey(className))
            this.productStorage.get(className).add(product);
        else {
            ArrayList<Product> list = new ArrayList<>();
            list.add(product);
            this.productStorage.put(className, list);
        }
    }

    /*
     * If Something is missing from the Current phase's Inputs
     */
    public String getMissingProduct() {

        Optional<String> missingProduct = this.currentPhase.getInput()
                .entrySet()
                .stream()
                .filter(e -> this.getAvailableProduct(e.getKey()) < e.getValue())
                .map(Map.Entry::getKey).findAny();

        return missingProduct.orElse(null);
    }

    @Override
    public void onFinished() {
        this.status = Status.DONE;
    }

    @Override
    public void onStart() {
        this.status = Status.IN_PROGRESS;
    }

    @Override
    public void onFailure() {
        this.status = Status.FAILIURE;
    }

    @Override
    public void onHold() {
        this.status = Status.WAITING_FOR_PARTS;
    }

    @Override
    public void onStop() {
        this.status = Status.STOPPED;
    }

    public Program getProgram() {
        return program;
    }

    public void stop() {
        this.thread.cancel(false);
        this.onStop();
        Log.info(this.name + " stopped.");
    }

    public String toString() {
        return "Name: " + this.name + ", SN: " + this.serialNumber + ", Actual Phase: " + this.currentPhase;
    }
}
