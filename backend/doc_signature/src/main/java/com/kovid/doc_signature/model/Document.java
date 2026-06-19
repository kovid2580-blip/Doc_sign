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
@Table(name = "documents")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Document {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fileName;


    private String filePath;


    private LocalDateTime uploadedAt;


    private String status;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;


}