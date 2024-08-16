package com.acnovate.audit_manager.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.service.IUserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IUserService userService;

	@Override
	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username != null) {

			try {
				User user = userService.findByUserName(username);

				if (user == null) {
					throw new UsernameNotFoundException("User : [" + username + "] is not found ");

				} else {
					System.out.println(user);
					final List<GrantedAuthority> auths = new ArrayList<>();
					// For SpringSecurityAuditorAware get userid so we have append userName_userId
					return new CustomUserDetails(user.getUserName() + "_" + user.getId(), user.getPassword(), auths,
							user.getId());

				}
			} catch (Exception e) {
				System.out.println("Exceptrion ::" + ExceptionUtils.getStackTrace(e));
				throw new CustomErrorHandleException("Invalid username : [" + username + "]");
			}

		} else {
			throw new CustomErrorHandleException("Invalid username : [" + username + "]");
		}

	}
}
