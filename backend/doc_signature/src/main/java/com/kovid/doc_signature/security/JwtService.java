package com.kovid.doc_signature.security;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {


    private static final long EXPIRATION_MILLISECONDS = 24 * 60 * 60 * 1000;

    private final String secret;


    public JwtService(@Value("${app.jwt.secret}") String secret){

        this.secret = secret;

    }


    private SecretKey getKey(){

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );

    }


    public String generateToken(String email){

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_MILLISECONDS)
                )
                .signWith(getKey())
                .compact();

    }


    public boolean isTokenValid(String token){

        try {

            parseClaims(token);

            return true;

        } catch (Exception e) {

            return false;

        }

    }


    public String extractEmail(String token){

        try {

            return parseClaims(token).getSubject();

        } catch (Exception e) {

            return null;

        }

    }


    public long getExpirationSeconds(){

        return EXPIRATION_MILLISECONDS / 1000;

    }


    private Claims parseClaims(String token){

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }


}
