package com.kovid.doc_signature.service;

import com.kovid.doc_signature.model.AuditLog;
import com.kovid.doc_signature.model.Document;
import com.kovid.doc_signature.model.User;
import com.kovid.doc_signature.repository.AuditLogRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository){
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String action, User user, Document document){

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .user(user)
                .document(document)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getLogsByDocument(Long documentId){

        return auditLogRepository.findByDocumentId(documentId);
    }
}