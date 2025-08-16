package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.DormUser;
import org.example.dormallocationsystem.Domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DormUserRepository extends JpaRepository<DormUser, Long> {
    Optional<DormUser> findByEmail(String email);
}
