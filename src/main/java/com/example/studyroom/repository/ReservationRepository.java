package com.example.studyroom.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.Room;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	boolean existsByRoomAndEndAtAfterAndStartAtBefore(Room room, LocalDateTime startAt, LocalDateTime endAt);
}
