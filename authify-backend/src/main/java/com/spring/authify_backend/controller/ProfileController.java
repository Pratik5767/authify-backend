package com.spring.authify_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;
import com.spring.authify_backend.service.IProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

	private final IProfileService profileService;
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
		ProfileResponse response = profileService.createProfile(request);
		// send the welcome email
		return response;
	}
	
}
