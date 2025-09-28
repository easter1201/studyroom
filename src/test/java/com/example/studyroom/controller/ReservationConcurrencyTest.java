package com.example.studyroom.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.studyroom.entity.Room;
import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.RoomRepository;
import com.example.studyroom.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationConcurrencyTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	RoomRepository roomRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ReservationRepository reservationRepository;

	Long roomId;

	@BeforeEach
	void setup() {
		reservationRepository.deleteAll();
		roomRepository.deleteAll();
		userRepository.deleteAll();

		for (int i = 1; i < 11; i++) {
			userRepository.save(new User(
				"user" + i + "@example.com",
				"User" + i, "password",
				Role.USER, "user-token-" + i
			));
		}

		Room room = new Room("A", "1", 10);
		roomId = roomRepository.save(room).getId();
	}

	@Test
	void concurrent_onlyOne() throws Exception {
		String requestJson = String.format("""
				{"roomId":%d,"startAt":"2025-01-01T00:00","endAt":"2025-01-01T01:00"}
			""", roomId);

		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<MvcResult>> futures = new ArrayList<>();

		for (int i  = 1; i < 11; i++) {
			final int userIndex = i;
			futures.add(executor.submit(() ->
				mockMvc.perform(post("/reservations")
					.header("Authorization", "user-token-" + userIndex)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson))
					.andReturn()
			));
		}

		int success = 0;
		int conflict = 0;

		for (Future<MvcResult> f : futures) {
			MvcResult result = f.get();
			int status = result.getResponse().getStatus();
			if (status == 201) success++;
			if (status == 409) conflict++;
		}

		executor.shutdown();

		assertEquals(1, success);
		assertEquals(9, conflict);
	}
}
