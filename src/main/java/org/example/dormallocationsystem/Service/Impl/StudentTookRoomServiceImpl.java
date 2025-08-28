package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.Studenttookroom;
import org.example.dormallocationsystem.Repository.StudentTookRoomRepository;
import org.example.dormallocationsystem.Service.IStudentTookRoomService;
import org.springframework.stereotype.Service;

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
}
