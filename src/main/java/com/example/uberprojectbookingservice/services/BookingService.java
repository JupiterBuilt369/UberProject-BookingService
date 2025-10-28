package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dto.CreateBookingDto;
import com.example.uberprojectbookingservice.dto.CreateBookingResponseDto;

public interface BookingService {
    CreateBookingResponseDto CreateBooking(CreateBookingDto booking);
}
