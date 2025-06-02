package com.spring.authify_backend.service.profile;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring.authify_backend.entity.UserEntity;
import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;
import com.spring.authify_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

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
}
