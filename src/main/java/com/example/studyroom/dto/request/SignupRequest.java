package com.example.studyroom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequest {
	String email;
	String name;
	String password;
}
