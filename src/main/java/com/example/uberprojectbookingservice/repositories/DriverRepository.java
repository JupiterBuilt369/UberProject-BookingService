package com.example.uberprojectbookingservice.repositories;

import org.example.uberprojectentityservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {


}