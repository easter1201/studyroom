package com.example.studyroom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeSlot {
	private LocalDateTime startAt;
	private LocalDateTime endAt;
}
