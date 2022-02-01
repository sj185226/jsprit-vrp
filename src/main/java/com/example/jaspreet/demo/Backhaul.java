package com.example.jaspreet.demo;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.Plotter.Label;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;

import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;

public class Backhaul {
    public static void main(String[] args) {
        /*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

		/*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
		 */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        VehicleType vehicleType = vehicleTypeBuilder.build();

		/*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
		 */
        final double DEPOT_LATITUDE = -36.7786899;
        final double DEPOT_LONGITUDE = -59.8626802;
        final int WEIGHT_INDEX = 0;
        // vehicle capacity
        final int WEIGHT_VALUE = 7;
        final int VEHICLE_COUNT = 6;
        final int CP_COUNT = 30;
		/*
         * build pickups and deliveries at the required locations, each with a capacity-demand of 1.
		 */
        
        final double[][] PICKUPANDDELIVERYAMOUNTS = {
            {47879.6992481203,238703.759398496},
            {161958.646616541,572.932330827068},
            {309954.135338346,307227.067669173},
            {245600.751879699,246.616541353383},
            {189553.383458647,70.6766917293233},
            {465657.142857143,5962.40601503759},
            {381093.233082707,703.759398496241},
            {76290.2255639098,9404.51127819549},
            {497333.082706767,114.285714285714},
            {428606.015037594,144.360902255639},
            {869887.218045113,28482.7067669173},
            {174815.789473684,85.7142857142857},
            {623769.92481203,156.390977443609},
            {193336.090225564,5308.27067669173},
            {709138.345864662,655.639097744361},
            {124157.894736842,94.7368421052632},
            {831227.067669173,105375.939849624},
            {528539.097744361,315.789473684211},
            {244470.676691729,0},
            {738517.293233083,156.390977443609},
            {354897.744360902,439.097744360902},
            {413818.796992481,0},
            {680015.789473684,251.127819548872},
            {813752.631578947,189.473684210526},
            {402577.443609023,34929.3233082707},
            {634616.172932331,0},
            {651104.038172354,0},
            {667591.903412377,0},
            {684079.7686524,0},
            {700567.633892423,0}
            };

            final double[][] LOCATIONS = {
                { -36.83789, -60.22154 },
                { -36.8854011, -60.3117682 },
                { -37.2811, -59.36174 },
                { -36.9830847, -60.2786874 },
                { -37.3153825, -59.1358714 },
                { -37.310964, -59.9849222 },
                { -37.326989, -59.1347328 },
                { -36.93131, -60.1600396 },
                { -36.7818291, -59.8653433 },
                { -36.8902699, -60.3316318 },
                { -37.3087394, -59.1410418 },
                { -37.3052097, -59.1212296 },
                { -36.35624, -60.0239248 },
                { -36.8656099, -60.15751 },
                { -36.3777766, -59.5056733 },
                { -36.0213728, -60.0150616 },
                { -38.0330697, -60.0992996 },
                { -37.6745799, -59.8033973 },
                { -37.96412, -60.41475 },
                { -36.8917009, -60.3213956 },
                { -36.7740099, -59.08785 },
                { -36.777105, -59.863055 },
                { -36.2313297, -61.1130004 },
                { -37.5443336, -60.799877 },
                { -37.248281, -61.2612062 },
                { -37.1512197, -58.4878396 },
                { -36.01598, -59.09886 },
                { -35.6388321, -59.7805788 },
                { -36.7704241, -59.8596 },
                { -37.20978, -59.07611 }
        };

        final List<Location> locationList = new ArrayList<>();
        locationList.add(Location.newInstance(DEPOT_LATITUDE, DEPOT_LONGITUDE));
        for (double[] loc : LOCATIONS) {
            locationList.add(Location.newInstance(loc[0], loc[1]));
        }

        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .addCapacityDimension(WEIGHT_INDEX, WEIGHT_VALUE);
        VehicleType vehicleType = vehicleTypeBuilder.build();


        List<Pickup> pickups = new ArrayList<>();
        List<Delivery> deliveries = new ArrayList<>();

        for (Integer i = 0; i < PICKUPANDDELIVERIES.length; i++) {

            Pickup pickup = Pickup.Builder.newInstance("pickup"+i.toString()).addSizeDimension(0, PICKUPANDDELIVERYAMOUNTS[i][0]).setLocation(locationList.get(i)).build();
            pickups.add(pickup);
            Delivery delivery = Delivery.Builder.newInstance("delivery"+i.toString()).addSizeDimension(0, PICKUPANDDELIVERYAMOUNTS[i][1]).setLocation(locationList.get(i)).build();
            deliveries.add(delivery);
            
        }

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        List<VehicleImpl> vehicles = new ArrayList<>();

        for (int i = 0; i < VEHICLE_COUNT; i++) {

            Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle" + i);
            vehicleBuilder.setStartLocation(locationList.get(0));
            vehicleBuilder.setType(vehicleType);
            VehicleImpl vehicle = vehicleBuilder.build();
            vehicles.add(vehicle);
        }
        vrpBuilder.addAllVehicles(vehicles);

        List<Service> services = new ArrayList<>();
        for (int i = 0; i < CP_COUNT; i++) {
            Service service = Service.Builder.newInstance(String.valueOf(i + 1)).addSizeDimension(WEIGHT_INDEX, 1)
                    .setLocation(locationList.get(i + 1)).addTimeWindow(60, 360).setServiceTime(30).build();
            services.add(service);
        }
        vrpBuilder.addAllJobs(services);
        VehicleRoutingProblem problem = vrpBuilder.build();


        StateManager stateManager = new StateManager(problem);
        ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
        constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(), ConstraintManager.Priority.CRITICAL);

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).setStateAndConstraintManager(stateManager,constraintManager)
            .buildAlgorithm();

		/*
         * and search a solution
		 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
         * get the best
		 */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(bestSolution);

		/*
         * plot
		 */
        Plotter plotter = new Plotter(problem, bestSolution);
        plotter.setLabel(Label.SIZE);
        plotter.plot("output/solution.png", "solution");

    }

    
}
