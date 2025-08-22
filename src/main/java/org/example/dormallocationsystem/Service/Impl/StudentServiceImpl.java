package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.*;
import org.example.dormallocationsystem.Repository.*;
import org.example.dormallocationsystem.Service.IStudentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentServiceImpl implements IStudentService {
    private final StudentRepository studentRepository;
    private final DormUserRepository dormUserRepository;
    private final DormDocumentRepository documentRepository;
    private final RoomRequestRepository roomRequestRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String UPLOAD_DIR = "uploads/";


    public StudentServiceImpl(StudentRepository studentRepository, DormUserRepository dormUserRepository, DormDocumentRepository documentRepository, RoomRequestRepository roomRequestRepository, RoomRepository roomRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.dormUserRepository = dormUserRepository;
        this.documentRepository = documentRepository;
        this.roomRequestRepository = roomRequestRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean registerStudent(String email, String pass, String firstName, String lastName, String phoneNumber, String facultyName, Integer yearOfStudies, String gender) {
        if(dormUserRepository.findByEmail(email).isEmpty()){
            DormUser dormUser = new DormUser();
            dormUser.setEmail(email);
            dormUser.setPass(passwordEncoder.encode(pass));
            dormUser.setFirstName(firstName);
            dormUser.setLastName(lastName);
            dormUser.setPhoneNumber(phoneNumber);
            dormUser = dormUserRepository.save(dormUser);

            Student student = new Student();
            student.setDormUser(dormUser);
            student.setGender(gender);
            student.setFacultyName(facultyName);
            student.setYearOfStudies(yearOfStudies);
            studentRepository.save(student);
            return true;
        }
        return false;
    }
    @Override
    public boolean applyForRoom(Long studentId, String preferredRoom, String roommateEmail) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (studentOpt.isPresent()) {
            String[] parts = preferredRoom.split("-");
            if (parts.length != 2) return false;

            Integer roomNumber;
            try {
                roomNumber = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                return false;
            }
            String blockId = parts[1];

            RoomId roomId = new RoomId(roomNumber, blockId);
            Optional<Room> roomOpt = roomRepository.findById(roomId);

            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                Student student = studentOpt.get();

                RoomrequestId roomrequestId = new RoomrequestId();
                roomrequestId.setRoomNumber(roomNumber);
                roomrequestId.setBlockId(blockId);
                roomrequestId.setStudentId(studentId);

                Roomrequest roomRequest = new Roomrequest();
                roomRequest.setId(roomrequestId); // Set embedded ID
                roomRequest.setStudent(student);
                roomRequest.setRoom(room);
                roomRequest.setRoomateEmail(roommateEmail);
                roomRequest.setRequestedTime(LocalDate.now());
                roomRequest.setStatus("Pending");

                roomRequestRepository.save(roomRequest);
                return true;
            }
        }
        return false;
    }


    @Override
    public Roomrequest getRoomRequestsByStudent(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()) {
            return roomRequestRepository.findByStudent(student.get());
        } else {
            return null;
        }
    }

    @Override
    public List<DormDocument> getDocumentsByStudent(Long studentId) {
        return documentRepository.findByStudentId(studentId);
    }

    @Override
    public DormUser getUserDetails(Long studentId) {
        return dormUserRepository.findById(studentId).orElse(null);
    }

    @Override
    public Student getStudentByEmail(String email) {
        Optional<Student> student =  studentRepository.findByDormUser_Email(email);
        if (student.isPresent()) {
            return student.get();
        }
        return null;
    }

    @Override
    public boolean identicalRoomRequestByStudents(Roomrequest r1, Roomrequest r2) {
        return Objects.equals(r1.getId().getRoomNumber(), r2.getId().getRoomNumber()) &&
                Objects.equals(r1.getId().getBlockId(), r2.getId().getBlockId()) &&
                (
                        (Objects.equals(r1.getRoomateEmail(), r2.getStudent().getDormUser().getEmail()) &&
                                Objects.equals(r2.getRoomateEmail(), r1.getStudent().getDormUser().getEmail()))
                                ||
                                Objects.equals(r1.getRoomateEmail(), r2.getRoomateEmail())
                );
    }

    @Override
    public Long getStudentIdByEmail(String email) {
        Optional<Student> studentOpt = studentRepository.findByDormUser_Email(email);
        return studentOpt.map(Student::getId).orElse(null);
    }

    @Override
    public long getUploadedDocumentsCount(Long studentId) {
        return documentRepository.countByStudentId(studentId);
    }

    @Override
    public boolean uploadDocument(MultipartFile file, Long studentId) {
        try {
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isEmpty()) {
                return false; // Student not found
            }
            Student student = studentOpt.get();

            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file to disk
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            // Save document info to the database
            DormDocument dormDocument = new DormDocument();
            dormDocument.setFilePath(filePath.toString()); // Save file path
            dormDocument.setUploadDate(LocalDate.now());
            dormDocument.setDStatus("Pending"); // Default status
            dormDocument.setStudent(student); // Link document to student

            // ✅ Save the document name from the uploaded file
            dormDocument.setDocumentName(file.getOriginalFilename());

            documentRepository.save(dormDocument);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void submitRoomRequest(Roomrequest roomrequest) {
        roomRequestRepository.save(roomrequest);
    }
}
