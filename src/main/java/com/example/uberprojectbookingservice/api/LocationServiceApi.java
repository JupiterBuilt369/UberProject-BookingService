package com.example.uberprojectbookingservice.api;

import com.example.uberprojectbookingservice.dto.DriverLocationDto;
import com.example.uberprojectbookingservice.dto.NearbyDriverRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceApi {
    public static final String LOCATION_SERVICE_URL = "http://localhost:8081/api/location/";

    @POST(LOCATION_SERVICE_URL + "/nearby/drivers")
    Call<DriverLocationDto[]> getNearbyDrivers(@Body NearbyDriverRequestDto requestDto);
}
