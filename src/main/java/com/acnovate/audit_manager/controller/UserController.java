package com.acnovate.audit_manager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {
		
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/allUser")
	public List<User> displayAllUsers(User user) {
		return userRepository.findAll();
	}
	
	@PostMapping("/createUser")
	public String createNewUser(@RequestBody User user) {
		userRepository.save(user);
		return "user Saved Successfuly";
	}
	
	@GetMapping("/getUser/{id}")
	public ResponseEntity<Optional<User>> getUserById(@PathVariable(value = "id") Long id)
			throws Exception {		
		Optional<User> usr =  userRepository.findById(id);
		if (usr.isPresent()) {
		return ResponseEntity.ok().body(usr);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
		}
		
	}
	
	@PutMapping("/updateUser/{id}")
	public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long id,
			 @RequestBody User user) throws Exception {
		User usr = userRepository.findById(id)
				.orElseThrow(() -> new Exception("User not found for this id :: " + id));

		usr.setACTIVE(user.getACTIVE());
		usr.setUSERNAME(user.getUSERNAME());
		usr.setPASSWORD(user.getPASSWORD());
		usr.setPROFILEIMAGENAME(user.getPROFILEIMAGENAME());
		final User updatedUser = userRepository.save(usr);
		return ResponseEntity.ok(updatedUser);
	}
	
	@DeleteMapping("/deleteUser/{id}")
	public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long id)
			throws Exception {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new Exception("User not found for this id :: " + id));

		userRepository.delete(user);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}