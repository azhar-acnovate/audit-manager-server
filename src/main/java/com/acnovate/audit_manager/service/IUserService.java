package com.acnovate.audit_manager.service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.common.interfaces.IService;
import com.acnovate.audit_manager.domain.User;

public interface IUserService extends IService<User> {
	User findByUserName(String username);

	LoggedInUserDetails getLoggedInUserDetails(User resource);
}
