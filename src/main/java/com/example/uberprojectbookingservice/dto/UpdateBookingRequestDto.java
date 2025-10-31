package com.example.uberprojectbookingservice.dto;

import lombok.*;
import org.example.uberprojectentityservice.model.BookingStatus;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequestDto {
    private String bookingStatus;
    private Long driverId;
}
