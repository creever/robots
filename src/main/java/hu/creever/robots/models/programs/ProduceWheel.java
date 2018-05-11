package hu.creever.robots.models.programs;

import hu.creever.robots.exceptions.InsufficientProductException;
import hu.creever.robots.exceptions.WrongProductClassNameException;
import hu.creever.robots.helpers.Log;
import hu.creever.robots.models.Product;
import hu.creever.robots.models.Robot;

public class ProduceWheel extends Program {

    @Override
    public void run() {
        try {
            Product product = this.robot.productSupplier.get();
            this.robot.store(product);
            Log.info("------------------------------------------------------------------------------------");
            Log.info(product.getName() + " has been successfully Completed on " + this.robot.getName() + " - Storage Quantity: " + this.robot.getProductStorage().get(product.getClass().getSimpleName()).size());
            Log.info("------------------------------------------------------------------------------------");
        } catch (InsufficientProductException e) {
            Log.info("Not enough product to build for " + robot.getName() + " in phase " + this.robot.getCurrentPhase().name());
        } catch (WrongProductClassNameException e) {
            Log.info("Wrong product class name " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
