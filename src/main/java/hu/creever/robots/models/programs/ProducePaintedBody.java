package hu.creever.robots.models.programs;

import hu.creever.robots.models.products.PaintedBody;

public class ProducePaintedBody extends Program {

    @Override
    public PaintedBody run(String name) {
        PaintedBody paintedBody = new PaintedBody();
        paintedBody.setName(name);
        return paintedBody;
    }
}
