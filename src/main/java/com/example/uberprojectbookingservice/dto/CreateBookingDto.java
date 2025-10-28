package com.example.uberprojectbookingservice.dto;

import lombok.*;
import org.example.uberprojectentityservice.model.ExactLocation;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBookingDto {
    private Long passengerId;

    private ExactLocation startLocation;

    private ExactLocation endLocation;
}
