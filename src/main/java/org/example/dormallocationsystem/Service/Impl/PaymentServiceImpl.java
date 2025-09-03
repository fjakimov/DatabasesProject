package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.Payment;
import org.example.dormallocationsystem.Domain.Student;
import org.example.dormallocationsystem.Repository.PaymentRepository;
import org.example.dormallocationsystem.Repository.StudentRepository;
import org.example.dormallocationsystem.Service.IPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Payment> findByStudentId(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    @Transactional
    @Override
    public void recordPayment(Long studentId, List<String> paymentMonths) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        for (String month : paymentMonths) {
            Payment payment = new Payment();
            payment.setAmount(50);
            payment.setPaymentDate(LocalDate.now());
            payment.setPaymentMonth(month);
            payment.setStudent(student);
            paymentRepository.save(payment);
        }
    }
}
