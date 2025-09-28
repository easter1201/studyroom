package com.example.studyroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.Room;
import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.RoomRepository;
import com.example.studyroom.repository.UserRepository;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;

	public Reservation createReservation(Long userId, Long roomId, LocalDateTime startAt, LocalDateTime endAt) {
		if (!startAt.isBefore(endAt)) {
			throw new IllegalArgumentException("시작일은 종료일보다 앞서야 합니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		Room room = roomRepository.findByIdForUpdate(roomId)
			.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

		List<Reservation> overlap = reservationRepository.findOverlappingReservations(roomId, startAt, endAt);
		if (!overlap.isEmpty()) {
			throw new DataIntegrityViolationException("동일 시간대 예약이 존재합니다.");
		}

		Reservation reservation = new Reservation(room, user, startAt, endAt);
		try {
			return reservationRepository.save(reservation);
		} catch (OptimisticLockException e) {
			throw new DataIntegrityViolationException("동일 시간대 예약이 존재합니다.");
		}
	}

	public void cancelReservation(Long userId, Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		boolean isAdmin = user.getRole() == Role.ADMIN;

		if (!isAdmin && !reservation.getUser().getId().equals(userId))  {
			throw new SecurityException("권한이 존재하지 않습니다.");
		}

		reservationRepository.delete(reservation);
	}
}
