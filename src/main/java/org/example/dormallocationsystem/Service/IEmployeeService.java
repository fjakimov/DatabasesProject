package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.*;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    boolean register(String email, String pass, String phoneNumber, String firstName, String lastName);
    boolean loginEmployee(String email, String password);
    boolean assignRoomToStudent(Long studentId, RoomId roomId);
    List<Roomrequest> viewRoomRequests();
    List<DormDocument> viewDocumentsToValidate();
    void addDocumentComment(Long documentId, String comment);
    List<DormDocument> getDocumentsByStudent(Long studentId);
    List<Roomrequest> getRoomRequestsByStudent(Long studentId);
    void validateDocument(Long documentId);
    List<Student> getStudentsWithDocuments();
    boolean areAllDocumentsValidated(Long studentId);

}
