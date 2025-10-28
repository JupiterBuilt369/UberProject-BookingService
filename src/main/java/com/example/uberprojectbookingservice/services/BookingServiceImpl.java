package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dto.CreateBookingDto;
import com.example.uberprojectbookingservice.dto.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dto.DriverLocationDto;
import com.example.uberprojectbookingservice.dto.NearbyDriverRequestDto;
import com.example.uberprojectbookingservice.repositories.BookingRepository;
import com.example.uberprojectbookingservice.repositories.PassengerRepository;
import org.example.uberprojectentityservice.model.Booking;
import org.example.uberprojectentityservice.model.BookingStatus;
import org.example.uberprojectentityservice.model.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    public final RestTemplate restTemplate;
    public static final String LOCATION_SERVICE_URL = "http://localhost:8081/api/location/";


    public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.restTemplate = new RestTemplate();
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

        ResponseEntity<DriverLocationDto[]> response =
                restTemplate.postForEntity(LOCATION_SERVICE_URL + "/nearby/drivers",requestDto , DriverLocationDto[].class);

        if(response.getStatusCode().is2xxSuccessful()){
            assert response.getBody() != null;
            DriverLocationDto[] nearbyDrivers = response.getBody();
            for (DriverLocationDto driverLocationDto : nearbyDrivers) {
                System.out.println(driverLocationDto.getDriverId() + " : " + "longitude, latitude : " + driverLocationDto.getLongitude() + " , " + driverLocationDto.getLatitude());
            }
        }

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
//                .driver(Optional.of(newBooking.getDriver()))
                .build();
    }
}
