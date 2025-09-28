package com.example.studyroom.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.studyroom.dto.response.RoomAvailabilityResponse;
import com.example.studyroom.dto.response.TimeSlot;
import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.Room;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.RoomRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class RoomService {
	private final RoomRepository roomRepository;
	private final ReservationRepository reservationRepository;

	public Room createRoom(Room room) {
		return roomRepository.save(room);
	}

	@Transactional(readOnly = true)
	public List<RoomAvailabilityResponse> getRoomAvailability(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.plusDays(1).atStartOfDay();

		List<Room> rooms = roomRepository.findAll();
		List<RoomAvailabilityResponse> result = new ArrayList<>();

		for (Room room : rooms) {
			List<Reservation> reservations =
				reservationRepository.findByRoomAndStartAtBetweenOrderByStartAt(room, start, end);

			List<TimeSlot> reservedSlots = reservations.stream()
				.map(r -> new TimeSlot(r.getStartAt(), r.getEndAt()))
				.toList();

			List<TimeSlot> availableSlots = new ArrayList<>();
			LocalDateTime current = start;

			for (Reservation r : reservations) {
				if (current.isBefore(r.getStartAt())) {
					availableSlots.add(new TimeSlot(current, r.getStartAt()));
				}
				current = r.getEndAt();
			}
			if (current.isBefore(end)) {
				availableSlots.add(new TimeSlot(current, end));
			}

			result.add(new RoomAvailabilityResponse(room, reservedSlots, availableSlots));
		}

		return result;
	}
}
