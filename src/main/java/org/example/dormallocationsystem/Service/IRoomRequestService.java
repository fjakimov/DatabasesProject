package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.DTO.RoomMismatchDTO;
import org.example.dormallocationsystem.Domain.Roomrequest;

import java.util.List;

public interface IRoomRequestService {
    Roomrequest findRoomRequestForStudent(Long studentId);
    List<RoomMismatchDTO> findRoomMismatches();
}
