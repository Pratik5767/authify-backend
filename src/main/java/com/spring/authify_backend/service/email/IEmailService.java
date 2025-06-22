package com.spring.authify_backend.service.email;

import jakarta.mail.MessagingException;

public interface IEmailService {
	
	void sendWelcomeEmail(String toEmail, String name);
	
	void sendResetOtpEmail(String toEmail, String otp) throws MessagingException;
	
	void sendOtpEmail(String toEmail, String otp) throws MessagingException;

}