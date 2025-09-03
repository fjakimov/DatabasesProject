package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "payment", schema = "project")
public class Payment {
    @Id
    @Column(name = "p_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "payment_month", nullable = false, length = 15)
    private String paymentMonth;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}