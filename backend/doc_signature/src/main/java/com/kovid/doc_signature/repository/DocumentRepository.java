package com.kovid.doc_signature.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kovid.doc_signature.model.Document;


public interface DocumentRepository 
        extends JpaRepository<Document, Long> {


    List<Document> findByOwnerId(Long userId);


}