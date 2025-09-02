package org.example.dormallocationsystem.Web;

import org.example.dormallocationsystem.Domain.*;
import org.example.dormallocationsystem.Repository.BlockRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.example.dormallocationsystem.Service.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final IEmployeeService employeeService;
    private final StudentRepository studentRepository;
    private final IRoomRequestService roomRequestService;
    private final IRoomService roomService;
    private final IStudentService studentService;
    private final IBlockService blockService;
    private final IStudentTookRoomService studentTookRoomService;
    private final IDormDocumentService dormDocumentService;
    public EmployeeController(IEmployeeService employeeService,
                              StudentRepository studentRepository, BlockRepository blockRepository, IRoomRequestService roomRequestService, IRoomService roomService, IStudentService studentService, IBlockService blockService, IStudentTookRoomService studentTookRoomService, IDormDocumentService dormDocumentService) {
        this.employeeService = employeeService;
        this.studentRepository = studentRepository;
        this.roomRequestService = roomRequestService;
        this.roomService = roomService;
        this.studentService = studentService;
        this.blockService = blockService;
        this.studentTookRoomService = studentTookRoomService;
        this.dormDocumentService = dormDocumentService;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(@RequestParam Long employeeId, Model model) {
        List<Student> studentsAddedToRoom = studentService.getStudentsAddedToRoom();
        List<Student> studentsThatNeedToBeReviewed = studentService.getStudentsWithNotReviewedDocs();
        List<Studenttookroom> endStayRequests = studentTookRoomService.getPendingEndStayRequests();
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("addedToRoomStudents", studentsAddedToRoom);
        model.addAttribute("studentsToReview", studentsThatNeedToBeReviewed);
        model.addAttribute("endStayRequests", endStayRequests);
        return "employee-dashboard";
    }

    @GetMapping("/view-student")
    public String viewStudentDetails(@RequestParam Long studentId, @RequestParam Long employeeId, Model model) {
        Student student = studentRepository.findById(studentId).orElse(null);
        DormUser studentDetails = studentService.getUserDetails(studentId);
        List<DormDocument> documentsToValidate = employeeService.viewDocumentsToValidate(student);
        List<DormDocument> reviewedDocuments = employeeService.getReviewedDocumentsByStudent(studentId);
        Roomrequest studentRoomRequest = roomRequestService.findRoomRequestForStudent(studentId);
        if (studentRoomRequest != null) {
            Student roommate = studentService.getStudentByEmail(studentRoomRequest.getRoomateEmail());
            if (roommate != null) {
                Roomrequest roommateRoomRequest = roomRequestService.findRoomRequestForStudent(roommate.getId());
                Studenttookroom studenttookroom = studentTookRoomService.getStudentInRoom(roommate.getId());
                if (roommateRoomRequest != null) {
                    boolean identicalRoomRequests = studentService.identicalRoomRequestByStudents(studentRoomRequest, roommateRoomRequest);
                    boolean areAllRoommatesDocsApproved = dormDocumentService.areAllDocumentsApproved(roommate.getId());
                    boolean areAllRoommateDocsReviewed = dormDocumentService.areAllDocumentsReviewed(roommate.getId());
                    System.out.println(areAllRoommateDocsReviewed);
                    System.out.println(areAllRoommatesDocsApproved);
                    model.addAttribute("areAllRoommatesDocsApproved", areAllRoommatesDocsApproved);
                    model.addAttribute("areAllRoommatesDocsReviewed", areAllRoommateDocsReviewed);
                    model.addAttribute("roommateRoomRequest", roommateRoomRequest);
                    model.addAttribute("roomRequest", studentRoomRequest);
                    model.addAttribute("identicalRoomRequests", identicalRoomRequests);
                }
                if ( studenttookroom != null ){
                    model.addAttribute("studentIsInRoom", studenttookroom);
                    model.addAttribute("roommatesRoom", studenttookroom.getId().getRoomNum());
                    model.addAttribute("roommatesBlock", studenttookroom.getId().getBlockId());
                }
                model.addAttribute("roommateEmail", roommate.getDormUser().getEmail());
            }
            System.out.println(studentRoomRequest);
            model.addAttribute("studentRoomRequest", studentRoomRequest);
            model.addAttribute("roomRequest", studentRoomRequest);
        }
        boolean allDocsReviewed = dormDocumentService.areAllDocumentsReviewed(studentId);
        boolean allDocsApproved = dormDocumentService.areAllDocumentsApproved(studentId);
        Studenttookroom str = studentTookRoomService.getStudentInRoom(student.getId());
        if ( str != null) {
            model.addAttribute("studentAddedRoom", str);
        }
        model.addAttribute("studentId", studentId);
        model.addAttribute("fullName", studentDetails.getFirstName() + " " + studentDetails.getLastName());
        model.addAttribute("documentsToValidate", documentsToValidate);
        model.addAttribute("reviewedDocuments", reviewedDocuments);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("allDocsReviewed", allDocsReviewed);
        model.addAttribute("allDocsApproved", allDocsApproved);

        return "student-details";
    }

    @GetMapping("/register")
    public String registerEmployee() {
        return "employee-register";
    }
    @PostMapping("/register")
    public String registerEmployee(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            Model model) {

        boolean success = employeeService.register(email, password, firstName, lastName, phoneNumber);

        if (success) {
            return "redirect:/";
        } else {
            model.addAttribute("error", "Registration failed. Email might already be in use.");
            return "register";
        }
    }

    @GetMapping("/room-request")
    public String getRoomRequest(@RequestParam Long studentId, @RequestParam Long employeeId, Model model) {
        Roomrequest studentRoomRequests = roomRequestService.findRoomRequestForStudent(studentId);
        model.addAttribute("blocks", blockService.getAll());
        model.addAttribute("studentId", studentId);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("roomRequest", studentRoomRequests);
        return "room-request";
    }

    @GetMapping("/view-rooms")
    public String viewRoomsPerFloor(@RequestParam Long studentId, @RequestParam Integer floorNumber, @RequestParam String blockId, @RequestParam Long employeeId, Model model) {
        List<Room> roomsPerFloor = roomService.getRoomsInFloor(blockId, floorNumber);
        Roomrequest roomrequest = roomRequestService.findRoomRequestForStudent(studentId);
        Student roommate = studentService.getStudentByEmail(roomrequest.getRoomateEmail());
        if (roommate != null) {
            Roomrequest roommatesRoomRequest = roomRequestService.findRoomRequestForStudent(roommate.getId());
            if (roommatesRoomRequest != null) {
                boolean identicalRoomRequests = studentService.identicalRoomRequestByStudents(roomrequest, roommatesRoomRequest);
                if (identicalRoomRequests) {
                    Studenttookroom str = studentTookRoomService.getStudentInRoom(roommate.getId());
                    if (str != null){
                        model.addAttribute("str", str);
                    }
                }
            }
        }
        if (roomrequest != null) {
            model.addAttribute("roomRequest", roomrequest);
        }
        model.addAttribute("roomsPerFloor", roomsPerFloor);
        model.addAttribute("studentId", studentId);
        model.addAttribute("employeeId", employeeId);
        return "view-rooms";
    }

    @GetMapping("/view-floors")
    public String viewFloorsForBlock(@RequestParam String blockId, @RequestParam Long studentId, @RequestParam Long employeeId, Model model) {
        Set<Integer> allFloors = roomService.getAllFloors(blockId);
        Map<Integer, Long> takenRooms = roomService.getTakenRoomsPerFloorInBlock(blockId);
        Roomrequest roomrequest = roomRequestService.findRoomRequestForStudent(studentId);
        if (roomrequest != null) {
            model.addAttribute("roomRequest", roomrequest);
        }
        Map<Integer, Double> floorCapacityPercentage = roomService.getFloorCapacityPercentage(blockId);
        model.addAttribute("allFloors", allFloors);
        model.addAttribute("takenRooms", takenRooms);
        model.addAttribute("blockId", blockId);
        model.addAttribute("studentId", studentId);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("floorCapacityPercentage", floorCapacityPercentage);
        return "view-floors";
    }

    @PostMapping("/approve-document")
    public String approveDocument(@RequestParam Long documentId, @RequestParam Long studentId, @RequestParam Long employeeId) {
        employeeService.approveDocument(documentId, employeeId);
        return "redirect:/employee/view-student?studentId=" + studentId + "&employeeId=" + employeeId;
    }

    @PostMapping("/decline-document")
    public String declineDocument(@RequestParam Long documentId, @RequestParam Long studentId, @RequestParam Long employeeId) {
        employeeService.declineDocument(documentId, employeeId);
        return "redirect:/employee/view-student?studentId=" + studentId + "&employeeId=" + employeeId;
    }

    @PostMapping("/add-comment")
    public String addComment(@RequestParam Long documentId, @RequestParam String comment, @RequestParam Long studentId, @RequestParam Long employeeId) {
        employeeService.addDocumentComment(documentId, comment);
        return "redirect:/employee/view-student?studentId=" + studentId + "&employeeId=" + employeeId;
    }

    @GetMapping("/download-document")
    public ResponseEntity<Resource> downloadDocument(@RequestParam Long documentId) {
        DormDocument document = dormDocumentService.getDocumentById(documentId);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getDocumentName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/assign-room")
    public String assignRoomToStudent(@RequestParam Long studentId, @RequestParam Integer roomNumber, @RequestParam String blockId, @RequestParam Long employeeId, Model model) {
        RoomId roomId = new RoomId(roomNumber, blockId);
        employeeService.assignRoomToStudent(studentId, roomId);
        return "redirect:/employee/dashboard?employeeId=" + employeeId;
    }

    @PostMapping("/approve-end-stay")
    public String approveEndStay(
            @RequestParam Long studentId,
            @RequestParam Long employeeId) {

        studentTookRoomService.approveEndStay(studentId);

        return "redirect:/employee/dashboard?employeeId=" + employeeId;
    }
}
