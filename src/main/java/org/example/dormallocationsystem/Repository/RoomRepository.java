package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Room;
import org.example.dormallocationsystem.Domain.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, RoomId> {
    Optional<Room> findById(RoomId roomId);
}
