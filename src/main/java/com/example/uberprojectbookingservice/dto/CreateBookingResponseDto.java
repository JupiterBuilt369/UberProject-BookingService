package com.example.uberprojectbookingservice.dto;

import lombok.*;
import org.example.uberprojectentityservice.model.Driver;

import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBookingResponseDto {
    private Long bookingId;

    private String bookingStatus;

    private Optional<Driver> driver;
}
