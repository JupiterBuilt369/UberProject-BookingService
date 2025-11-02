package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.api.LocationServiceApi;
import com.example.uberprojectbookingservice.api.UberSocketApi;
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

import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    public final RestTemplate restTemplate;
    private final DriverRepository driverRepository;
    private final LocationServiceApi locationServiceApi;
    private final UberSocketApi uberSocketApi;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              PassengerRepository passengerRepository,
                              UberSocketApi uberSocketApi,
                              LocationServiceApi locationServiceApi,
                              DriverRepository driverRepository) {

        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.locationServiceApi = locationServiceApi;
        this.restTemplate = new RestTemplate();
        this.driverRepository = driverRepository;
        this.uberSocketApi = uberSocketApi;
    }

    // ============================================================
    // 1️ CREATE BOOKING REQUEST (from passenger)
    // ============================================================
    @Override
    public CreateBookingResponseDto CreateBooking(CreateBookingDto bookingDetails) {

        // Step 1: Fetch passenger details using passengerId
        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());

        // Step 2: Create a new booking with status = ASSIGNING_DRIVER
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger.get())
                .build();

        // Step 3: Save booking in DB
        Booking newBooking = bookingRepository.save(booking);

        // Step 4: Prepare a request to find nearby drivers
        NearbyDriverRequestDto requestDto = NearbyDriverRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        // Step 5: Process driver search asynchronously
        processNearbyDriversAsync(requestDto, bookingDetails.getPassengerId(), newBooking.getId());

        // Step 6: Return booking confirmation response to passenger
        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }

    // ============================================================
    // 2️ ASYNC PROCESS: FIND NEARBY DRIVERS (via Location Service)
    // ============================================================
    @Async
    public void processNearbyDriversAsync(NearbyDriverRequestDto bookingDetails, Long passengerId, Long bookingId) {

        System.out.println("call coming");
        // Step 1: Call location microservice to get nearby driver coordinates
        Call<DriverLocationDto[]> call = locationServiceApi.getNearbyDrivers(bookingDetails);
        System.out.println("call coming after");

        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(@NotNull Call<DriverLocationDto[]> call, @NotNull Response<DriverLocationDto[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DriverLocationDto[] nearbyDrivers = response.body();


                    // Step 2: Print all found nearby drivers (for debug/logging)
                    for (DriverLocationDto driverLocationDto : nearbyDrivers) {
                        System.out.println(driverLocationDto.getDriverId() + " : " +
                                "longitude, latitude : " +
                                driverLocationDto.getLongitude() + " , " +
                                driverLocationDto.getLatitude());
                    }

                    // Step 3: Notify socket service to raise ride request for available drivers
                    raiseRideRequestAsync(RideRequestDto.builder()
                            .passengerId(passengerId)
                            .bookingId(bookingId)
                            .build());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                // Step 4: Log if location service API fails
                System.out.println(throwable.getMessage());
            }
        });
    }

    // ============================================================
    // 3 RAISE RIDE REQUEST (Notify drivers via WebSocket API)
    // ============================================================
    @Async
    public void raiseRideRequestAsync(RideRequestDto rideRequestDto) {

        // Step 1: Call UberSocketApi to broadcast ride request to available drivers
        Call<Boolean> call = uberSocketApi.raiseRideRequest(rideRequestDto);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = response.body();

                    // Step 2: Log driver acceptance or response
                    System.out.println("Driver response : " + success);
                } else {
                    // Step 3: Log if API call failed
                    System.out.println("request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                // Step 4: Log socket API failure
                System.out.println(throwable.getMessage());
            }
        });
    }

    // ============================================================
    // 4️⃣ UPDATE BOOKING (After driver accepts ride)
    // ============================================================
    @Override
    public UpdateBookingResponseDto UpdateBooking(UpdateBookingRequestDto requestDto, Long bookingId) {

        // Step 1: Fetch driver from driverId
        Optional<Driver> driver = driverRepository.findById(requestDto.getDriverId());

        // Step 2: Update booking with accepted driver & change status to SCHEDULED
        bookingRepository.updateBookingAndDriverById(bookingId, BookingStatus.SCHEDULED, driver.get());

        // Step 3: Fetch updated booking
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        // Step 4: Return updated booking details
        return UpdateBookingResponseDto.builder()
                .status(booking.get().getBookingStatus())
                .driver(Optional.of(booking.get().getDriver()))
                .BookingId(bookingId)
                .build();
    }
}
