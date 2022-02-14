package com.example.jaspreet.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import com.example.jaspreet.demo.Exception.MapApiException;
import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.DataMatrix;
import com.example.jaspreet.demo.model.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BingService {

    @Value("${API_URL}")
    private String apiUrl;

    @Value("${BING_API_KEY}")
    private String apiKey;

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private String getUrl(List<Cashpoint> cashpoints, Parameters param) {
        String key = "key=" + apiKey;
        String origins = "origins=" + param.getOriginlocation().getCoordinate().getX() + ","
                + param.getOriginlocation().getCoordinate().getY();

        for (Cashpoint cp : cashpoints) {
            origins += ";" + cp.getLocation().getCoordinate().getX() + ","
                    + +cp.getLocation().getCoordinate().getY();
        }

        String startTime = "startTime=" + param.getVehicleStartTime().toString();

        return String.join("&", apiUrl, key, origins, startTime);

    }

    public DataMatrix getCostMatrix(List<Cashpoint> cashpoints, Parameters param)
            throws IOException, MapApiException, JSONException {
        JSONObject json = readJsonFromUrl(getUrl(cashpoints, param));
        int responseCode = json.getInt("statusCode");

        if (responseCode < 200 || responseCode >= 400) {
            throw new MapApiException(responseCode, json.getString("authenticationResultCode"),
                    json.getJSONArray("errorDetails").getString(0));
        }

        json = json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0);

        JSONArray resultList = json.getJSONArray("results");

        return getDataMatrix(resultList, 31);
    }

    private DataMatrix getDataMatrix(JSONArray resultList, int n) {
        double[][] distanceMatrix = new double[n][n];
        double[][] timeMatrix = new double[n][n];

        for (int i = 0; i < resultList.length(); i++) {
            JSONObject json = resultList.getJSONObject(i);
            int j = json.getInt("originIndex");
            int k = json.getInt("destinationIndex");
            distanceMatrix[j][k] = json.getDouble("travelDistance");
            timeMatrix[j][k] = json.getDouble("travelDuration");
        }

        return new DataMatrix(distanceMatrix, timeMatrix);
    }
}
