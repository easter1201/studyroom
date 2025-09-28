package com.example.studyroom.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.studyroom.entity.Reservation;
import com.example.studyroom.entity.Room;
import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.RoomRepository;
import com.example.studyroom.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private ReservationRepository reservationRepository;

	Long roomId;

	@BeforeEach
	void setup() {
		reservationRepository.deleteAll();
		roomRepository.deleteAll();
		userRepository.deleteAll();

		User user1 = new User("user1@example.com", "User1", "password", Role.USER, "user-token-1");
		User user2 = new User("user2@example.com", "User2", "password", Role.USER, "user-token-2");
		userRepository.saveAll(List.of(user1, user2));

		Room room1 = new Room("A", "1", 10);
		Room savedRoom = roomRepository.save(room1);

		roomId = savedRoom.getId();

		Reservation reservation = new Reservation(savedRoom, user1,
			LocalDateTime.of(2025,1,2,0,0),
			LocalDateTime.of(2025,1,2,3,0));
		reservationRepository.save(reservation);
	}
	@Test
	void createReservation_success() throws Exception {
		String requestJson = String.format("""
				{"roomId":%d,"startAt":"2025-01-01T00:00","endAt":"2025-01-01T01:00"}
			""", roomId);

		mockMvc.perform(post("/reservations")
			.header("Authorization", "user-token-1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isCreated());
	}

	@Test
	void createReservation_conflict() throws Exception {
		String requestJson = String.format("""
				{"roomId":%d,"startAt":"2025-01-01T00:00","endAt":"2025-01-01T01:00"}
			""", roomId);

		mockMvc.perform(post("/reservations")
				.header("Authorization", "user-token-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/reservations")
				.header("Authorization", "user-token-2")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isConflict());
	}

	@Test
	void createReservation_invalid() throws Exception {
		String requestJson = String.format("""
				{"roomId":%d,"startAt":"2025-01-01T12:00","endAt":"2025-01-01T01:00"}
			""", roomId);

		mockMvc.perform(post("/reservations")
				.header("Authorization", "user-token-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void createReservation_cancelAndReBook() throws Exception {
		String requestJson = String.format("""
				{"roomId":%d,"startAt":"2025-01-01T00:00","endAt":"2025-01-01T01:00"}
			""", roomId);

		MvcResult result = mockMvc.perform(post("/reservations")
				.header("Authorization", "user-token-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated())
			.andReturn();

		String content = result.getResponse().getContentAsString();
		Long reservationId = JsonPath.parse(content).read("$.id", Long.class);

		mockMvc.perform(delete("/reservations/" + reservationId)
			.header("Authorization", "user-token-1"))
			.andExpect(status().isNoContent());

		mockMvc.perform(post("/reservations")
				.header("Authorization", "user-token-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated());
	}

	@Test
	void getAvailability_success() throws Exception {
		mockMvc.perform(get("/rooms")
			.param("date", "2025-01-02"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].roomId").value(roomId))
			.andExpect(jsonPath("$[0].reservations[0].startAt").value("2025-01-02T00:00:00"))
			.andExpect(jsonPath("$[0].reservations[0].endAt").value("2025-01-02T03:00:00"));
	}
}
