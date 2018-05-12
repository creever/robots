package hu.creever.robots.models.programs;

import hu.creever.robots.models.products.Wheel;

public class ProduceWheel extends Program {

    @Override
    public Wheel run(String name) {
        Wheel wheel = new Wheel();
        wheel.setName(name);
        return wheel;
    }
}
