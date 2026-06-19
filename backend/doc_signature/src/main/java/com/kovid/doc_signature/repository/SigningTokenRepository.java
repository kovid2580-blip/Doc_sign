package com.kovid.doc_signature.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kovid.doc_signature.model.SigningToken;

public interface SigningTokenRepository extends JpaRepository<SigningToken, Long> {

    Optional<SigningToken> findByToken(String token);

}