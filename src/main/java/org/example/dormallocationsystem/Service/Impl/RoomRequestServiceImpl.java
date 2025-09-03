package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.DTO.RoomMismatchDTO;
import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Domain.Student;
import org.example.dormallocationsystem.Repository.RoomRequestRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.example.dormallocationsystem.Service.IRoomRequestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomRequestServiceImpl implements IRoomRequestService {
    private final RoomRequestRepository roomRequestRepository;
    private final StudentRepository studentRepository;

    public RoomRequestServiceImpl(RoomRequestRepository roomRequestRepository, StudentRepository studentRepository) {
        this.roomRequestRepository = roomRequestRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Roomrequest findRoomRequestForStudent(Long studentId) {
        if (roomRequestRepository.existsByStudentId(studentId)) {
            Optional<Student> student = studentRepository.findById(studentId);
            if (student.isPresent()) {
                return roomRequestRepository.findByStudent(student.get());
            }
        }
        return null;
    }

    @Override
    public List<RoomMismatchDTO> findRoomMismatches() {
        List<Object[]> mismatches = roomRequestRepository.findRoomMismatches();
        return mismatches.stream().map(row -> {
            RoomMismatchDTO dto = new RoomMismatchDTO();
            dto.setStudentId(((Number) row[0]).longValue());
            dto.setStudentName((String) row[1]);
            dto.setRequestedBlock(row[2].toString());
            dto.setRequestedRoom(((Number) row[3]).intValue());
            dto.setAssignedBlock(row[4].toString());
            dto.setAssignedRoom(((Number) row[5]).intValue());
            return dto;
        }).toList();
    }


}
