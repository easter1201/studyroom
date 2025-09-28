package com.example.studyroom.controller;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyroom.dto.request.ReservationRequest;
import com.example.studyroom.dto.response.ReservationResponse;
import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.User;
import com.example.studyroom.service.ReservationService;
import com.example.studyroom.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<?> createReservation(
		@RequestHeader("Authorization") String token,
		@Valid @RequestBody ReservationRequest request) {
		Optional<User> user = userService.getByToken(token);

		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("접근이 유효하지 않습니다.");
		}

		try {
			Reservation reservation = reservationService.createReservation(
				user.get().getId(),
				request.getRoomId(),
				request.getStartAt(),
				request.getEndAt()
			);

			return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ReservationResponse(reservation));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> cancelReservation(@RequestHeader("Authorization") String token, @PathVariable Long id) {
		Optional<User> user = userService.getByToken(token);

		if (user.isEmpty()) {
			return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("접근이 유효하지 않습니다.");
		}

		try {
			reservationService.cancelReservation(user.get().getId(), id);
			return  ResponseEntity.noContent().build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}