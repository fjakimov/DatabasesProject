package org.example.dormallocationsystem.Web;

import org.example.dormallocationsystem.Domain.DormDocument;
import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Service.IRoomRequestService;
import org.example.dormallocationsystem.Service.IStudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/")
public class StudentController {
    private final IStudentService studentService;
    private final IRoomRequestService roomRequestService;

    public StudentController(IStudentService studentService, IRoomRequestService roomRequestService) {
        this.studentService = studentService;
        this.roomRequestService = roomRequestService;
    }

    @GetMapping("/dashboard")
    public String studentDashboard(@RequestParam Long studentId, Model model) {
        Roomrequest roomrequest = roomRequestService.findRoomRequestForStudent(studentId);
        List<DormDocument> documents = studentService.getDocumentsByStudent(studentId);
        // TODO: IMPROVE THE STUDENT DASHBOARD
        model.addAttribute("studentId", studentId);
        model.addAttribute("roomRequest", roomrequest);
        model.addAttribute("documents", documents);

        return "student-dashboard";
    }

    @GetMapping("/upload-documents")
    public String showUploadDocumentsForm(@RequestParam("studentId") Long studentId, Model model) {

        //TODO: MAKE BUTTONS DISABLED IF DOCS NOT ADDED
        model.addAttribute("studentId", studentId);
        return "upload-documents";
    }

    @PostMapping("/upload-documents")
    public String uploadDocuments(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("studentId") Long studentId,
            @RequestParam(value = "next", required = false) boolean next,
            Model model) {

        if (files.length < 5) {
            model.addAttribute("error", "You must upload at least 5 documents before proceeding.");
            return "upload-documents";
        }

        boolean allUploaded = true;
        for (MultipartFile file : files) {
            if (!studentService.uploadDocument(file, studentId)) {
                allUploaded = false;
                break;
            }
        }

        if (allUploaded) {
            return next ? "redirect:/choose-room?studentId=" + studentId : "redirect:/dashboard?studentId="+ studentId;
        } else {
            model.addAttribute("error", "Failed to upload one or more documents.");
            return "upload-documents";
        }
    }



    @GetMapping("/choose-room")
    public String showRoomSelectionForm(@RequestParam("studentId") Long studentId, Model model) {
        model.addAttribute("studentId", studentId);
        return "choose-room";
    }
    @PostMapping("/apply-room")
    public String applyForRoom(
            @RequestParam Long studentId,
            @RequestParam String preferredRoom,
            @RequestParam(required = false) String roommateEmail,
            Model model) {

        boolean success = studentService.applyForRoom(studentId, preferredRoom, roommateEmail);
        if (success) {
            model.addAttribute("studentId", studentId);
            model.addAttribute("preferredRoom", preferredRoom);
            return "application-confirmation";
        } else {
            model.addAttribute("error", "Room request failed.");
            return "choose-room";
        }
    }
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @RequestParam String facultyName,
            @RequestParam Integer yearOfStudies,
            @RequestParam String gender,
            Model model) {

        boolean success = studentService.registerStudent(email, password, firstName, lastName, phoneNumber, facultyName, yearOfStudies, gender);

        if (success) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Registration failed. Email might already be in use.");
            return "register";
        }
    }
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout, Model model) {
        if(error != null) {
            model.addAttribute("errorMessage", "Invalid email or password!");
            return "redirect:/login";
        }
        if(logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully");
            return "redirect:/login";
        }
        return "login";
    }
}
