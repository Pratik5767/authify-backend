package com.spring.authify_backend.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring.authify_backend.entity.User;
import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;
import com.spring.authify_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

	private final UserRepository userRepository;

	@Override
	public ProfileResponse createProfile(ProfileRequest request) {
		User newProfile = convertToEntity(request);
		if (!userRepository.existsByEmail(request.getEmail())) {
			newProfile = userRepository.save(newProfile);			
			return convertToProfileResponse(newProfile);
		}
		
		throw new ResponseStatusException(HttpStatus.CONFLICT, "Email alrealy exists");
	}

	private ProfileResponse convertToProfileResponse(User newProfile) {
		return ProfileResponse.builder()
				.name(newProfile.getName())
				.email(newProfile.getEmail())
				.userId(newProfile.getUserId())
				.isAccountVerified(newProfile.getIsAccountVerified())
				.build();
	}

	private User convertToEntity(ProfileRequest request) {
		return User.builder()
			.email(request.getEmail())
			.userId(UUID.randomUUID().toString())
			.name(request.getName())
			.password(request.getPassword())
			.isAccountVerified(false)
			.resetOtp(null)
			.resetOtpExpireAt(0L)
			.verifyOtp(null)
			.verifyOtpExpireAt(0L)
			.build();
	}
}
