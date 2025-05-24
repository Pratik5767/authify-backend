package com.spring.authify_backend.service.profile;

import com.spring.authify_backend.io.ProfileRequest;
import com.spring.authify_backend.io.ProfileResponse;

public interface IProfileService {
	
	ProfileResponse createProfile(ProfileRequest request);

}
