package com.example.studyroom.service;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.Room;
import com.example.studyroom.entity.User;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.RoomRepository;
import com.example.studyroom.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;

	@Transactional
	public Reservation createReservation(Long userId, Long roomId, LocalDateTime startAt, LocalDateTime endAt) {
		if (!startAt.isBefore(endAt)) {
			throw new IllegalArgumentException("시작일은 종료일보다 앞서야 합니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

		boolean isConflict = reservationRepository.existsByRoomAndEndAtAfterAndStartAtBefore(room, startAt, endAt);

		if (isConflict)  {
			throw new DataIntegrityViolationException("예약일이 선점되어 있습니다.");
		}

		Reservation  reservation = new Reservation(room, user, startAt, endAt);

		return reservationRepository.save(reservation);
	}

	@Transactional
	public void cancelReservation(Long userId, Long reservationId, boolean isAdmin) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

		if (!isAdmin && !reservation.getUser().getId().equals(userId))  {
			throw new SecurityException("권한이 존재하지 않습니다.");
		}

		reservationRepository.delete(reservation);
	}
}
