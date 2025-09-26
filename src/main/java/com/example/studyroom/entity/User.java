package com.example.studyroom.entity;

import com.example.studyroom.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Id
	@Column(nullable = false,  unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false, unique = true)
	private String token;

	public User(String email, String name, String password, Role role, String token) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role;
		this.token = token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
