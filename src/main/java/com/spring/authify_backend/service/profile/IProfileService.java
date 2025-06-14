package com.spring.authify_backend.service.profile;

import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;

public interface IProfileService {
	
	ProfileResponse createProfile(ProfileRequest request);
	
	ProfileResponse getProfile(String email);

	void sendResetOtp(String email);
	
	void resetPassword(String email, String otp, String newPassword);
	
	void sendOtp(String email);
	
	void verifyOtp(String email, String otp);
}