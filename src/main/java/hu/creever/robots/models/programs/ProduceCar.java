package hu.creever.robots.models.programs;

import hu.creever.robots.models.products.Car;

public class ProduceCar extends Program {

    @Override
    public Car run(String name) {
        Car car = new Car();
        car.setName(name);
        return car;
    }
}
