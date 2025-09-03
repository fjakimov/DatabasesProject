package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Block;
import org.example.dormallocationsystem.Domain.DormUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Block findByBlockId(String blockId);
    @Query(value = """
        SELECT 
            b.block_id AS blockId,
            (SELECT COUNT(*) 
             FROM Room r 
             WHERE r.block_id = b.block_id 
               AND r.is_available = TRUE 
               AND r.capacity > 0) AS freeRooms,
            (SELECT COUNT(*) 
             FROM RoomRequest rr 
             WHERE rr.status = 'Pending' 
               AND rr.block_id = b.block_id) 
             AS pendingRoomRequests
        FROM Block b
        ORDER BY b.block_id
        """, nativeQuery = true)
    List<Object[]> getBlockRoomReport();
}
