package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.DormUser;
import org.example.dormallocationsystem.Repository.DormUserRepository;
import org.example.dormallocationsystem.Repository.EmployeeRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final DormUserRepository dormUserRepository;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(DormUserRepository dormUserRepository, StudentRepository studentRepository, EmployeeRepository employeeRepository) {
        this.dormUserRepository = dormUserRepository;
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        DormUser dormUser = dormUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role;
        if (studentRepository.findById(dormUser.getId()).isPresent()) {
            role = "STUDENT";
        } else if (employeeRepository.findById(dormUser.getId()).isPresent()) {
            role = "EMPLOYEE";
        } else {
            throw new UsernameNotFoundException("User has no role");
        }
        return User.builder()
                .username(dormUser.getEmail())
                .password(dormUser.getPass()) // already encoded in DB
                .roles(role) // STUDENT or EMPLOYEE
                .build();
    }
}
