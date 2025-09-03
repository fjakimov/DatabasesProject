package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.Payment;

import java.util.List;

public interface IPaymentService {
    List<Payment> findByStudentId(Long studentId);
    void recordPayment(Long studentId, List<String> paymentMonths);
}
