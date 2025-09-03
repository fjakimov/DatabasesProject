package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.DormUser;
import org.example.dormallocationsystem.Domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByDormUser_Email(String email);
    List<Student> findDistinctByPaymentListIsNotEmpty();
}