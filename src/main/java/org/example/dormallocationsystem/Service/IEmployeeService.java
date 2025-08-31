package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.*;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    boolean register(String email, String pass, String phoneNumber, String firstName, String lastName);
    boolean assignRoomToStudent(Long studentId, RoomId roomId);
    List<Roomrequest> viewRoomRequests();
    List<DormDocument> viewDocumentsToValidate(Student student);
    void addDocumentComment(Long documentId, String comment);
    List<DormDocument> getReviewedDocumentsByStudent(Long studentId);
    void approveDocument(Long documentId, Long employeeId);
    void declineDocument(Long documentId, Long employeeId);
    List<Student> getStudentsWithDocuments();
}
