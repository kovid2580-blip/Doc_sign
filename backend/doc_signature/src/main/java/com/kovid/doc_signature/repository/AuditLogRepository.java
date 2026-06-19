package com.kovid.doc_signature.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kovid.doc_signature.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByDocumentId(Long documentId);

}