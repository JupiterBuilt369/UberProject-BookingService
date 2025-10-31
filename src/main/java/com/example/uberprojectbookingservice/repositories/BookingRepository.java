package com.example.uberprojectbookingservice.repositories;

import org.example.uberprojectentityservice.model.Booking;

import org.example.uberprojectentityservice.model.BookingStatus;
import org.example.uberprojectentityservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional
    @Modifying
    @Query("update Booking b SET b.bookingStatus = :status , b.driver = :driver where b.id = :id")
    void updateBookingAndDriverById(@Param("id") Long id, @Param("status") BookingStatus  status, @Param("driver") Driver driver);

}
