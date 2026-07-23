package com.georgia.jeogiyo.user.fixture;

import com.georgia.jeogiyo.user.dto.request.UserSignupRequest;
import com.georgia.jeogiyo.user.dto.request.UserUpdateRequest;

public class UserFix {
	public static UserSignupRequest getUserSignupRequest() {
		return UserSignupRequest.builder()
				.loginId("test1234")
				.password("password1234A@")
				.nickname("nickname")
				.phone("02-000-0000")
				.email("test@email.com")
				.build();
	}
	
	public static UserUpdateRequest getUserUpdateRequest() {
		return new UserUpdateRequest(
				null,
				"02-111-1234",
				"test1234@email.com",
				"password123A@"
		);
	}
	
}
