package com.example.jaspreet.demo.model;

import com.graphhopper.jsprit.core.problem.Location;

public class Parameters {

  private double vehicleCapacity;
  private int numberOfVehicles;
  private boolean hardTimeWindow;
  private boolean backhaulRequired;
  private double fixedCostPerTrip;
  private double costPerUnitDistance;
  private double costPerUnitTime;
  private String originAddress;
  private Location originlocation;

    public Parameters(double vehicleCapacity, int numberOfVehicles, boolean hardTimeWindow, boolean backhaulRequired,
                      double fixedCostPerTrip, double costPerUnitDistance, double costPerUnitTime, double originLongitude,
                      double originLatitude) {
        this.vehicleCapacity = vehicleCapacity;
        this.numberOfVehicles = numberOfVehicles;
        this.hardTimeWindow = hardTimeWindow;
        this.backhaulRequired = backhaulRequired;
        this.fixedCostPerTrip = fixedCostPerTrip;
        this.costPerUnitDistance = costPerUnitDistance;
        this.costPerUnitTime = costPerUnitTime;
        this.originlocation = Location.newInstance(originLatitude, originLongitude);
    }

    public Parameters(double vehicleCapacity, int numberOfVehicles, boolean hardTimeWindow, boolean backhaulRequired,
                      double fixedCostPerTrip, double costPerUnitDistance, double costPerUnitTime, String originAddress) {
        this.vehicleCapacity = vehicleCapacity;
        this.numberOfVehicles = numberOfVehicles;
        this.hardTimeWindow = hardTimeWindow;
        this.backhaulRequired = backhaulRequired;
        this.fixedCostPerTrip = fixedCostPerTrip;
        this.costPerUnitDistance = costPerUnitDistance;
        this.costPerUnitTime = costPerUnitTime;
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

    public Location getOriginlocation() {
        return originlocation;
    }

    public void setOriginlocation(Location originlocation) {
        this.originlocation = originlocation;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public double getCostPerUnitTime() {
        return costPerUnitTime;
    }

    public void setCostPerUnitTime(double costPerUnitTime) {
        this.costPerUnitTime = costPerUnitTime;
    }
}
