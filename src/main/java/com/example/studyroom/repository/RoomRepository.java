package com.example.studyroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyroom.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
