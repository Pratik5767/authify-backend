package com.spring.authify_backend.service.email;

public interface IEmailService {
	
	void sendWelcomeEmail(String toEmail, String name);
	
	void sendResetOtpEmail(String toEmail, String otp);
	
	void sendOtpEmail(String toEmail, String otp);

}