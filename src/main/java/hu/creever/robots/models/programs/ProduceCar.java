package hu.creever.robots.models.programs;

import hu.creever.robots.exceptions.InsufficientProductException;
import hu.creever.robots.exceptions.WrongProductClassNameException;
import hu.creever.robots.helpers.Log;
import hu.creever.robots.models.Product;

public class ProduceCar extends Program {

    @Override
    public void run() {
        try {
            Product product = this.robot.productSupplier.get();
            this.robot.store(product);
            Log.info(product.getName() + " has been successfully Completed");
        } catch (InsufficientProductException e) {
            Log.error("Not enough product to build for " + robot.getName() + " in phase " + this.robot.getCurrentPhase().name());
        } catch (WrongProductClassNameException e) {
            Log.error("Wrong product class name " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
