package com.example.studyroom.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.repository.RoomRepository;
import com.example.studyroom.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoomRepository roomRepository;

	@BeforeEach
	void setup() {
		roomRepository.deleteAll();
		userRepository.deleteAll();

		User admin = new User("admin@example.com", "admin", "password", Role.ADMIN, "admin-token");
		userRepository.save(admin);
	}

	@Test
	void createRoom_success() throws Exception {
		String requestJson = """
			{"name":"A","location":"1","capacity":10}	
		""";

		mockMvc.perform(post("/rooms")
			.header("Authorization", "admin-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("A"));
	}

	@Test
	void createRoom_forbidden() throws Exception {
		String requestJson = """
			{"name":"A","location":"1","capacity":10}	
		""";

		mockMvc.perform(post("/rooms")
			.header("Authorization", "user-token-1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isForbidden());
	}
}
