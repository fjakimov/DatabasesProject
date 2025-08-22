package org.example.dormallocationsystem.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "block", schema = "public")
public class Block {
    @Id
    @Column(name = "block_id", nullable = false, columnDefinition = "CHAR(1)")
    private String blockId;
    @Column(name = "num_available_rooms", nullable = false)
    private Integer numAvailableRooms;
    @Column(name = "num_total_rooms", nullable = false)
    private Integer numTotalRooms;
}