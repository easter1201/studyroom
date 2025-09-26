package com.example.studyroom.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.studyroom.entity.User;
import com.example.studyroom.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public Optional<User> getByToken(String token) {
		return userRepository.findByToken(token);
	}
}
