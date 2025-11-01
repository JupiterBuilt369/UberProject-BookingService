package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.api.LocationServiceApi;
import com.example.uberprojectbookingservice.dto.*;
import com.example.uberprojectbookingservice.repositories.BookingRepository;
import com.example.uberprojectbookingservice.repositories.DriverRepository;
import com.example.uberprojectbookingservice.repositories.PassengerRepository;
import org.example.uberprojectentityservice.model.Booking;
import org.example.uberprojectentityservice.model.BookingStatus;
import org.example.uberprojectentityservice.model.Driver;
import org.example.uberprojectentityservice.model.Passenger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    public final RestTemplate restTemplate;
    private final DriverRepository driverRepository;
    public LocationServiceApi locationServiceApi;

//    public static final String LOCATION_SERVICE_URL = "http://localhost:8081/api/location/";


    public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository, LocationServiceApi locationServiceApi, DriverRepository driverRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.locationServiceApi = locationServiceApi;
        this.restTemplate = new RestTemplate();
        this.driverRepository = driverRepository;
    }


    @Override
    public CreateBookingResponseDto CreateBooking(CreateBookingDto bookingDetails){
        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());
        Booking booking = Booking
                .builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger.get())
                .build();
        Booking newBooking = bookingRepository.save(booking);

        NearbyDriverRequestDto requestDto = NearbyDriverRequestDto
                .builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        /*
        ResponseEntity<DriverLocationDto[]> response =
                restTemplate.postForEntity(LOCATION_SERVICE_URL + "/nearby/drivers",requestDto , DriverLocationDto[].class);

        if(response.getStatusCode().is2xxSuccessful()){
            assert response.getBody() != null;
            DriverLocationDto[] nearbyDrivers = response.getBody();
            for (DriverLocationDto driverLocationDto : nearbyDrivers) {
                System.out.println(driverLocationDto.getDriverId() + " : " + "longitude, latitude : " + driverLocationDto.getLongitude() + " , " + driverLocationDto.getLatitude());
            }
        }
        */

        processNearbyDriversAsync(requestDto);

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
//                .driver(Optional.of(newBooking.getDriver()))
                .build();
    }

    @Override
    public UpdateBookingResponseDto UpdateBooking(UpdateBookingRequestDto requestDto, Long bookingId) {
        Optional<Driver> driver =  driverRepository.findById(requestDto.getDriverId());
             bookingRepository.updateBookingAndDriverById(bookingId, BookingStatus.SCHEDULED,driver.get());
             Optional<Booking> booking = bookingRepository.findById(bookingId);
             return UpdateBookingResponseDto
                     .builder()
                     .status(booking.get().getBookingStatus ())
                     .driver(Optional.of(booking.get().getDriver()))
                     .BookingId(bookingId)
                     .build();

    }

    @Async
    public void processNearbyDriversAsync(NearbyDriverRequestDto bookingDetails){
        Call<DriverLocationDto[]> call = locationServiceApi.getNearbyDrivers(bookingDetails);
        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(@NotNull Call<DriverLocationDto[]> call, @NotNull Response<DriverLocationDto[]> response) {
                if(response.isSuccessful() &&  response.body() != null){
                    DriverLocationDto[] nearbyDrivers = response.body();
                    for (DriverLocationDto driverLocationDto : nearbyDrivers) {
                        System.out.println(driverLocationDto.getDriverId() + " : " + "longitude, latitude : " + driverLocationDto.getLongitude() + " , " + driverLocationDto.getLatitude());
                    }

                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

    }
}
