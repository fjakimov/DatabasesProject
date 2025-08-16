package org.example.dormallocationsystem.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class StudenttookroomId implements Serializable {
    private static final long serialVersionUID = -5397392239603341049L;
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "room_num", nullable = false)
    private Integer roomNum;

    @Column(name = "block_id", nullable = false, columnDefinition = "CHAR(1)")
    private String blockId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudenttookroomId entity = (StudenttookroomId) o;
        return Objects.equals(this.studentId, entity.studentId) &&
                Objects.equals(this.blockId, entity.blockId) &&
                Objects.equals(this.roomNum, entity.roomNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, blockId, roomNum);
    }

}