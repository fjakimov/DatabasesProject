package org.example.dormallocationsystem.Service;

import org.example.dormallocationsystem.Domain.DormDocument;

public interface IDormDocumentService {
    boolean areAllDocumentsReviewed(Long studentId);
    boolean areAllDocumentsApproved(Long studentId);
    long getUploadedDocumentsCount(Long studentId);
    DormDocument getDocumentById(Long id);
}
