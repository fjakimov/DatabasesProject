package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.Room;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRoomService {
    Map<Integer, Long> getTakenRoomsPerFloorInBlock(String blockId);
    Map<Integer, Double> getFloorCapacityPercentage(String blockId);
    Set<Integer> getAllFloors(String blockId);
    List<Room> getRoomsInFloor(String blockId, Integer floorNumber);
}
