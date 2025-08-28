package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.Roomrequest;

public interface IRoomRequestService {
    Roomrequest findRoomRequestForStudent(Long studentId);
}
