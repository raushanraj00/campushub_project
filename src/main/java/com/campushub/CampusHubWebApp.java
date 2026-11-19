package com.campushub;

import com.campushub.dao.AcademicDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CampusHubWebApp {

    public static void main(String[] args) {
        // Bootstrap academic data before server starts
        AcademicDAO.bootstrapAcademicData();
        SpringApplication.run(CampusHubWebApp.class, args);

        System.out.println("=================================================");
        System.out.println("  CampusHub ERP - Web Server Ready!");
        System.out.println("  Open Chrome: http://localhost:8080");
        System.out.println("  Admin:   admin / Admin@123");
        System.out.println("  Faculty: fac_cse_01 / Faculty@123");
        System.out.println("  Student: 25bcs0001 / Student@123");
        System.out.println("=================================================");
    }
}

