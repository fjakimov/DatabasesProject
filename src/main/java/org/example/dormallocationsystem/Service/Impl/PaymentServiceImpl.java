package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.Payment;
import org.example.dormallocationsystem.Repository.PaymentRepository;
import org.example.dormallocationsystem.Service.IPaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> findByStudentId(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }
}
