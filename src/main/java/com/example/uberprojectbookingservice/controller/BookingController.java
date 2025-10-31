package com.example.uberprojectbookingservice.controller;

import com.example.uberprojectbookingservice.dto.CreateBookingDto;
import com.example.uberprojectbookingservice.dto.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dto.UpdateBookingRequestDto;
import com.example.uberprojectbookingservice.dto.UpdateBookingResponseDto;
import com.example.uberprojectbookingservice.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingDto createBookingDto) {
        return new ResponseEntity<>( bookingService.CreateBooking(createBookingDto) , HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingRequestDto requestDto, @PathVariable Long bookingId ) {
        return new ResponseEntity<>( bookingService.UpdateBooking(requestDto, bookingId) , HttpStatus.CREATED);

    }


}
