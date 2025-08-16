package org.example.dormallocationsystem.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@EqualsAndHashCode
public class RoomId implements Serializable {
    private static final long serialVersionUID = 6753443220311200943L;

    @Column(name = "room_number", nullable = false)
    private final Integer roomNumber;

    @Column(name = "block_id", nullable = false, columnDefinition = "CHAR(1)")
    private final String blockId;

    protected RoomId() {
        this.roomNumber = null;
        this.blockId = null;
    }

    public RoomId(Integer roomNumber, String blockId) {
        this.roomNumber = roomNumber;
        this.blockId = blockId;
    }
}
