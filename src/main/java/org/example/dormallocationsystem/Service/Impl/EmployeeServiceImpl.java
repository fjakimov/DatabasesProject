package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.*;
import org.example.dormallocationsystem.Repository.*;
import org.example.dormallocationsystem.Service.IEmployeeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    private final DormUserRepository dormUserRepository;
    private final EmployeeRepository employeeRepository;
    private final DormDocumentRepository dormDocumentRepository;
    private final RoomRepository roomRepository;
    private final RoomRequestRepository roomRequestRepository;
    private final StudentTookRoomRepository studentTookRoomRepository;
    private final StudentRepository studentRepository;
    private final BlockRepository blockRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(DormUserRepository dormUserRepository, EmployeeRepository employeeRepository, DormDocumentRepository dormDocumentRepository, RoomRepository roomRepository, RoomRequestRepository roomRequestRepository, StudentTookRoomRepository studentTookRoomRepository, StudentRepository studentRepository, BlockRepository blockRepository, PasswordEncoder passwordEncoder) {
        this.dormUserRepository = dormUserRepository;
        this.employeeRepository = employeeRepository;
        this.dormDocumentRepository = dormDocumentRepository;
        this.roomRepository = roomRepository;
        this.roomRequestRepository = roomRequestRepository;
        this.studentTookRoomRepository = studentTookRoomRepository;
        this.studentRepository = studentRepository;
        this.blockRepository = blockRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean register(String email, String pass, String phoneNumber, String firstName, String lastName) {
        if(dormUserRepository.findByEmail(email).isEmpty()){
            DormUser dormUser = new DormUser();
            dormUser.setEmail(email);
            dormUser.setPass(passwordEncoder.encode(pass));
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
                    Block blockToUpdate = room.getBlock();
                    blockToUpdate.setNumAvailableRooms(blockToUpdate.getNumAvailableRooms() - 1);
                    blockRepository.save(blockToUpdate);
                }
                roomRepository.save(room);
                studentTookRoomRepository.save(studentTookRoom);
                Roomrequest roomrequest = roomRequestRepository.findByStudent(student);
                roomrequest.setStatus("Approved");
                roomRequestRepository.save(roomrequest);
                return true;
            }else{
                Roomrequest roomrequest = roomRequestRepository.findByStudent(student);
                roomrequest.setStatus("Declined");
                roomRequestRepository.save(roomrequest);
            }
        }

        return false;
    }
    @Override
    public boolean areAllDocumentsReviewed(Long studentId) {
        List<DormDocument> documents = dormDocumentRepository.findByStudentId(studentId);
        return documents.size() == 5 && documents.stream().noneMatch(dormDocument -> dormDocument.getDStatus().equals("Pending"));
    }

    @Override
    public boolean areAllDocumentsApproved(Long studentId) {
        List<DormDocument> documents = dormDocumentRepository.findByStudentId(studentId);
        return documents.size() == 5 && documents.stream().allMatch(dormDocument -> dormDocument.getDStatus().equals("Approved"));
    }

    @Override
    public List<Roomrequest> viewRoomRequests() {
        return null;
    }

    @Override
    public List<DormDocument> viewDocumentsToValidate(Student student) {
        return dormDocumentRepository.findByStudent(student).stream()
                .filter(dormDocument -> dormDocument.getDStatus().equals("Pending")).toList();
    }

    @Override
    public List<Student> getStudentsWithDocuments() {
        return studentRepository.findAll().stream()
                .filter(student -> !dormDocumentRepository.findByStudent(student).isEmpty())
                .toList();
    }

    @Override
    public List<DormDocument> getReviewedDocumentsByStudent(Long studentId) {
        return dormDocumentRepository.findByStudentId(studentId).stream()
                .filter(dormDocument -> !dormDocument.getDStatus().equals("Pending")).toList();
    }

    @Override
    public Roomrequest getRoomRequestsByStudent(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if(student.isPresent()) {
            return roomRequestRepository.findByStudent(student.get());
        }
        return null;
    }

    @Override
    public void approveDocument(Long documentId, Long employeeId) {
        Optional<DormDocument> documentOptional = dormDocumentRepository.findById(documentId);
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (documentOptional.isPresent() && employee.isPresent()) {
            DormDocument document = documentOptional.get();
            document.setDStatus("Approved");
            document.setEmployee(employee.get());
            dormDocumentRepository.save(document);
        }
    }

    @Override
    public void declineDocument(Long documentId, Long employeeId) {
        Optional<DormDocument> documentOptional = dormDocumentRepository.findById(documentId);
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (documentOptional.isPresent() && employee.isPresent()) {
            DormDocument document = documentOptional.get();
            document.setDStatus("Declined");
            document.setEmployee(employee.get());
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
