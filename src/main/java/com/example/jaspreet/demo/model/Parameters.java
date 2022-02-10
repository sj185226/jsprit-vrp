package com.example.jaspreet.demo.model;

public class Parameters {

  private double vehicleCapacity;
  private int numberOfVehicles;
  private boolean hardTimeWindow;
  private boolean backhaulRequired;
  private double fixedCostPerTrip;
  private double costPerUnitDistance;
  private double originLongitude;
  private double originLatitude;
  private String originAddress;


    public Parameters(double vehicleCapacity, int numberOfVehicles, boolean hardTimeWindow, boolean backhaulRequired,
                      double fixedCostPerTrip, double costPerUnitDistance, double originLongitude, double originLatitude,
                      String originAddress) {
        this.vehicleCapacity = vehicleCapacity;
        this.numberOfVehicles = numberOfVehicles;
        this.hardTimeWindow = hardTimeWindow;
        this.backhaulRequired = backhaulRequired;
        this.fixedCostPerTrip = fixedCostPerTrip;
        this.costPerUnitDistance = costPerUnitDistance;
        this.originLongitude = originLongitude;
        this.originLatitude = originLatitude;
        this.originAddress = originAddress;
    }

    public double getVehicleCapacity() {
        return vehicleCapacity;
    }

    public void setVehicleCapacity(double vehicleCapacity) {
        this.vehicleCapacity = vehicleCapacity;
    }

    public int getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public void setNumberOfVehicles(int numberOfVehicles) {
        this.numberOfVehicles = numberOfVehicles;
    }

    public boolean isHardTimeWindow() {
        return hardTimeWindow;
    }

    public void setHardTimeWindow(boolean hardTimeWindow) {
        this.hardTimeWindow = hardTimeWindow;
    }

    public boolean isBackhaulRequired() {
        return backhaulRequired;
    }

    public void setBackhaulRequired(boolean backhaulRequired) {
        this.backhaulRequired = backhaulRequired;
    }

    public double getFixedCostPerTrip() {
        return fixedCostPerTrip;
    }

    public void setFixedCostPerTrip(double fixedCostPerTrip) {
        this.fixedCostPerTrip = fixedCostPerTrip;
    }

    public double getCostPerUnitDistance() {
        return costPerUnitDistance;
    }

    public void setCostPerUnitDistance(double costPerUnitDistance) {
        this.costPerUnitDistance = costPerUnitDistance;
    }

    public double getOriginLongitude() {
        return originLongitude;
    }

    public void setOriginLongitude(double originLongitude) {
        this.originLongitude = originLongitude;
    }

    public double getOriginLatitude() {
        return originLatitude;
    }

    public void setOriginLatitude(double originLatitude) {
        this.originLatitude = originLatitude;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }
}
