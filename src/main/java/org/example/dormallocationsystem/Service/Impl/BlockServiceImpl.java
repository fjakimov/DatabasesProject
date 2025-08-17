package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.Block;
import org.example.dormallocationsystem.Repository.BlockRepository;
import org.example.dormallocationsystem.Service.IBlockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockServiceImpl implements IBlockService {
    private final BlockRepository blockRepository;

    public BlockServiceImpl(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }
    @Override
    public List<Block> getAll() {
        return blockRepository.findAll();
    }
}
