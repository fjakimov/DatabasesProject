package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "roomrequest", schema = "public")
public class Roomrequest {
    @EmbeddedId
    private RoomrequestId id;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "room_number", referencedColumnName = "room_number", nullable = false),
            @JoinColumn(name = "block_id", referencedColumnName = "block_id", nullable = false, columnDefinition = "CHAR(1)")
    })
    private Room room;

    @MapsId("studentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "roomate_email", length = 1000)
    private String roomateEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "requested_time", nullable = false)
    private LocalDate requestedTime;

}