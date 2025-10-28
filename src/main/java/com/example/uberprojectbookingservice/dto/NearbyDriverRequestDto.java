package com.example.uberprojectbookingservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyDriverRequestDto {
     Double latitude;
     Double longitude;
}
