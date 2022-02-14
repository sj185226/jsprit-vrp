package com.example.jaspreet.demo;

import com.example.jaspreet.demo.Exception.CorruptedFieldDataException;
import com.example.jaspreet.demo.Exception.EmptyMandatoryFieldException;
import com.example.jaspreet.demo.Exception.IncorrectHeadersException;
import com.example.jaspreet.demo.Exception.MapApiException;
import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.DataMatrix;
import com.example.jaspreet.demo.model.Parameters;
import com.example.jaspreet.demo.service.BingService;
import com.example.jaspreet.demo.service.DataParser;
import com.example.jaspreet.demo.service.VRPSolver;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {


	@Autowired
	private DataParser dataParser;

	@Autowired
	private VRPSolver vrpSolver;

	@Autowired
	private BingService bingService;

	@PostConstruct
	public void init() throws IOException {
		Logger logger = Logger.getLogger("");

		try {
			List<Cashpoint> cashpoints = dataParser.getData();
			Parameters param = dataParser.getParameters();

			// System.out.println(bingService.getUrl(cashpoints, param));
			DataMatrix dm = bingService.getCostMatrix(cashpoints, param);
			boolean solution = vrpSolver.solve(cashpoints, param, dm);
			if (solution) {
				logger.info("Solution has been created in the outpur file");
			} else {
				logger.severe("Solution is not feasible.\nTry relaxing some constraints");
			}
		} catch (IncorrectHeadersException e) {
			logger.severe(e.getMessage());
		} catch (EmptyMandatoryFieldException e) {
			logger.severe(e.getMessage());
		} catch (CorruptedFieldDataException e) {
			logger.severe(e.getMessage());
		} catch (MapApiException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// SpringApplication.run(DemoApplication.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(DemoApplication.class);
		builder.headless(false);
		builder.run(args);
	}

}
