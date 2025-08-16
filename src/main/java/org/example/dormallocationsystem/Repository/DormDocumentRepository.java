package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.DormDocument;
import org.example.dormallocationsystem.Domain.Student;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DormDocumentRepository extends JpaRepository<DormDocument, Long> {
    List<DormDocument> findByStudentId(Long studentId);
    List<DormDocument> findByEmployeeId(Long employeeId);
    long countByStudentId(Long studentId);
    List<DormDocument> findByStudent(Student student);

}
