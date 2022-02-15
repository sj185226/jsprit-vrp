package com.example.jaspreet.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.example.jaspreet.demo.Exception.CorruptedFieldDataException;
import com.example.jaspreet.demo.Exception.EmptyMandatoryFieldException;
import com.example.jaspreet.demo.Exception.IncorrectHeadersException;
import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.Parameters;
import com.example.jaspreet.demo.model.Parameters.ParametersBuilder;
import com.graphhopper.jsprit.core.problem.Location;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataParser {
    @Value("${PARAMS_FILE}")
    private String parameterFile;

    @Value("${DATA_FILE}")
    private String dataFile;

    @Value("${RESULT_FILE}")
    private String outputFile;

    @Value("${PARAM_HEADERS}")
    private String paramHeadersStr;

    @Value("${DATA_HEADERS}")
    private String dataHeadersStr;

    private String[] paramHeaders;
    private String[] dataHeaders;

    private HashSet<String> yesSet = new HashSet<>(
            Arrays.asList(new String[] { "yes", "y", "t", "true", "yeah", "1", "si" }));

    private static Logger logger = Logger.getLogger("Data Parser");

    @PostConstruct
    public void init() throws IncorrectHeadersException {
        paramHeaders = paramHeadersStr.split(",");
        if (paramHeaders.length != 10) {
            throw new IncorrectHeadersException("Property");
        }
        dataHeaders = dataHeadersStr.split(",");
        if (dataHeaders.length != 8) {
            throw new IncorrectHeadersException("Property");
        }
    }

    private boolean isEmptyField(String field) {
        return field == null || "".equals(field.trim());
    }

    public Parameters getParameters()
            throws IOException, IncorrectHeadersException, EmptyMandatoryFieldException, CorruptedFieldDataException {
        BufferedReader br = new BufferedReader(new FileReader(parameterFile));
        String headerLine = br.readLine();
        validateHeaders(paramHeaders, headerLine.split(","));

        String[] line = br.readLine().split(",");
        if (line.length != paramHeaders.length) {
            br.close();
            throw new IncorrectHeadersException("File");
        }

        ParametersBuilder paramBuilder;

        if (isEmptyField(line[0])) {
            br.close();
            throw new EmptyMandatoryFieldException("Parameters", 1);
        }
        if (isEmptyField(line[1])) {
            br.close();
            throw new EmptyMandatoryFieldException("Parameters", 2);
        }
        if (isEmptyField(line[8])) {
            br.close();
            throw new EmptyMandatoryFieldException("Parameters", 8);
        }
        if (isEmptyField(line[9])) {
            br.close();
            throw new EmptyMandatoryFieldException("Parameters", 9);
        }

        int processingField = 1;

        try {
            double vehicleCapacity = Double.parseDouble(line[0].trim());
            processingField++;
            int numberOfVehicles = Integer.parseInt(line[1].trim());
            processingField = 9;
            double latitude = Double.parseDouble(line[8].trim());
            processingField++;
            double longitude = Double.parseDouble(line[9].trim());
            paramBuilder = new ParametersBuilder(vehicleCapacity, numberOfVehicles,
                    Location.newInstance(latitude, longitude));
            processingField = 3;
            if (!isEmptyField(line[2])) {
                String[] hhmm = line[2].split(":");
                paramBuilder.setVehicleStartTime(LocalTime.of(Integer.parseInt(hhmm[0]), Integer.parseInt(hhmm[1])));
            }
            processingField++;
            if (!isEmptyField(line[3])) {
                paramBuilder.setHardTimeWindow(yesSet.contains(line[3].trim().toLowerCase()) ? true : false);
            }
            processingField++;
            if (!isEmptyField(line[4])) {
                paramBuilder.setBackhaulRequired(yesSet.contains(line[4].trim().toLowerCase()) ? true : false);
            }
            processingField++;
            if (!isEmptyField(line[5])) {
                paramBuilder.setFixedCostPerTrip(Double.parseDouble(line[5].trim()));
            }
            processingField++;
            if (!isEmptyField(line[6])) {
                paramBuilder.setCostPerUnitDistance(Double.parseDouble(line[6].trim()));
            }
            processingField++;
            if (!isEmptyField(line[7])) {
                paramBuilder.setCostPerUnitTime(Double.parseDouble(line[7].trim()));
            }
        } catch (Exception ex) {
            throw new CorruptedFieldDataException("Parameters", processingField);
        } finally {
            br.close();
        }

        return paramBuilder.build();

    }

    public List<Cashpoint> getData() throws IOException, IncorrectHeadersException, CorruptedFieldDataException {
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        String headerLine = br.readLine();
        if (!validateHeaders(dataHeaders, headerLine.split(","))) {
            br.close();
            throw new IncorrectHeadersException("Data");
        }

        List<Cashpoint> cashpoints = new ArrayList<Cashpoint>();
        String line;
        int lineNo = 0;
        while ((line = br.readLine()) != null) {
            lineNo++;
            try {
                String[] vals = line.split(",");

                String name = vals[0].trim();
                double latitude = Double.parseDouble(vals[1].trim());
                double longitude = Double.parseDouble(vals[2].trim());

                LocalTime windowStartTime = LocalTime.of(10, 0);
                if (vals.length >= 3 && vals[3] != null && !"".equals(vals[3].trim())) {
                    String[] hhmm = vals[3].trim().split(":");
                    windowStartTime = LocalTime.of(Integer.parseInt(hhmm[0]), Integer.parseInt(hhmm[1]));
                }
                LocalTime windowEndTime = LocalTime.of(22, 0);
                if (vals.length >= 4 && vals[4] != null && !"".equals(vals[4].trim())) {
                    String[] hhmm = vals[4].trim().split(":");
                    windowEndTime = LocalTime.of(Integer.parseInt(hhmm[0]), Integer.parseInt(hhmm[1]));
                }
                int serviceTime = 10;
                if (vals.length >= 5 && vals[5] != null && !"".equals(vals[5].trim())) {
                    serviceTime = Integer.parseInt(vals[5].trim());
                }
                double pickupAmount = 0;
                if (vals.length >= 6 && vals[6] != null && !"".equals(vals[6].trim())) {
                    pickupAmount = Double.parseDouble(vals[6].trim());
                }
                double deliveryAmount = 0;
                if (vals.length >= 7 && vals[7] != null && !"".equals(vals[7].trim())) {
                    deliveryAmount = Double.parseDouble(vals[7].trim());
                }

                Cashpoint cp = new Cashpoint(name, Location.newInstance(latitude, longitude), windowStartTime,
                        windowEndTime, serviceTime, pickupAmount, deliveryAmount);

                if (verifyCashpoint(cp)) {
                    cashpoints.add(cp);
                } else {
                    logger.warning("Invalid Cashpoint at index: " + lineNo + "cashpoint: " + name);
                }

            } catch (Exception ex) {
                br.close();
                throw new CorruptedFieldDataException("Data", lineNo);
            }

        }
        br.close();

        return cashpoints;

    }

    private boolean verifyCashpoint(Cashpoint cashpoint) {
        if (cashpoint.getName() == null || "".equals(cashpoint.getName()))
            return false;
        if (cashpoint.getPickupAmount() == 0 && cashpoint.getDeliveryAmount() == 0)
            return false;
        return true;
    }

    private boolean validateHeaders(String[] origHeaders, String[] parsedHeaders) {

        if (parsedHeaders.length == origHeaders.length) {
            HashSet<String> parsedSet = new HashSet<>();
            for (String str : parsedHeaders) {
                parsedSet.add(str.trim());
            }
            if (parsedSet.size() == origHeaders.length) {
                boolean fine = true;
                for (String header : origHeaders) {
                    if (!parsedSet.contains(header))
                        fine = false;
                }
                if (fine)
                    return true;
            }
        }

        return false;

    }

    public void print(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution, LocalTime startTime)
            throws FileNotFoundException {

        PrintWriter out = new PrintWriter(new File(outputFile));

        out.printf("Problem %n");
        out.printf("Parameter,Value %n");
        out.printf("No of Jobs," + problem.getJobs().values().size() + "%n");
        out.printf( "fleetSize,"+ problem.getFleetSize().toString()+"%n");



        out.printf("%nSolution%n");
        out.printf("Parameter,Value%n");
        out.printf( "Costs,"+ solution.getCost()+"%n");
        out.printf( "No. of Vehicles,"+ solution.getRoutes().size()+"%n");
        out.printf("Unassigned Jobs," + solution.getUnassignedJobs().size() + "%n");
        printVerbose(out, problem, solution, startTime);
        out.close();
    }

    private static void printVerbose(PrintWriter out, VehicleRoutingProblem problem,
            VehicleRoutingProblemSolution solution, LocalTime startTime) {
        out.printf("%nDetailed solution %n");
        out.printf("Route no,Vehicle,Activity,Location,ArrivalTime,EndTime,Costs%n");
        int routeNu = 1;

        List<VehicleRoute> list = new ArrayList<VehicleRoute>(solution.getRoutes());
        Collections.sort(list , new com.graphhopper.jsprit.core.util.VehicleIndexComparator());
        for (VehicleRoute route : list) {
            double costs = 0;
            out.printf(routeNu + "," + getVehicleString(route) + "," + route.getStart().getName() + ",Depot,-,"
                    + LocalTime.of(startTime.getHour(), startTime.getMinute())
                            .plusMinutes(Math.round(route.getStart().getEndTime())).toString()
                    +
                    "," + Math.round(costs*100)/100.0+"%n");
            TourActivity prevAct = route.getStart();
            for (TourActivity act : route.getActivities()) {
                String jobId;
                if (act instanceof TourActivity.JobActivity) {
                    jobId = ((TourActivity.JobActivity) act).getJob().getId();
                } else {
                    jobId = "-";
                }
                double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(), prevAct.getEndTime(), route.getDriver(),
                        route.getVehicle());
                c += problem.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(), route.getVehicle());
                costs += c;
                out.printf(
                        routeNu + "," + getVehicleString(route) + "," + act.getName().split("_")[0] + "," + jobId + ","
                                + LocalTime.of(startTime.getHour(), startTime.getMinute())
                                        .plusMinutes(Math.round(act.getArrTime())).toString()
                                + "," +
                                LocalTime.of(startTime.getHour(), startTime.getMinute())
                                        .plusMinutes(Math.round(act.getEndTime())).toString()
                                + "," + Math.round(costs * 100) / 100.0 + "%n");
                prevAct = act;
            }
            double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(), prevAct.getEndTime(),
                    route.getDriver(), route.getVehicle());
            c += problem.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(), route.getDriver(), route.getVehicle());
            costs += c;
            out.printf(routeNu + "," + getVehicleString(route) + "," + route.getEnd().getName() + ",Depot,"
                    + LocalTime.of(startTime.getHour(), startTime.getMinute())
                            .plusMinutes(Math.round(route.getEnd().getArrTime())).toString()
                    + ",-," +
                    Math.round(costs*100)/100.0+"%n%n");
            routeNu++;
        }
        if (!solution.getUnassignedJobs().isEmpty()) {
            out.printf("%nUnassigned Jobs%n");
            for (Job j : solution.getUnassignedJobs()) {
                out.printf(j.getId()+"%n");
            }

        }
    }

    private static String getVehicleString(VehicleRoute route) {
        return route.getVehicle().getId();
    }

}
