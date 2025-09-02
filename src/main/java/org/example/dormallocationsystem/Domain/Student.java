package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "student", schema = "project")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "u_id", nullable = false)
    private DormUser dormUser;

    @Column(name = "faculty_name", nullable = false)
    private String facultyName;

    @Column(name = "year_of_studies", nullable = false)
    private Integer yearOfStudies;

    @Column(name = "gender", nullable = false, length = Integer.MAX_VALUE)
    private String gender;

    @Column(name = "is_exempt", nullable = false)
    private Boolean isExempt = false;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Payment> paymentList;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<DormDocument> documents = new ArrayList<>();

}