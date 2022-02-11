package com.example.jaspreet.demo;

import com.example.jaspreet.demo.Exception.CorruptedFieldDataException;
import com.example.jaspreet.demo.Exception.EmptyMandatoryFieldException;
import com.example.jaspreet.demo.Exception.IncorrectHeadersException;
import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.DataMatrix;
import com.example.jaspreet.demo.model.Parameters;
import com.example.jaspreet.demo.service.DataParser;
import com.example.jaspreet.demo.service.VRPSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

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

	@PostConstruct
	public void init() throws IOException {
		Logger logger = Logger.getLogger("");

		List<Cashpoint> cashpoints;
		try {
			cashpoints = dataParser.getData();
			Parameters param;
			param = dataParser.getParameters();
			DataMatrix dm = new DataMatrix();
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
		}
	}

	public static void main(String[] args) {
		// SpringApplication.run(DemoApplication.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(DemoApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
	}

}
