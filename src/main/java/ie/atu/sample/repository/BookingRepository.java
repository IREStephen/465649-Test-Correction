package ie.atu.sample.repository;

import ie.atu.sample.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT b
            FROM Booking b
            JOIN b.room r
            WHERE r.name = :roomName
            ORDER BY b.attendeeCount DESC, b.id ASC
            """)
    List<Booking> findByRoomName(
            @Param("roomName") String roomName);
}

