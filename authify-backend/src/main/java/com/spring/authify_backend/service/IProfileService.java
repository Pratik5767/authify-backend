package com.spring.authify_backend.service;

import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;

public interface IProfileService {
	
	ProfileResponse createProfile(ProfileRequest request);

}
