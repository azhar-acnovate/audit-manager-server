package com.acnovate.audit_manager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.repository.UserRepository;
import com.acnovate.audit_manager.service.IUserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl extends AbstractRawService<User> implements IUserService {
	@Autowired
	private UserRepository repo;

	@Override
	protected JpaRepository<User, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<User> getSpecificationExecutor() {
		return repo;
	}

	@Override
	public User findByUserName(String username) {
		return repo.findByUserName(username);
	}

	@Override
	public LoggedInUserDetails getLoggedInUserDetails(User resource) {
		LoggedInUserDetails response = new LoggedInUserDetails();
		response.setId(resource.getId());
		response.setUserName(resource.getUserName());
		response.setProfileImageName(resource.getProfileImageName());
		return response;
	}

}
