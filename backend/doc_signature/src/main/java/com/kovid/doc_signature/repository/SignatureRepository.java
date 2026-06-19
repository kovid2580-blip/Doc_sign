package com.kovid.doc_signature.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kovid.doc_signature.model.Signature;

public interface SignatureRepository extends JpaRepository<Signature, Long> {

    List<Signature> findByDocumentId(Long documentId);

    List<Signature> findBySignerId(Long userId);
}
