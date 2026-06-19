package com.kovid.doc_signature.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kovid.doc_signature.model.AuditLog;
import com.kovid.doc_signature.service.AuditLogService;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService){
        this.auditLogService = auditLogService;
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @PathVariable Long documentId
    ){
        return ResponseEntity.ok(
                auditLogService.getLogsByDocument(documentId)
        );
    }
}