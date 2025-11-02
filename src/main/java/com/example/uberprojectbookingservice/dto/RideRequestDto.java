package com.example.uberprojectbookingservice.dto;

//import com.example.uberprojectclientsocketservice.model.ExactLocation;
import com.example.uberprojectbookingservice.model.ExactLocation;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDto {
    private Long passengerId;
//
//    private ExactLocation startLocation;
//
//    private ExactLocation endLocation;

    private List<Long> driverID;

    private Long bookingId;

}
