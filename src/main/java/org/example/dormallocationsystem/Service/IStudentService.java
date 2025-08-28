package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.DormDocument;
import org.example.dormallocationsystem.Domain.DormUser;
import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Domain.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IStudentService {
    boolean registerStudent(String email, String pass, String firstName, String lastName, String phoneNumber, String facultyName, Integer yearOfStudies, String gender);
    boolean uploadDocument(MultipartFile file, Long studentId);
    void submitRoomRequest(Roomrequest roomrequest);
    boolean applyForRoom(Long studentId, String preferredRoom, String roommateEmail);
    Long getStudentIdByEmail(String email);
    long getUploadedDocumentsCount(Long studentId);
    Roomrequest getRoomRequestsByStudent(Long studentId);
    List<DormDocument> getDocumentsByStudent(Long studentId);
    DormUser getUserDetails(Long studentId);
    Student getStudentByEmail(String email);

    boolean identicalRoomRequestByStudents(Roomrequest roomRequestStudent1, Roomrequest roomRequestStudent2);
}
