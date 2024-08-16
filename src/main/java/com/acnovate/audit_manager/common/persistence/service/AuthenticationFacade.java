package com.acnovate.audit_manager.common.persistence.service;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

//	@Autowired
//	private IUserService userService;
//	@Autowired
//	private ICustomerAuthService customerAuthService;
//
//	public User getLoginUser() {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null && auth.getName() != null) {
//			User user = userService.findByUserName(auth.getName());
//			return user != null ? user : userService.findOne(0);
//		} else {
//			return userService.findOne(0);
//		}
//	}
//
//	public CustomerAuth getLoginCustomerUser() {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null && auth.getName() != null) {
//			CustomerAuth user = customerAuthService.findByUserName(auth.getName());
//			return user != null ? user : customerAuthService.findOne(0);
//		} else {
//			return customerAuthService.findOne(0);
//		}
//	}
}
