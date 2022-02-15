package com.example.jaspreet.demo.model;

import java.time.LocalTime;

import com.graphhopper.jsprit.core.problem.Location;

public class Cashpoint {
    private String name;
    private Location location;
    private LocalTime windowStartTime;
    private LocalTime windowEndTime;
    private int serviceTime;
    private double pickupAmount;
    private double deliveryAmount;

    public Cashpoint(String name, Location location, LocalTime windowStartTime,
            LocalTime windowEndTime, int serviceTime, double pickupAmount, double deliveryAmount) {
        this.name = name;
        this.location = location;
        this.windowStartTime = windowStartTime;
        this.windowEndTime = windowEndTime;
        this.serviceTime = serviceTime;
        this.pickupAmount = pickupAmount;
        this.deliveryAmount = deliveryAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalTime getWindowStartTime() {
        return windowStartTime;
    }

    public void setWindowStartTime(LocalTime windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    public LocalTime getWindowEndTime() {
        return windowEndTime;
    }

    public void setWindowEndTime(LocalTime windowEndTime) {
        this.windowEndTime = windowEndTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double getPickupAmount() {
        return pickupAmount;
    }

    public void setPickupAmount(double pickupAmount) {
        this.pickupAmount = pickupAmount;
    }

    public double getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(double deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }
}