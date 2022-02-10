package com.example.jaspreet.demo.model;

public class DataMatrix {

    private double[][] distanceMatrix;
    private double[][] timeMatrix;

    public DataMatrix() {
    }

    public DataMatrix(double[][] distanceMatrix, double[][] timeMatrix) {
        this.distanceMatrix = distanceMatrix;
        this.timeMatrix = timeMatrix;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(double[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public double[][] getTimeMatrix() {
        return timeMatrix;
    }

    public void setTimeMatrix(double[][] timeMatrix) {
        this.timeMatrix = timeMatrix;
    }

}