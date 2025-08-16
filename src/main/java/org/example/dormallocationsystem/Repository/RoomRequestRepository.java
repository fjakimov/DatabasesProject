package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Domain.RoomrequestId;
import org.example.dormallocationsystem.Domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRequestRepository extends JpaRepository<Roomrequest, RoomrequestId> {
    boolean existsByStudentId(Long studentId);
    List<Roomrequest> findByStudent(Student student);

}
