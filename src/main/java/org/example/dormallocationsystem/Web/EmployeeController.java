package org.example.dormallocationsystem.Web;

import org.example.dormallocationsystem.Domain.*;
import org.example.dormallocationsystem.Repository.BlockRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.example.dormallocationsystem.Service.IBlockService;
import org.example.dormallocationsystem.Service.IEmployeeService;
import org.example.dormallocationsystem.Service.IStudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final IEmployeeService employeeService;
    private final StudentRepository studentRepository;
    private final IStudentService studentService;
    private final IBlockService blockService;
    public EmployeeController(IEmployeeService employeeService,
                              StudentRepository studentRepository, BlockRepository blockRepository, IStudentService studentService, IBlockService blockService) {
        this.employeeService = employeeService;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        this.blockService = blockService;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(@RequestParam Long employeeId, Model model) {
        // TODO: MAKE IT A SCREEN WITH OPTION TO VIEW THE APPROVED STUDENTS,
        //  STUDENTS THAT NEED TO BE APPROVED,
        //  ALSO VIEW THE AVAILABLE ROOMS IN THE BLOCKS
        List<Student> studentsWithDocuments = employeeService.getStudentsWithDocuments();
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("students", studentsWithDocuments);
         return "employee-dashboard";
    }

    @GetMapping("/view-student")
    public String viewStudentDetails(@RequestParam Long studentId, @RequestParam Long employeeId, Model model) {
        Student student = studentRepository.findById(studentId).orElse(null);
        DormUser studentDetails = studentService.getUserDetails(studentId);
        List<DormDocument> documentsToValidate = employeeService.viewDocumentsToValidate(student);
        List<DormDocument> reviewedDocuments = employeeService.getReviewedDocumentsByStudent(studentId);
        List<Roomrequest> studentRoomRequests = employeeService.getRoomRequestsByStudent(studentId);
        boolean allDocsReviewed = employeeService.areAllDocumentsReviewed(studentId);
        boolean allDocsApproved = employeeService.areAllDocumentsApproved(studentId);
        //TODO: CHECK IF THE DOCS ARE ALREADY VALIDATED + ROOM IS APPROVED/GIVEN
        model.addAttribute("studentId", studentId);
        model.addAttribute("fullName", studentDetails.getFirstName() + " " + studentDetails.getLastName());
        model.addAttribute("documentsToValidate", documentsToValidate);
        model.addAttribute("reviewedDocuments", reviewedDocuments);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("roomRequests", studentRoomRequests);
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
    public String getRoomRequest(@RequestParam Long studentId, Model model) {
        List<Roomrequest> studentRoomRequests = employeeService.getRoomRequestsByStudent(studentId);
        model.addAttribute("blocks", blockService.getAll());
        model.addAttribute("roomRequests", studentRoomRequests);
        return "room-request";
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
    public String addComment(@RequestParam Long documentId, @RequestParam String comment, @RequestParam Long studentId) {
        employeeService.addDocumentComment(documentId, comment);
        return "redirect:/employee/view-student?studentId=" + studentId;
    }

    @PostMapping("/assign-room")
    public String assignRoomToStudent(@RequestParam Long studentId, @RequestParam String roomId, Model model) {
        String[] parts = roomId.split("-");
        if (parts.length != 2) {
            model.addAttribute("error", "Invalid room format. Use '101-A'");
            return "employee-dashboard";
        }

        Integer roomNumber;
        try {
            roomNumber = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid room number.");
            return "employee-dashboard";
        }

        String blockId = parts[1];
        RoomId roomIdObj = new RoomId(roomNumber, blockId);
        employeeService.assignRoomToStudent(studentId, roomIdObj);
        return "redirect:/employee/dashboard";
    }
}
