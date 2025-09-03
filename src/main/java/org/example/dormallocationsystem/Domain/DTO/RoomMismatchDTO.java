package org.example.dormallocationsystem.Domain.DTO;

import lombok.Data;

@Data
public class RoomMismatchDTO {
    private Long studentId;
    private String studentName;
    private String requestedBlock;
    private Integer  requestedRoom;
    private String assignedBlock;
    private Integer  assignedRoom;

    public RoomMismatchDTO() {
    }

    public RoomMismatchDTO(Long studentId, String studentName, String requestedBlock, Integer  requestedRoom,
                           String assignedBlock, Integer assignedRoom) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.requestedBlock = requestedBlock;
        this.requestedRoom = requestedRoom;
        this.assignedBlock = assignedBlock;
        this.assignedRoom = assignedRoom;
    }
}
