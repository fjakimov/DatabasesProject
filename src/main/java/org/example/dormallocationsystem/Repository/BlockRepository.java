package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Block;
import org.example.dormallocationsystem.Domain.DormUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}
