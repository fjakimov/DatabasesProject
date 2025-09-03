package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.Block;
import org.example.dormallocationsystem.Domain.Room;
import org.example.dormallocationsystem.Repository.BlockRepository;
import org.example.dormallocationsystem.Repository.RoomRepository;
import org.example.dormallocationsystem.Service.IRoomService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements IRoomService {
    private final RoomRepository roomRepository;
    private final BlockRepository blockRepository;
    private final static int FLOOR_CAPACITY_ROOMS = 16;

    public RoomServiceImpl(RoomRepository roomRepository, BlockRepository blockRepository) {
        this.roomRepository = roomRepository;
        this.blockRepository = blockRepository;
    }

    @Override
    public Map<Integer, Long> getTakenRoomsPerFloorInBlock(String blockId) {
        Block block = blockRepository.findByBlockId(blockId);
        List<Room> rooms = roomRepository.getRoomsByBlock(block);
        return rooms.stream().filter(room -> !room.getIsAvailable()).collect(
                Collectors.groupingBy(
                        room -> extractFloorFromRoomNumber(room.getId().getRoomNumber()),
                        Collectors.counting()
                ));
    }
    @Override
    public Map<Integer, Double> getFloorCapacityPercentage(String blockId) {
        Set<Integer> floors = getAllFloors(blockId);
        Map<Integer, Long> takenRooms = getTakenRoomsPerFloorInBlock(blockId);
        Map<Integer, Double> floorCapacity = new HashMap<>();

        for (Integer floor : floors) {
            long taken = takenRooms.getOrDefault(floor, 0L);
            double percentage = ((double) (FLOOR_CAPACITY_ROOMS - taken) / FLOOR_CAPACITY_ROOMS) * 100;
            floorCapacity.put(floor, percentage);
        }

        return floorCapacity;
    }

    @Override
    public Set<Integer> getAllFloors(String blockId) {
        Block blockRooms = blockRepository.findByBlockId(blockId);
        return roomRepository.getRoomsByBlock(blockRooms).stream().map(room -> {
            return extractFloorFromRoomNumber(room.getId().getRoomNumber());
        }).collect(Collectors.toSet());
    }

    @Override
    public List<Room> getRoomsInFloor(String blockId, Integer floorNumber) {
        Block block = blockRepository.findByBlockId(blockId);
        List<Room> roomsInBlock = roomRepository.getRoomsByBlock(block);
        return roomsInBlock.stream().filter(room -> {
            int firstRoomNumber = room.getId().getRoomNumber() / 100;
            return firstRoomNumber == floorNumber;
        }).toList();
    }
    private int extractFloorFromRoomNumber(Integer roomNumber) {
        return roomNumber / 100;
    }
}
