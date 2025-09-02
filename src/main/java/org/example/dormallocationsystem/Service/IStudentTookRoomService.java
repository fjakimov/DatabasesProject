package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.Studenttookroom;

import java.time.LocalDate;
import java.util.List;

public interface IStudentTookRoomService {
    Studenttookroom getStudentInRoom(Long studentId);
    void createEndStayRequest(Long studentId, LocalDate requestedEndDate);
    void save(Studenttookroom studenttookroom);
    List<Studenttookroom> getPendingEndStayRequests();
    void approveEndStay(Long studentId);
}
