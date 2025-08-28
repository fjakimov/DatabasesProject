package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.Studenttookroom;

public interface IStudentTookRoomService {
    Studenttookroom getStudentInRoom(Long studentId);
}
