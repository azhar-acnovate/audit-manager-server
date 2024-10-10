package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.dto.response.UserResponseDto;

public interface IUserService extends IService<User> {
	UserResponseDto domainToDto(User user);

	User findByUserName(String username);

	LoggedInUserDetails getLoggedInUserDetails(User resource);

	UserResponseDto createUser(UserRequestDto userRequestDto);
}
