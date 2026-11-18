package ie.atu.sample.controller;

import ie.atu.sample.dto.BookingRequest;
import ie.atu.sample.dto.BookingResponse;
import ie.atu.sample.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        return service.createBooking(request);
    }

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public List<BookingResponse> searchByRoomName(
            @RequestParam("roomName") String roomName) {
        return service.searchByRoomName(roomName);
    }
}