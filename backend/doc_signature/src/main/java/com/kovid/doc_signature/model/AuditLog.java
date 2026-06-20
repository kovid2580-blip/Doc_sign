package com.kovid.doc_signature.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "audit_logs")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuditLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String action;


    private LocalDateTime timestamp;



    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private User user;



    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;


}
    
