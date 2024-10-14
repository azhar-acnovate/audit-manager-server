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

import java.security.SecureRandom;

@Service
@Transactional
public class UserServiceImpl extends AbstractRawService<User> implements IUserService {
	@Autowired
	private UserRepository repo;
	@Autowired
private  EmailService emailService;
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
			User user;
			String generatedPassword = null;
			boolean send =false;
			// If userRequestDto has an ID, fetch the existing user details by ID (update scenario)
			if (userRequestDto.getId() != null) {
				user = findOne(userRequestDto.getId());

				// If a password is provided during an update, encode and set it
				if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
					user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
				}
			} else {
				// Create a new user object (create scenario)
				 user = new User();
                 send =true;
				// Generate a random password and encode it
				 generatedPassword = generateRandomPassword();
				 user.setPassword(passwordEncoder.encode(generatedPassword));

			}
			// Common fields for both create and update
			user.setUserName(userRequestDto.getUserName());
			user.setUserEmail(userRequestDto.getUserEmail());
			user.setUserRole(userRequestDto.getUserRole());

			// Save or update the user in the database
			 user = create(user);
			// Send email only if it's a new user creation and use the generated password
			if(user!= null && send) {
				emailService.sendEmail(userRequestDto.getUserEmail(), generatedPassword);
			}
			// Convert the user domain object to a DTO and return it
			return domainToDto(user);

		} catch (DataIntegrityViolationException e) {
			// Handle unique constraint violation (e.g., user name is already taken)
			throw new CustomErrorHandleException("User name " + userRequestDto.getUserName() + " is already taken");
		}
	}


	private String generateRandomPassword() {
		int length = 8; // Set the desired password length
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(characters.length());
			password.append(characters.charAt(index));
		}

		return password.toString();
	}

}
