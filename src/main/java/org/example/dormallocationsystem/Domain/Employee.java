package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "employee", schema = "project")
public class Employee {
    @Id
    @Column(name = "u_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "u_id", nullable = false)
    private DormUser dormUser;
    @OneToMany(mappedBy = "employee")
    private List<DormDocument> checkedDocuments = new ArrayList<>();
}