package hu.creever.robots.models;

public abstract class Product {

    protected String name = "";
    protected boolean producible = false;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
