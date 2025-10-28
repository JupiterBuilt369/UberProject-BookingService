package com.example.uberprojectbookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "org.example.uberprojectentityservice.model")
@EnableJpaRepositories(basePackages = "com.example.uberprojectbookingservice.repositories")
@ComponentScan(basePackages = {
        "com.example.uberprojectbookingservice",
        "org.example.uberprojectentityservice"
})
public class UberProjectBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UberProjectBookingServiceApplication.class, args);
        System.out.println("<--------------JAI-SHREE-RAM-------------->");
    }



}
