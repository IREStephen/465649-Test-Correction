package ie.atu.sample.service;

import ie.atu.sample.dto.BookingRequest;
import ie.atu.sample.dto.BookingResponse;
import ie.atu.sample.model.Booking;
import ie.atu.sample.model.BookingStatus;
import ie.atu.sample.model.Room;
import ie.atu.sample.repository.BookingRepository;
import ie.atu.sample.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository repository,
                          RoomRepository roomRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
    }

    public BookingResponse createBooking(BookingRequest request) {

        if (request.roomId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room Id is required");
        }

        Room room = roomRepository
                .findById(request.roomId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found with id " + request.roomId()));

        if (request.attendeeCount() > room.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "attendeeCount " + request.attendeeCount()
                            + " exceeds the capacity of " + room.getName()
                            + " (" + room.getCapacity() + ")");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setOrganiserName(request.organiserName());
        booking.setAttendeeCount(request.attendeeCount());
        booking.setStatus(BookingStatus.ACTIVE);

        return toResponse(repository.save(booking));
    }

    public BookingResponse getById(Long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found with id " + id));
        return toResponse(booking);
    }

    public List<BookingResponse> searchByRoomName(String roomName) {
        return repository.findByRoomName(roomName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Entity -> response DTO (never return the JPA entity directly)
    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getOrganiserName(),
                booking.getAttendeeCount(),
                booking.getStatus());
    }
}