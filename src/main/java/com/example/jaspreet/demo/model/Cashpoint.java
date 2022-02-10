package com.example.jaspreet.demo.model;

import java.time.LocalTime;

public class Cashpoint {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private LocalTime windowStartTime;
    private LocalTime windowEndTime;
    private int serviceTime;
    private double pickupAmount;
    private double deliveryAmount;

    public Cashpoint(String name, String address, double latitude, double longitude, LocalTime windowStartTime,
            LocalTime windowEndTime, int serviceTime, double pickupAmount, double deliveryAmount) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLocation() {
        return address;
    }

    public void setLocation(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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