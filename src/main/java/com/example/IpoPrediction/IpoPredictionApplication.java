package com.example.IpoPrediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IpoPredictionApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpoPredictionApplication.class, args);
	}

}
