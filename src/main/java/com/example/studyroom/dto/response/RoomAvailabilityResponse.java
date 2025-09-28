package com.example.studyroom.dto.response;

import java.util.List;

import com.example.studyroom.entity.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomAvailabilityResponse {
	private Long roomId;
	private String roomName;
	private int capacity;
	private List<TimeSlot> reservations;
	private List<TimeSlot> availableSlots;

	public RoomAvailabilityResponse(Room room, List<TimeSlot> reservations, List<TimeSlot> availableSlots) {
		this.roomId = room.getId();
		this.roomName = room.getName();
		this.capacity = room.getCapacity();
		this.reservations = reservations;
		this.availableSlots = availableSlots;
	}
}
