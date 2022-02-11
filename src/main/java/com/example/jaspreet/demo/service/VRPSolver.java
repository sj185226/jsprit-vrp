package com.example.jaspreet.demo.service;

import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.DataMatrix;
import com.example.jaspreet.demo.model.Parameters;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class VRPSolver {

    List<Pickup> pickups = new ArrayList<>();
    List<Delivery> deliveries = new ArrayList<>();
    List<VehicleImpl> vehicles = new ArrayList<>();

    Parameters param;
    VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
            .newInstance(true);
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    public boolean solve(List<Cashpoint> cashpoints, Parameters param, DataMatrix dataMatrix) {
        this.param = param;
        int timeBuffer =0;
        for(int i=0;i<5;i++){
            createServices(cashpoints, timeBuffer);
            createVehicles(param.getNumberOfVehicles());
            vrpBuilder.addAllVehicles(vehicles);
            createCostMatrix(dataMatrix.getDistanceMatrix(), dataMatrix.getTimeMatrix(), cashpoints);
            VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
            vrpBuilder.addAllJobs(pickups);
            vrpBuilder.addAllJobs(deliveries);
            vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
            vrpBuilder.setRoutingCost(costMatrix);
            VehicleRoutingProblem problem = vrpBuilder.build();

            StateManager stateManager = new StateManager(problem);
            ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
            if(param.isBackhaulRequired()) {
                constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(),
                        ConstraintManager.Priority.CRITICAL);
            }

            VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                    .setStateAndConstraintManager(stateManager, constraintManager)
                    .buildAlgorithm();

            Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
            VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

    //        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");
            SolutionPrinter.print(bestSolution);
            if (!bestSolution.getUnassignedJobs().isEmpty()) {
                timeBuffer += 60;
                if(param.isHardTimeWindow()) return false;
            }
            else {
                Plotter plotter = new Plotter(problem, bestSolution);
                plotter.setLabel(Plotter.Label.SIZE);
                plotter.plot("output/solution.png", "solution");
                SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
                new GraphStreamViewer(problem, bestSolution).setRenderDelay(200).display();
                return true;
            }
        }
        return false;
    }

    public void createServices(List<Cashpoint> cashpoints, int timeBuffer){
        for (Cashpoint cashpoint : cashpoints) {

            if ((int) cashpoint.getPickupAmount() != 0) {
                Pickup pickup = Pickup.Builder.newInstance(cashpoint.getName() + "_pickup")
                        .addSizeDimension(0, (int) cashpoint.getPickupAmount())
                        .setLocation(cashpoint.getLocation())
                        .addTimeWindow(getTimeDifference(cashpoint.getWindowStartTime()), getTimeDifference(cashpoint.getWindowEndTime()) + timeBuffer)
                        .setServiceTime(cashpoint.getServiceTime()).build();
                pickups.add(pickup);
            }
            if ((int) cashpoint.getDeliveryAmount() != 0) {
                Delivery delivery = Delivery.Builder.newInstance(cashpoint.getName() + "_delivery")
                        .addSizeDimension(0, (int) cashpoint.getDeliveryAmount())
                        .addTimeWindow(getTimeDifference(cashpoint.getWindowStartTime()), getTimeDifference(cashpoint.getWindowEndTime()) + timeBuffer)
                        .setServiceTime(cashpoint.getServiceTime()).build();
                deliveries.add(delivery);
            }
        }
    }

    public int getTimeDifference(LocalTime timeValue){
        return Math.max(0,(int)param.getVehicleStartTime().until(timeValue, ChronoUnit.MINUTES));
    }


    public void createVehicles(int numberOfVehicles) {
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .addCapacityDimension(0,(int) param.getVehicleCapacity());
        VehicleType vehicleType = vehicleTypeBuilder.build();
        for (int i = 0; i < numberOfVehicles; i++) {
            VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle" + i);
            vehicleBuilder.setStartLocation(param.getOriginlocation());
            vehicleBuilder.setType(vehicleType);
            VehicleImpl vehicle = vehicleBuilder.build();
            vehicles.add(vehicle);
        }
    }

    public void createCostMatrix(double[][] DISTANCE_MATRIX, double[][] TIME_MATRIX, List<Cashpoint> cashpoints){

        for (int i = 0; i < DISTANCE_MATRIX.length; i++) {
            for (int j = 0; j < i; j++) {
                costMatrixBuilder.addTransportDistance(cashpoints.get(i).getLocation().getId(),
                        cashpoints.get(i).getLocation().getId(),
                        DISTANCE_MATRIX[i][j]);
                costMatrixBuilder.addTransportTime(cashpoints.get(i).getLocation().getId(),
                        cashpoints.get(i).getLocation().getId(),
                        TIME_MATRIX[i][j]);
            }
        }
    }



}
