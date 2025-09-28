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
import com.example.studyroom.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setup() {
		userRepository.deleteAll();
	}

	@Test
	void createUser_success() throws Exception {
		String requestJson = """
			{"email":"user@example.com","name":"user","password":"password"}
			""";

		mockMvc.perform(post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(content().string("회원가입 성공"));
	}

	@Test
	void createAdmin_success() throws Exception {
		String requestJson = """
			{"email":"admin@example.com","name":"admin","password":"password"}
			""";

		mockMvc.perform(post("/users/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(content().string("관리자 회원가입 성공"));
	}

	@Test
	void login_success() throws Exception {
		User user = new User("user@example.com", "user", "password", Role.USER, "user-token-1");
		userRepository.save(user);

		String requestJson = """
			{"email":"user@example.com","password":"password"}
			""";

		mockMvc.perform(post("/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	void login_fail() throws Exception {
		User user = new User("user@example.com", "user", "password", Role.USER, "user-token-1");
		userRepository.save(user);

		String requestJson = """
			{"email":"user@example.com","password":"wrong"}
			""";

		mockMvc.perform(post("/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isUnauthorized());
	}
}
