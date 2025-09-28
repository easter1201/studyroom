package com.example.studyroom.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.Room;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	boolean existsByRoomAndEndAtAfterAndStartAtBefore(Room room, LocalDateTime startAt, LocalDateTime endAt);

	List<Reservation> findByRoomAndStartAtBetweenOrderByStartAt(Room room, LocalDateTime startAt, LocalDateTime endAt);

	@Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
	"AND r.startAt < :endAt AND r.endAt > :startAt")
	List<Reservation> findOverlappingReservations(
		@Param("roomId") Long roomId,
		@Param("startAt") LocalDateTime startAt,
		@Param("endAt") LocalDateTime endAt
	);
}
