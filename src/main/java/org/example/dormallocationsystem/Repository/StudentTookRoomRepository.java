package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Studenttookroom;
import org.example.dormallocationsystem.Domain.StudenttookroomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTookRoomRepository extends JpaRepository<Studenttookroom, StudenttookroomId> {
}
