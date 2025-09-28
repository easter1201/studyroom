package com.example.studyroom.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.studyroom.dto.request.RoomRequest;
import com.example.studyroom.dto.response.RoomAvailabilityResponse;
import com.example.studyroom.dto.response.RoomResponse;
import com.example.studyroom.entity.Room;
import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.service.RoomService;
import com.example.studyroom.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
public class RoomController {
	private final RoomService roomService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String token, @RequestBody RoomRequest request)  {
		Optional<User> user = userService.getByToken(token);

		if (user.isEmpty() || user.get().getRole() != Role.ADMIN) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 존재하지 않습니다.");
		}

		Room createdRoom = roomService.createRoom(new Room(
			request.getName(),
			request.getLocation(),
			request.getCapacity()
		));
		return ResponseEntity.status(HttpStatus.CREATED).body(new RoomResponse(createdRoom));
	}

	@GetMapping
	public ResponseEntity<List<RoomAvailabilityResponse>> getRoomAvailability(@RequestParam("date") String date) {
		LocalDate localDate = LocalDate.parse(date);
		List<RoomAvailabilityResponse> availability = roomService.getRoomAvailability(localDate);

		return ResponseEntity.ok(availability);
	}
}
