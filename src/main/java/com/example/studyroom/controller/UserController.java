package com.example.studyroom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyroom.dto.request.LoginRequest;
import com.example.studyroom.dto.request.SignupRequest;
import com.example.studyroom.dto.response.LoginResponse;
import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	public ResponseEntity<String> createUser(@RequestBody SignupRequest request) {
		Role role = Role.USER;
		userService.signup(request, role);

		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
	}

	@PostMapping("/admin")
	public ResponseEntity<String> createAdmin(@RequestBody SignupRequest request) {
		Role role = Role.ADMIN;
		userService.signup(request, role);

		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
	}

	@GetMapping
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		String token = userService.login(request);

		LoginResponse response =  new LoginResponse(token);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
