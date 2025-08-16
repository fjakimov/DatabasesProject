package org.example.dormallocationsystem.Web;

import org.example.dormallocationsystem.Domain.DormDocument;
import org.example.dormallocationsystem.Domain.RoomId;
import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Domain.Student;
import org.example.dormallocationsystem.Service.IEmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(Model model) {
        List<Student> studentsWithDocuments = employeeService.getStudentsWithDocuments();
        model.addAttribute("students", studentsWithDocuments);
         return "employee-dashboard";
    }

    @GetMapping("/view-student")
    public String viewStudentDetails(@RequestParam Long studentId, Model model) {
        List<DormDocument> studentDocuments = employeeService.getDocumentsByStudent(studentId);
        List<Roomrequest> studentRoomRequests = employeeService.getRoomRequestsByStudent(studentId);
        boolean allDocsValidated = employeeService.areAllDocumentsValidated(studentId); // Check validation status

        model.addAttribute("studentId", studentId);
        model.addAttribute("documents", studentDocuments);
        model.addAttribute("roomRequests", studentRoomRequests);
        model.addAttribute("allDocsValidated", allDocsValidated);

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

    @PostMapping("/validate-document")
    public String validateDocument(@RequestParam Long documentId, @RequestParam Long studentId) {
        employeeService.validateDocument(documentId);
        return "redirect:/employee/view-student?studentId=" + studentId;
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
