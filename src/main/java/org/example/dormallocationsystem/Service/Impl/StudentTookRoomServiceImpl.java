package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.Studenttookroom;
import org.example.dormallocationsystem.Repository.StudentTookRoomRepository;
import org.example.dormallocationsystem.Service.IStudentTookRoomService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentTookRoomServiceImpl implements IStudentTookRoomService {
    private final StudentTookRoomRepository studentTookRoomRepository;

    public StudentTookRoomServiceImpl(StudentTookRoomRepository studentTookRoomRepository) {
        this.studentTookRoomRepository = studentTookRoomRepository;
    }

    @Override
    public Studenttookroom getStudentInRoom(Long studentId) {
        Optional<Studenttookroom> str = studentTookRoomRepository.findByIdStudentId(studentId);
        if (str.isPresent()) {
            return str.get();
        } else {
            return null;
        }
    }

    @Override
    public void createEndStayRequest(Long studentId, LocalDate requestedEndDate) {
        Studenttookroom str = getStudentInRoom(studentId);
        if (str != null) {
            str.setEndStayRequested(true);
            str.setRequestedEndDate(requestedEndDate);
            studentTookRoomRepository.save(str);
        }
    }

    @Override
    public void save(Studenttookroom studenttookroom) {
        studentTookRoomRepository.save(studenttookroom);
    }

    @Override
    public List<Studenttookroom> getPendingEndStayRequests() {
        return studentTookRoomRepository.findAll().stream()
                .filter(str -> str.getEndDate() == null && str.getEndStayRequested())
                .toList();
    }

    @Override
    public void approveEndStay(Long studentId) {
        Studenttookroom str = getStudentInRoom(studentId);
        if (str != null && str.getEndStayRequested()) {
            str.setEndDate(str.getRequestedEndDate());
            studentTookRoomRepository.save(str);
        }
    }
}
