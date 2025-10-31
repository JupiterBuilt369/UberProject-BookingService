package com.example.uberprojectbookingservice.dto;

import lombok.*;
import org.example.uberprojectentityservice.model.BookingStatus;
import org.example.uberprojectentityservice.model.Driver;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingResponseDto {

    private Long  BookingId;
    private BookingStatus status;
    private Optional<Driver> driver;
}
