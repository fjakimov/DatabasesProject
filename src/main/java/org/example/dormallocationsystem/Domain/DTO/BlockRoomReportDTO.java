package org.example.dormallocationsystem.Domain.DTO;

import lombok.Data;

@Data
public class BlockRoomReportDTO {
    private String blockId;
    private Long freeRoomsAfterFirstDay;
    private Long pendingRoomRequests;

    public BlockRoomReportDTO(String blockId, Long freeRoomsAfterFirstDay, Long pendingRoomRequests) {
        this.blockId = blockId;
        this.freeRoomsAfterFirstDay = freeRoomsAfterFirstDay;
        this.pendingRoomRequests = pendingRoomRequests;
    }
}
