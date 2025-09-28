package com.example.studyroom.dto.response;

import java.time.LocalDateTime;

import com.example.studyroom.entity.Reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationResponse {
	private Long id;
	private Long roomId;
	private Long userId;
	private LocalDateTime startAt;
	private LocalDateTime endAt;

	public ReservationResponse(Reservation reservation) {
		this.id = reservation.getId();
		this.roomId = reservation.getRoom().getId();
		this.userId = reservation.getUser().getId();
		this.startAt = reservation.getStartAt();
		this.endAt = reservation.getEndAt();
	}
}
