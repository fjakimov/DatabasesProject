package org.example.dormallocationsystem.Web;

import org.example.dormallocationsystem.Domain.DormDocument;
import org.example.dormallocationsystem.Domain.Roomrequest;
import org.example.dormallocationsystem.Domain.Studenttookroom;
import org.example.dormallocationsystem.Service.IRoomRequestService;
import org.example.dormallocationsystem.Service.IStudentService;
import org.example.dormallocationsystem.Service.IStudentTookRoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/")
public class StudentController {
    private final IStudentService studentService;
    private final IRoomRequestService roomRequestService;
    private final IStudentTookRoomService studentTookRoomService;

    public StudentController(IStudentService studentService, IRoomRequestService roomRequestService, IStudentTookRoomService studentTookRoomService) {
        this.studentService = studentService;
        this.roomRequestService = roomRequestService;
        this.studentTookRoomService = studentTookRoomService;
    }

    @GetMapping("/dashboard")
    public String studentDashboard(@RequestParam Long studentId, Model model) {
        Roomrequest roomrequest = roomRequestService.findRoomRequestForStudent(studentId);
        Studenttookroom studenttookroom = studentTookRoomService.getStudentInRoom(studentId);
        boolean differentRoomAdded = roomrequest != null && studenttookroom != null &&
                !Objects.equals(studenttookroom.getId().getRoomNum(), roomrequest.getId().getRoomNumber());
        List<DormDocument> documents = studentService.getDocumentsByStudent(studentId);
        model.addAttribute("studentId", studentId);
        if (roomrequest != null) {
            model.addAttribute("roomRequest", roomrequest);
        }
        if (studenttookroom != null) {
            model.addAttribute("studentRoom", studenttookroom);
        }
        model.addAttribute("documents", documents);
        model.addAttribute("differentRoomAdded", differentRoomAdded);
        return "student-dashboard";
    }

    @GetMapping("/upload-documents")
    public String showUploadDocumentsForm(@RequestParam("studentId") Long studentId, Model model) {
        List<DormDocument> documents = studentService.getDocumentsByStudent(studentId);
        model.addAttribute("studentId", studentId);

        if (documents != null && !documents.isEmpty()) {
            model.addAttribute("documents", documents);
            return "upload-summary";
        }
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

    @PostMapping("/request-end-stay")
    public String requestEndStay(@RequestParam Long studentId,
                                 @RequestParam("requestedEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestedEndDate) {
        studentTookRoomService.createEndStayRequest(studentId, requestedEndDate);
        return "redirect:/dashboard?studentId=" + studentId;
    }
}
