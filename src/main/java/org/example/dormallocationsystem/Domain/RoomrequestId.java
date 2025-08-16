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
public class RoomrequestId implements Serializable {
    private static final long serialVersionUID = 1629239276690490525L;
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @Column(name = "block_id", nullable = false, columnDefinition = "CHAR(1)")
    private String blockId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoomrequestId entity = (RoomrequestId) o;
        return Objects.equals(this.blockId, entity.blockId) &&
                Objects.equals(this.studentId, entity.studentId) &&
                Objects.equals(this.roomNumber, entity.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockId, studentId, roomNumber);
    }

}