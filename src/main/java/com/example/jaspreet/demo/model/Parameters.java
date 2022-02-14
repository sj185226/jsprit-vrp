package com.example.jaspreet.demo.model;

import com.graphhopper.jsprit.core.problem.Location;

import java.time.LocalTime;

public class Parameters {

    private double vehicleCapacity;
    private int numberOfVehicles;
    private LocalTime vehicleStartTime;
    private boolean hardTimeWindow;
    private boolean backhaulRequired;
    private double fixedCostPerTrip;
    private double costPerUnitDistance;
    private double costPerUnitTime;
    private String originAddress;
    private Location originlocation;

    public static class ParametersBuilder {

        private double vehicleCapacity;
        private int numberOfVehicles;
        private LocalTime vehicleStartTime = LocalTime.of(9, 0);
        private boolean hardTimeWindow = false;
        private boolean backhaulRequired = false;
        private double fixedCostPerTrip = 0;
        private double costPerUnitDistance = 1;
        private double costPerUnitTime = 0;
        private String originAddress;
        private Location originlocation;

        public ParametersBuilder(double vehicleCapacity, int numberOfVehicles,
                Location originLocation) {
        this.vehicleCapacity = vehicleCapacity;
        this.numberOfVehicles = numberOfVehicles;
        this.originlocation = originLocation;
        this.originAddress = originLocation.toString();
    }

    public ParametersBuilder(double vehicleCapacity, int numberOfVehicles,
            String originAddress) {
        this.vehicleCapacity = vehicleCapacity;
        this.numberOfVehicles = numberOfVehicles;
        this.originAddress = originAddress;

    }

    public void setVehicleStartTime(LocalTime vehicleStartTime) {
        this.vehicleStartTime = vehicleStartTime;
    }

    public void setHardTimeWindow(boolean hardTimeWindow) {
        this.hardTimeWindow = hardTimeWindow;
    }

    public void setBackhaulRequired(boolean backhaulRequired) {
        this.backhaulRequired = backhaulRequired;
    }

    public void setFixedCostPerTrip(double fixedCostPerTrip) {
        this.fixedCostPerTrip = fixedCostPerTrip;
    }

    public void setCostPerUnitDistance(double costPerUnitDistance) {
        this.costPerUnitDistance = costPerUnitDistance;
    }

    public void setCostPerUnitTime(double costPerUnitTime) {
        this.costPerUnitTime = costPerUnitTime;
    }

    public Parameters build() {
        return new Parameters(this);
    }

}

private Parameters(ParametersBuilder builder) {
    this.backhaulRequired = builder.backhaulRequired;
    this.costPerUnitDistance = builder.costPerUnitDistance;
    this.costPerUnitTime = builder.costPerUnitTime;
    this.fixedCostPerTrip = builder.fixedCostPerTrip;
    this.hardTimeWindow = builder.hardTimeWindow;
    this.numberOfVehicles = builder.numberOfVehicles;
    this.originAddress = builder.originAddress;
    this.originlocation = builder.originlocation;
    this.vehicleCapacity = builder.vehicleCapacity;
    this.vehicleStartTime = builder.vehicleStartTime;
    }

    public double getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public boolean isHardTimeWindow() {
        return hardTimeWindow;
    }

    public boolean isBackhaulRequired() {
        return backhaulRequired;
    }

    public double getFixedCostPerTrip() {
        return fixedCostPerTrip;
    }

    public double getCostPerUnitDistance() {
        return costPerUnitDistance;
    }

    public Location getOriginlocation() {
        return originlocation;
    }

    public void setOriginLocation(Location origiLocation) {
        this.originlocation = origiLocation;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public double getCostPerUnitTime() {
        return costPerUnitTime;
    }

    public LocalTime getVehicleStartTime() {
        return vehicleStartTime;
    }
}
