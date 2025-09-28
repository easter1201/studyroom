package com.example.studyroom.dto.response;

import com.example.studyroom.entity.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {
	private Long id;
	private String name;
	private String location;
	private int capacity;

	public RoomResponse(Room room) {
		this.id = room.getId();
		this.name = room.getName();
		this.location = room.getLocation();
		this.capacity = room.getCapacity();
	}
}
