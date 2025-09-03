package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.Block;
import org.example.dormallocationsystem.Domain.DTO.BlockRoomReportDTO;

import java.util.List;

public interface IBlockService {
    List<Block> getAll();
    List<BlockRoomReportDTO> getBlockRoomReport();
}
