package com.kovid.doc_signature;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
class DocSignatureApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class MailTestConfiguration {

		@Bean
		JavaMailSender javaMailSender() {
			return new JavaMailSenderImpl();
		}
	}

}
