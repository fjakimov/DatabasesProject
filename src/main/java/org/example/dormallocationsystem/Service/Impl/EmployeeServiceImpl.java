package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.*;
import org.example.dormallocationsystem.Repository.*;
import org.example.dormallocationsystem.Service.IEmployeeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    private final DormUserRepository dormUserRepository;
    private final EmployeeRepository employeeRepository;
    private final DormDocumentRepository dormDocumentRepository;
    private final RoomRepository roomRepository;
    private final RoomRequestRepository roomRequestRepository;
    private final StudentTookRoomRepository studentTookRoomRepository;
    private final StudentRepository studentRepository;

    public EmployeeServiceImpl(DormUserRepository dormUserRepository, EmployeeRepository employeeRepository, DormDocumentRepository dormDocumentRepository, RoomRepository roomRepository, RoomRequestRepository roomRequestRepository, StudentTookRoomRepository studentTookRoomRepository, StudentRepository studentRepository) {
        this.dormUserRepository = dormUserRepository;
        this.employeeRepository = employeeRepository;
        this.dormDocumentRepository = dormDocumentRepository;
        this.roomRepository = roomRepository;
        this.roomRequestRepository = roomRequestRepository;
        this.studentTookRoomRepository = studentTookRoomRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public boolean register(String email, String pass, String phoneNumber, String firstName, String lastName) {
        if(dormUserRepository.findByEmail(email).isEmpty()){
            DormUser dormUser = new DormUser();
            dormUser.setEmail(email);
            dormUser.setPass(pass);
            dormUser.setPhoneNumber(phoneNumber);
            dormUser.setFirstName(firstName);
            dormUser.setLastName(lastName);
            dormUser = dormUserRepository.save(dormUser);
            Employee employee = new Employee();
            employee.setDormUser(dormUser);
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    @Override
    public boolean loginEmployee(String email, String password) {
        if(dormUserRepository.findByEmail(email).isPresent()){
            DormUser employee = dormUserRepository.findByEmail(email).get();
            return employee.getPass().equals(password);
        }
        return false;
    }
    @Override
    public boolean assignRoomToStudent(Long studentId, RoomId roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalRoom.isPresent() && optionalStudent.isPresent()) {
            Room room = optionalRoom.get();
            Student student = optionalStudent.get();

            if (room.getIsAvailable() && room.getCapacity() > 0) {
                Studenttookroom studentTookRoom = new Studenttookroom();
                StudenttookroomId studentTookRoomId = new StudenttookroomId();
                studentTookRoomId.setStudentId(student.getId());
                studentTookRoomId.setRoomNum(room.getId().getRoomNumber());
                studentTookRoomId.setBlockId(room.getId().getBlockId());
                studentTookRoom.setId(studentTookRoomId);
                studentTookRoom.setStudent(student);
                studentTookRoom.setStartDate(LocalDate.now());

                room.setCapacity(room.getCapacity() - 1);
                if (room.getCapacity() == 0) {
                    room.setIsAvailable(false);
                }
                roomRepository.save(room);

                studentTookRoomRepository.save(studentTookRoom);
                List<Roomrequest> roomRequests = roomRequestRepository.findByStudent(student);
                for (Roomrequest request : roomRequests) {
                    request.setStatus("Approved");
                    roomRequestRepository.save(request);
                }
                return true;
            }else{
                List<Roomrequest> roomRequests = roomRequestRepository.findByStudent(student);
                for (Roomrequest request : roomRequests) {
                    request.setStatus("Declined");
                    roomRequestRepository.save(request);
                }
            }
        }

        return false;
    }
    @Override
    public boolean areAllDocumentsValidated(Long studentId) {
        List<DormDocument> documents = dormDocumentRepository.findByStudentId(studentId);
        return documents.size() == 5 && documents.stream().allMatch(dormDocument -> dormDocument.getDStatus().equals("Approved"));
    }
    @Override
    public List<Roomrequest> viewRoomRequests() {
        return null;
    }

    @Override
    public List<DormDocument> viewDocumentsToValidate() {
        return null;
    }


    @Override
    public List<Student> getStudentsWithDocuments() {
        return studentRepository.findAll().stream()
                .filter(student -> !dormDocumentRepository.findByStudent(student).isEmpty())
                .toList();
    }

    @Override
    public List<DormDocument> getDocumentsByStudent(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(dormDocumentRepository::findByStudent).orElse(null);
    }

    @Override
    public List<Roomrequest> getRoomRequestsByStudent(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(roomRequestRepository::findByStudent).orElse(null);
    }

    @Override
    public void validateDocument(Long documentId) {
        Optional<DormDocument> documentOptional = dormDocumentRepository.findById(documentId);
        if (documentOptional.isPresent()) {
            DormDocument document = documentOptional.get();
            document.setDStatus("Approved");
            dormDocumentRepository.save(document);
        }
    }

    @Override
    public void addDocumentComment(Long documentId, String comment) {
        Optional<DormDocument> documentOptional = dormDocumentRepository.findById(documentId);
        if (documentOptional.isPresent()) {
            DormDocument document = documentOptional.get();
            document.setDComment(comment);
            dormDocumentRepository.save(document);
        }
    }
}
