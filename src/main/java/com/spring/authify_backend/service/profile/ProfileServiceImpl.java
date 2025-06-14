package com.spring.authify_backend.service.profile;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring.authify_backend.entity.UserEntity;
import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;
import com.spring.authify_backend.repository.UserRepository;
import com.spring.authify_backend.service.email.IEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final IEmailService emailService;

	@Override
	public ProfileResponse createProfile(ProfileRequest request) {
		UserEntity newProfile = convertToEntity(request);
		if (!userRepository.existsByEmail(request.getEmail())) {
			newProfile = userRepository.save(newProfile);
			return convertToProfileResponse(newProfile);
		}

		throw new ResponseStatusException(HttpStatus.CONFLICT, "Email alrealy exists");
	}

	@Override
	public ProfileResponse getProfile(String email) {
		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found!"));
		return convertToProfileResponse(existingUser);
	}

	private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
		return ProfileResponse.builder().name(newProfile.getName()).email(newProfile.getEmail())
				.userId(newProfile.getUserId()).isAccountVerified(newProfile.getIsAccountVerified()).build();
	}

	private UserEntity convertToEntity(ProfileRequest request) {
		return UserEntity.builder().email(request.getEmail()).userId(UUID.randomUUID().toString())
				.name(request.getName()).password(passwordEncoder.encode(request.getPassword()))
				.isAccountVerified(false).resetOtp(null).resetOtpExpireAt(0L).verifyOtp(null).verifyOtpExpireAt(0L)
				.build();
	}

	@Override
	public void sendResetOtp(String email) {
		UserEntity existingEntity = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		// generate the 6 digit otp
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

		// calculate expiry time(current time + 15 milliseconds)
		long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

		// update profile entity
		existingEntity.setResetOtp(otp);
		existingEntity.setResetOtpExpireAt(expiryTime);

		// save in db
		userRepository.save(existingEntity);

		try {
			// send the reset otp
			emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);
		} catch (Exception e) {
			throw new RuntimeException("Unable to send email");
		}
	}

	@Override
	public void resetPassword(String email, String otp, String newPassword) {
		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)) {
			throw new RuntimeException("Invalid OTP");
		}

		if (existingUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
			throw new RuntimeException("OTP expired");
		}

		existingUser.setPassword(passwordEncoder.encode(newPassword));
		existingUser.setResetOtp(null);
		existingUser.setResetOtpExpireAt(0L);

		userRepository.save(existingUser);
	}

	@Override
	public void sendOtp(String email) {
		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));

		if (existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()) {
			return;
		}

		// generate 6 digit OTP
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

		// calculate expire time(current time + 24 hours)
		long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

		// update profile entity
		existingUser.setVerifyOtp(otp);
		existingUser.setVerifyOtpExpireAt(expiryTime);

		// save in db
		userRepository.save(existingUser);

		try {
			// send the verify otp
			emailService.sendOtpEmail(existingUser.getEmail(), otp);
		} catch (Exception e) {
			throw new RuntimeException("Unable to send email");
		}
	}

	@Override
	public void verifyOtp(String email, String otp) {
		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
		
		if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
			throw new RuntimeException("OTP Invalid");
		}
		
		if (existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
			throw new RuntimeException("OTP Expired");
		}
		
		existingUser.setIsAccountVerified(true);
		existingUser.setVerifyOtp(null);
		existingUser.setVerifyOtpExpireAt(0L);
		
		userRepository.save(existingUser);
	}
}