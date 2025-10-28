package com.example.uberprojectbookingservice.repositories;

import org.example.uberprojectentityservice.model.Booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
