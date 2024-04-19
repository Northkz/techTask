package com.nursultan.order.Order;

public class OrderRequestDTO {
    private String[] origin;
    private String[] destination;

    // Getters and Setters
    public String[] getOrigin() {
        return origin;
    }

    public void setOrigin(String[] origin) {
        this.origin = origin;
    }

    public String[] getDestination() {
        return destination;
    }

    public void setDestination(String[] destination) {
        this.destination = destination;
    }
}