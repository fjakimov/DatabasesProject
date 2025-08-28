package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Student;
import org.example.dormallocationsystem.Domain.Studenttookroom;
import org.example.dormallocationsystem.Domain.StudenttookroomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentTookRoomRepository extends JpaRepository<Studenttookroom, StudenttookroomId> {
    Optional<Studenttookroom> findByIdStudentId(Long studentId);
    Optional<Studenttookroom> findByStudent(Student student);
}
