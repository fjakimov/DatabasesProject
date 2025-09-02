package org.example.dormallocationsystem.Service.Impl;

import org.example.dormallocationsystem.Domain.DormDocument;
import org.example.dormallocationsystem.Domain.DormUser;
import org.example.dormallocationsystem.Domain.Student;
import org.example.dormallocationsystem.Repository.DormDocumentRepository;
import org.example.dormallocationsystem.Repository.DormUserRepository;
import org.example.dormallocationsystem.Service.IDormDocumentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DormDocumentServiceImpl implements IDormDocumentService {
    private final DormDocumentRepository dormDocumentRepository;

    public DormDocumentServiceImpl(DormDocumentRepository dormDocumentRepository) {
        this.dormDocumentRepository = dormDocumentRepository;
    }

    @Override
    public boolean areAllDocumentsReviewed(Long studentId) {
        List<DormDocument> documents = dormDocumentRepository.findByStudentId(studentId);
        return getUploadedDocumentsCount(studentId) == 5 && documents.stream().noneMatch(dormDocument -> dormDocument.getDStatus().equals("Pending"));
    }

    @Override
    public boolean areAllDocumentsApproved(Long studentId) {
        List<DormDocument> documents = dormDocumentRepository.findByStudentId(studentId);
        return getUploadedDocumentsCount(studentId) == 5 && documents.stream().allMatch(dormDocument -> dormDocument.getDStatus().equals("Approved"));
    }

    @Override
    public long getUploadedDocumentsCount(Long studentId) {
        return dormDocumentRepository.countByStudentId(studentId);
    }

    @Override
    public DormDocument getDocumentById(Long id) {
        return dormDocumentRepository.findById(id).get();
    }
}
