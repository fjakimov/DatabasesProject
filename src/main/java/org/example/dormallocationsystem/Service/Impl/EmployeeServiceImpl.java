package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.*;
import org.example.dormallocationsystem.Repository.*;
import org.example.dormallocationsystem.Service.IDormDocumentService;
import org.example.dormallocationsystem.Service.IEmployeeService;
import org.example.dormallocationsystem.Service.IStudentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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
    private final IStudentService studentService;
    private final IDormDocumentService dormDocumentService;

    public EmployeeServiceImpl(DormUserRepository dormUserRepository, EmployeeRepository employeeRepository, DormDocumentRepository dormDocumentRepository, RoomRepository roomRepository, RoomRequestRepository roomRequestRepository, StudentTookRoomRepository studentTookRoomRepository, StudentRepository studentRepository, BlockRepository blockRepository, PasswordEncoder passwordEncoder, IStudentService studentService, IDormDocumentService dormDocumentService) {
        this.dormUserRepository = dormUserRepository;
        this.employeeRepository = employeeRepository;
        this.dormDocumentRepository = dormDocumentRepository;
        this.roomRepository = roomRepository;
        this.roomRequestRepository = roomRequestRepository;
        this.studentTookRoomRepository = studentTookRoomRepository;
        this.studentRepository = studentRepository;
        this.blockRepository = blockRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
        this.dormDocumentService = dormDocumentService;
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
    @Transactional
    public void assignRoomToStudent(Long studentId, RoomId roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalRoom.isPresent() && optionalStudent.isPresent()) {
            Room room = optionalRoom.get();
            Student student = optionalStudent.get();

            if (room.getIsAvailable() && room.getCapacity() > 0) {
                if (roomRequestRepository.existsByStudentId(studentId)){
                    Roomrequest roomrequest = roomRequestRepository.findByStudent(student);
                    boolean allStudentDocsReviewed = dormDocumentService.areAllDocumentsReviewed(studentId);
                    boolean allStudentDocsApproved = dormDocumentService.areAllDocumentsApproved(studentId);
                    if (!roomrequest.getRoomateEmail().isBlank()) {
                        Optional<Student> roommate = studentRepository.findByDormUser_Email(roomrequest.getRoomateEmail());
                        if (roommate.isPresent()) {
                            Optional<Studenttookroom> str = studentTookRoomRepository.findByStudent(roommate.get());
                            if (roomRequestRepository.existsByStudentId(roommate.get().getId()))
                            {
                                Roomrequest roommatesRoomRequest = studentService.getRoomRequestsByStudent(roommate.get().getId());
                                boolean isIdenticalRoomRequest = studentService.identicalRoomRequestByStudents(roomrequest, roommatesRoomRequest);
                                boolean allDocsReviewed = dormDocumentService.areAllDocumentsReviewed(roommate.get().getId());
                                boolean allRoomatesDocsApproved = dormDocumentService.areAllDocumentsApproved(roommate.get().getId());
                                if (isIdenticalRoomRequest && str.isEmpty() && !room.getIsReserved()) {
                                    room.setIsReserved(true);
                                }
                                else if (isIdenticalRoomRequest && allRoomatesDocsApproved && str.isEmpty()) {
                                    roommatesRoomRequest.setStatus("Approved");
                                    Studenttookroom roommateTookRoom = new Studenttookroom();
                                    StudenttookroomId roommateTookRoomId = new StudenttookroomId();
                                    roommateTookRoomId.setStudentId(student.getId());
                                    roommateTookRoomId.setRoomNum(room.getId().getRoomNumber());
                                    roommateTookRoomId.setBlockId(room.getId().getBlockId());
                                    roommateTookRoom.setId(roommateTookRoomId);
                                    roommateTookRoom.setStudent(roommate.get());
                                    room.setIsReserved(false);
                                    room.setIsAvailable(false);
                                    room.setCapacity(room.getCapacity() - 1);
                                } else if (!isIdenticalRoomRequest && !allStudentDocsApproved) {
                                    roommatesRoomRequest.setStatus("Declined");
                                    room.setIsReserved(false);
                                }
                            }
                        } else {
                            room.setIsReserved(false);
                        }
                    }
                    if (allStudentDocsApproved ) {
                        roomrequest.setStatus("Approved");
                    } else if (allStudentDocsReviewed) {
                        roomrequest.setStatus("Declined");
                    }
                    roomRequestRepository.save(roomrequest);
                    if (roomrequest.getStatus().equals("Approved")) {
                        Studenttookroom studentTookRoom = new Studenttookroom();
                        StudenttookroomId studentTookRoomId = new StudenttookroomId();
                        studentTookRoomId.setStudentId(student.getId());
                        studentTookRoomId.setRoomNum(room.getId().getRoomNumber());
                        studentTookRoomId.setBlockId(room.getId().getBlockId());
                        studentTookRoom.setId(studentTookRoomId);
                        studentTookRoom.setStudent(student);
                        studentTookRoom.setStartDate(LocalDate.now());
                        room.setCapacity(room.getCapacity() - 1);
                        studentTookRoomRepository.save(studentTookRoom);
                    }
                } else {
                    Studenttookroom studentTookRoom = new Studenttookroom();
                    StudenttookroomId studentTookRoomId = new StudenttookroomId();
                    studentTookRoomId.setStudentId(student.getId());
                    studentTookRoomId.setRoomNum(room.getId().getRoomNumber());
                    studentTookRoomId.setBlockId(room.getId().getBlockId());
                    studentTookRoom.setId(studentTookRoomId);
                    studentTookRoom.setStudent(student);
                    studentTookRoom.setStartDate(LocalDate.now());
                    room.setCapacity(room.getCapacity() - 1);
                    studentTookRoomRepository.save(studentTookRoom);
                }
                if (room.getCapacity() == 0) {
                    room.setIsAvailable(false);
                    room.setIsReserved(false);
                    Block blockToUpdate = room.getBlock();
                    blockToUpdate.setNumAvailableRooms(blockToUpdate.getNumAvailableRooms() - 1);
                    blockRepository.save(blockToUpdate);
                }
                roomRepository.save(room);
            } else {
                Roomrequest roomrequest = roomRequestRepository.findByStudent(student);
                roomrequest.setStatus("Declined");
                roomRequestRepository.save(roomrequest);
            }
        }
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
    public void approveDocument(Long documentId, Long employeeId) {
        Optional<DormDocument> documentOptional = dormDocumentRepository.findById(documentId);
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (documentOptional.isPresent() && employee.isPresent()) {
            DormDocument document = documentOptional.get();
            document.setDStatus("Approved");
            document.setEmployee(employee.get());
            Roomrequest request = roomRequestRepository.findByStudent(document.getStudent());
            if (request != null) {
                request.setEmployee(employee.get());
            }
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
