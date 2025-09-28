package com.example.studyroom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomRequest {
	private String name;
	private String location;
	private int capacity;
}
