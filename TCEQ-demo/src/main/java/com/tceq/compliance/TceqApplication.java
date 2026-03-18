package com.tceq.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TCEQ Environmental Compliance Tracking System.
 *
 * <p>Spring Boot entry point for the compliance dashboard that tracks
 * facilities, permits, inspections, and violations across the state of Texas.
 */
@SpringBootApplication
public class TceqApplication {

    public static void main(String[] args) {
        SpringApplication.run(TceqApplication.class, args);
    }
}
