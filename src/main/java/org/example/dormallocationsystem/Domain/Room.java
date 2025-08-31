package org.example.dormallocationsystem.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room", schema = "public")
public class Room {
    @EmbeddedId
    private RoomId id;

    @MapsId("blockId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "block_id", nullable = false, columnDefinition = "CHAR(1)")
    private Block block;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved = false;
}

