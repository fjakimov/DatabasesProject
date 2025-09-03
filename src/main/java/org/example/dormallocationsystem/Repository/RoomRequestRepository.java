package org.example.dormallocationsystem.Repository;

import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Domain.RoomrequestId;
import org.example.dormallocationsystem.Domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRequestRepository extends JpaRepository<Roomrequest, RoomrequestId> {
    boolean existsByStudentId(Long studentId);
    Roomrequest findByStudent(Student student);

    @Query(value = """
        SELECT 
            du.u_id AS studentId,
            CONCAT(du.first_name, ' ', du.last_name) AS studentName,
            rr.block_id AS requestedBlock,
            rr.room_number AS requestedRoom,
            str.block_id AS assignedBlock,
            str.room_num AS assignedRoom
        FROM dorm_user du
        JOIN roomrequest rr ON du.u_id = rr.student_id
        JOIN studenttookroom str ON str.student_id = du.u_id
        WHERE rr.block_id != str.block_id OR rr.room_number != str.room_num
        """, nativeQuery = true)
    List<Object[]> findRoomMismatches();
}
