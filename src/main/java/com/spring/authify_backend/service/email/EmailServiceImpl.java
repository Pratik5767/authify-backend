package com.spring.authify_backend.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;

	@Override
	public void sendWelcomeEmail(String toEmail, String name) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Welcome to our platform");
		message.setText("Hello " + name + ", \n\nThanks for registering with us!\n\nRegards, \nAuthify Team");
		javaMailSender.send(message);
	}

	@Override
	public void sendResetOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Password Reset OTP");
		message.setText("Your OTP from resetting your password is: " + otp
				+ ".\n\nUse this otp to proceed with resetting your password");
		javaMailSender.send(message);
	}

	@Override
	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Account Verification OTP");
		message.setText("Your OTP is " + otp + " verify your account using this OTP.");
		javaMailSender.send(message);
	}
}