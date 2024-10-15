package com.acnovate.audit_manager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.dto.response.UserResponseDto;
import com.acnovate.audit_manager.repository.UserRepository;
import com.acnovate.audit_manager.service.IUserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl extends AbstractRawService<User> implements IUserService {
	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder passwordEncoder;

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
		// response.setProfileImageName(resource.getProfileImageName());
		return response;
	}

	@Override
	public UserResponseDto domainToDto(User user) {
		UserResponseDto response = new UserResponseDto();
		response.setId(user.getId());
		response.setActive(user.getActive());
		response.setCreatedAt(user.getCreatedAt());
		response.setUpdatedAt(user.getUpdatedAt());
		response.setUserName(user.getUserName());
		response.setUserRole(user.getUserRole());
		response.setUserEmail(user.getUserEmail());
		return response;
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {

		try {
			// Create a new User object
			User user = new User();

			// If userRequestDto has an ID, fetch the existing user details by ID
			if (userRequestDto.getId() != null) {
				user = findOne(userRequestDto.getId());
			}

			user.setUserName(userRequestDto.getUserName());
			user.setUserEmail(userRequestDto.getUserEmail());
			user.setUserRole(userRequestDto.getUserRole());
			// Check if it's a new user (no ID) and if the password is null or empty, throw
			// an exception
//			if (userRequestDto.getId() == null
//					&& (userRequestDto.getPassword() == null || userRequestDto.getPassword().isEmpty())) {
//				throw new CustomErrorHandleException("Password should not be empty");
//			}

			// If password is provided, encode it using passwordEncoder and set it for the
			// user
			if (userRequestDto.getPassword() != null) {
				user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
			}

			// Save or update the user in the database
			user = create(user);

			// Convert the user domain object to a DTO and return it
			return domainToDto(user);

		} catch (DataIntegrityViolationException e) {
			// If a DataIntegrityViolationException occurs (e.g., unique constraint
			// violation), throw a custom error
			throw new CustomErrorHandleException("User name " + userRequestDto.getUserName() + " is already taken");
		}

	}

}
