package com.example.jaspreet.demo.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.example.jaspreet.demo.Exception.CorruptedFieldDataException;
import com.example.jaspreet.demo.Exception.EmptyMandatoryFieldException;
import com.example.jaspreet.demo.Exception.IncorrectHeadersException;
import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.Parameters;
import com.graphhopper.jsprit.core.problem.Location;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataParser {
    @Value("${PARAMS_FILE}")
    private String parameterFile;

    @Value("${DATA_FILE}")
    private String dataFile;

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
        if (paramHeaders.length != 9) {
            throw new IncorrectHeadersException("Property");
        }
        dataHeaders = dataHeadersStr.split(",");
        if (paramHeaders.length != 7) {
            throw new IncorrectHeadersException("Property");
        }
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

        double vehicleCapacity = 0;
        int numberOfVehicles = 0;
        LocalTime vehicleStartTime = LocalTime.of(9, 0);
        boolean hardTimeWindow = false;
        boolean backhaulRequired = false;
        double fixedCostPerTrip = 0;
        double costPerUnitDistance = 1;
        double costPerUnitTime = 0;
        String originAddress = "";
        Location originLocation = null;

        for (int i = 0; i < paramHeaders.length; i++) {
            if (line[i] == null || "".equals(line[i].trim())) {
                if (i == 0 || i == 1 || i == 7) {
                    br.close();
                    throw new EmptyMandatoryFieldException("Parameters", i + 1);
                }
                continue;
            }

            try {

                switch (i) {
                    case 0:
                        vehicleCapacity = Double.parseDouble(line[i].trim());
                        break;
                    case 1:
                        numberOfVehicles = Integer.parseInt(line[i].trim());
                        break;
                    case 2:
                        String[] hhmm = line[i].split(":");
                        vehicleStartTime = LocalTime.of(Integer.parseInt(hhmm[0]), Integer.parseInt(hhmm[1]));
                        break;
                    case 3:
                        hardTimeWindow = yesSet.contains(line[i].trim().toLowerCase()) ? true : false;
                        break;
                    case 4:
                        backhaulRequired = yesSet.contains(line[i].trim().toLowerCase()) ? true : false;
                        break;
                    case 5:
                        fixedCostPerTrip = Double.parseDouble(line[i].trim());
                        break;
                    case 6:
                        costPerUnitDistance = Double.parseDouble(line[i].trim());
                        break;
                    case 7:
                        costPerUnitTime = Double.parseDouble(line[i].trim());
                        break;
                    default:
                        originLocation = getLocation(line[i].trim()); // may return null
                        if (originLocation == null)
                            originAddress = line[i].trim();
                }
            } catch (Exception ex) {
                br.close();
                throw new CorruptedFieldDataException("Parameters", i + 1);
            }
        }

        br.close();

        if (originLocation == null) {
            return new Parameters(vehicleCapacity, numberOfVehicles, hardTimeWindow, backhaulRequired, fixedCostPerTrip,
                    costPerUnitDistance, costPerUnitTime, vehicleStartTime, originAddress);
        }
        return new Parameters(vehicleCapacity, numberOfVehicles, hardTimeWindow, backhaulRequired, fixedCostPerTrip,
                costPerUnitDistance, costPerUnitTime, vehicleStartTime, originLocation);

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
                Location location = getLocation(vals[1].trim());
                String address = "";
                if (location == null)
                    address = vals[1].trim();
                LocalTime windowStartTime = LocalTime.of(10, 0);
                if (vals.length >= 3 && vals[2] != null && !"".equals(vals[2].trim())) {
                    String[] hhmm = vals[2].trim().split(":");
                    windowStartTime = LocalTime.of(Integer.parseInt(hhmm[0]), Integer.parseInt(hhmm[1]));
                }
                LocalTime windowEndTime = LocalTime.of(22, 0);
                if (vals.length >= 4 && vals[3] != null && !"".equals(vals[3].trim())) {
                    String[] hhmm = vals[3].trim().split(":");
                    windowEndTime = LocalTime.of(Integer.parseInt(hhmm[0]), Integer.parseInt(hhmm[1]));
                }
                int serviceTime = 10;
                if (vals.length >= 5 && vals[4] != null && !"".equals(vals[4].trim())) {
                    serviceTime = Integer.parseInt(vals[4].trim());
                }
                double pickupAmount = 0;
                if (vals.length >= 6 && vals[5] != null && !"".equals(vals[5].trim())) {
                    pickupAmount = Double.parseDouble(vals[5].trim());
                }
                double deliveryAmount = 0;
                if (vals.length >= 7 && vals[6] != null && !"".equals(vals[6].trim())) {
                    deliveryAmount = Double.parseDouble(vals[6].trim());
                }

                Cashpoint cp;
                if (location == null) {
                    cp = new Cashpoint(name, address, windowStartTime, windowEndTime, serviceTime, pickupAmount,
                            deliveryAmount);
                } else {
                    cp = new Cashpoint(name, location, windowStartTime, windowEndTime, serviceTime, pickupAmount,
                            deliveryAmount);
                }

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
        if (cashpoint.getLocation() == null && (cashpoint.getAddress() == null || "".equals(cashpoint.getAddress())))
            return false;
        if (cashpoint.getPickupAmount() == 0 && cashpoint.getDeliveryAmount() == 0)
            return false;
        return true;
    }

    private Location getLocation(String locStr) {
        String[] latlong = locStr.split(",");
        if (latlong.length != 2) {
            return null;
        }

        try {
            return Location.newInstance(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
        } catch (NumberFormatException nfe) {
            return null;
        }
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

}
