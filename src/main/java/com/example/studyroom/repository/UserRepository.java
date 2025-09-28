package com.example.studyroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyroom.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByToken(String token);

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}
