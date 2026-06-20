package com.kovid.doc_signature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class DocSignatureApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocSignatureApplication.class, args);
	}

}
