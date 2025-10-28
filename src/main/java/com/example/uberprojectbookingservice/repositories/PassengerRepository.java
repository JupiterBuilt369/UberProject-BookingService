package com.example.uberprojectbookingservice.repositories;

import org.example.uberprojectentityservice.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
