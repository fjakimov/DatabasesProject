package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "studenttookroom", schema = "project")
public class Studenttookroom {
    @EmbeddedId
    private StudenttookroomId id;

    @MapsId("studentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "end_stay_requested")
    private Boolean endStayRequested = false;

    @Column(name = "requested_end_date")
    private LocalDate requestedEndDate;
}