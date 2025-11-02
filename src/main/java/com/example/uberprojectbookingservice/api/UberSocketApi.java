package com.example.uberprojectbookingservice.api;

import com.example.uberprojectbookingservice.dto.DriverLocationDto;
import com.example.uberprojectbookingservice.dto.NearbyDriverRequestDto;
import com.example.uberprojectbookingservice.dto.RideRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UberSocketApi {

    @POST("/api/socket/newRide")
    Call<Boolean> raiseRideRequest(@Body RideRequestDto rideRequestDto) ;
}
