package com.kovid.doc_signature.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;


@Service
public class EmailService {


    private final JavaMailSender mailSender;


    public EmailService(JavaMailSender mailSender){

        this.mailSender = mailSender;

    }


    public void sendSigningEmail(
            String to,
            String link
    ){


        SimpleMailMessage message =
                new SimpleMailMessage();


        message.setTo(to);

        message.setSubject(
                "Document Signature Request"
        );


        message.setText(
                "Hello,\n\n"
                +
                "You have a document waiting for signature.\n\n"
                +
                "Click below:\n"
                +
                link
                +
                "\n\nThank you."
        );


        mailSender.send(message);

    }


}