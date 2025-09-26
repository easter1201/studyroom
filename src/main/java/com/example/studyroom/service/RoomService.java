package com.example.studyroom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.studyroom.entity.Room;
import com.example.studyroom.repository.RoomRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoomService {
	private final RoomRepository roomRepository;

	public Room createRoom(Room room) {
		return roomRepository.save(room);
	}

	public List<Room> getAll() {
		return roomRepository.findAll();
	}
}
