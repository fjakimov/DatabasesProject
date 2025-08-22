package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dorm_document", schema = "public")
public class DormDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "d_id", nullable = false)
    private Long id;

    @Column(name = "d_name", nullable = false)
    private String documentName;

    @Column(name = "d_comment", length = 1000)
    private String dComment;

    @Column(name = "d_status", nullable = false, length = 10)
    private String dStatus;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = true)
    private Employee employee;
}