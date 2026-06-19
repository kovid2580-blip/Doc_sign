package com.kovid.doc_signature.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kovid.doc_signature.service.SigningLinkService;

@RestController
@RequestMapping("/api/sign-links")
public class SigningLinkController {

    private final SigningLinkService signingLinkService;


    public SigningLinkController(
            SigningLinkService signingLinkService
    ){
        this.signingLinkService = signingLinkService;
    }


    @PostMapping("/generate")
    public ResponseEntity<Map<String,String>> generateLink(
            @RequestParam Long documentId,
            @RequestParam String signerEmail
    ){

        String link =
                signingLinkService.generateSigningLink(
                        documentId,
                        signerEmail
                );


        return ResponseEntity.ok(
                Map.of("signingLink", link)
        );

    }

}