package com.example.studyroom.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.studyroom.dto.request.LoginRequest;
import com.example.studyroom.dto.request.SignupRequest;
import com.example.studyroom.entity.User;
import com.example.studyroom.enums.Role;
import com.example.studyroom.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public void signup(SignupRequest request, Role role) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("이미 가입된 이메일입니다.");
		}

		String token = "";

		User newUser = new User(request.getEmail(), request.getName(), request.getPassword(), role, token);

		User savedUser = userRepository.save(newUser);

		if (role.equals(Role.ADMIN)) {
			savedUser.setToken("admin-token");
		} else {
			savedUser.setToken("user-token-" + savedUser.getId());
		}

		userRepository.save(savedUser);
	}

	public String login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));

		if (!request.getPassword().equals(user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		return user.getToken();
	}

	public Optional<User> getByToken(String token) {
		return userRepository.findByToken(token);
	}
}
