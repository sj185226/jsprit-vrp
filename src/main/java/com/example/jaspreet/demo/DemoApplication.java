package com.example.jaspreet.demo;

import com.example.jaspreet.demo.model.Cashpoint;
import com.example.jaspreet.demo.model.Parameters;
import com.example.jaspreet.demo.service.DataParser;
import com.example.jaspreet.demo.service.VRPSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {


	@Autowired
	static DataParser dataParser;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("hey.....");



		List<Cashpoint> cashpoints = dataParser.getData();
		Parameters param = dataParser.getParameters();
		VRPSolver vrpsolver = new VRPSolver();
		vrpsolver.solve(cashpoints, param, dataMatrix);
	}

}
