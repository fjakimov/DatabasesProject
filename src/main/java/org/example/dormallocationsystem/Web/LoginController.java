package org.example.dormallocationsystem.Web;

import org.example.dormallocationsystem.Domain.DormUser;
import org.example.dormallocationsystem.Repository.DormUserRepository;
import org.example.dormallocationsystem.Repository.EmployeeRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.example.dormallocationsystem.Service.IStudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    private final DormUserRepository dormUserRepository;
    private final IStudentService studentService;

    public LoginController(StudentRepository studentRepository,
                           EmployeeRepository employeeRepository,
                           DormUserRepository dormUserRepository, IStudentService studentService) {
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
        this.dormUserRepository = dormUserRepository;
        this.studentService = studentService;
    }
    @GetMapping("/post-login")
    public String redirectAfterLogin(Authentication authentication) {
        String email = authentication.getName();
        DormUser dormUser = dormUserRepository.findByEmail(email).orElseThrow();
        if(studentRepository.findById(dormUser.getId()).isPresent()) {
            long documentCount = studentService.getUploadedDocumentsCount(dormUser.getId());
            if( documentCount > 5){
                return "redirect:/dashboard?studentId=" + dormUser.getId();
            }
            else {
                return "redirect:/upload-documents?studentId=" + dormUser.getId();
            }
        } else if (employeeRepository.findById(dormUser.getId()).isPresent()){
            return "redirect:/employee/dashboard";
        }
        return "redirect:/";
    }
}
