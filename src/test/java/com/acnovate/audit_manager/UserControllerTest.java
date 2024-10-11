package com.acnovate.audit_manager;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.controller.UserController;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.dto.response.UserResponseDto;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private IUserService userService;

    @Test
    void testFindAll() {
    	
    	// Instantiating a User object
    	User user = createUser();

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUserName(user.getUserName());
        userResponseDto.setUserEmail(user.getUserEmail());
        userResponseDto.setUserRole(user.getUserRole());
        userResponseDto.setActive(user.getActive());

        when(userService.findAll()).thenReturn(Arrays.asList(user)); // Mocking to return User
        when(userService.domainToDto(any(User.class))).thenReturn(userResponseDto); // Mocking conversion

        ResponseEntity<CommonResponse> response = userController.findAll(null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CommonResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("Successfully fetched user Data..", body.getMessage());
        assertNotNull(body.getData());
        assertEquals(1, ((List<?>) body.getData()).size());

        UserResponseDto returnedUser = (UserResponseDto) ((List<?>) body.getData()).get(0);
        assertEquals(1L, returnedUser.getId());
        assertEquals("testUser", returnedUser.getUserName());
        assertEquals("test@example.com", returnedUser.getUserEmail());
        assertEquals("USER", returnedUser.getUserRole());
        assertTrue(returnedUser.getActive());
    }

    @Test
    void testFindOne() {
    	
    	// instantiating the User object
    	User user = createUser();
       
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUserName(user.getUserName());
        userResponseDto.setUserEmail(user.getUserEmail());
        userResponseDto.setUserRole(user.getUserRole());
        userResponseDto.setActive(user.getActive());
        
        when(userService.findOne(1L)).thenReturn(user);
        when(userService.domainToDto(any(User.class))).thenReturn(userResponseDto); // Mocking conversion
        
        ResponseEntity<CommonResponse> response = userController.findOne(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CommonResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("Successfully fetched user data..", body.getMessage());
        assertNotNull(body.getData());

        UserResponseDto returnedUser = (UserResponseDto) body.getData();
        assertEquals(1L, returnedUser.getId());
        assertEquals("testUser", returnedUser.getUserName());
        assertEquals("test@example.com", returnedUser.getUserEmail());
        assertEquals("USER", returnedUser.getUserRole());
    }

    @Test
    void testCreateUser() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserName("newUser");
        userRequestDto.setUserEmail("new@example.com");
        userRequestDto.setPassword("password123");

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUserName("newUser");
        userResponseDto.setUserEmail("new@example.com");
        userResponseDto.setActive(true);
        userResponseDto.setUserRole("USER");

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);
        
        ResponseEntity<CommonResponse> response = userController.createUser(userRequestDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CommonResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("Successfully Create User", body.getMessage());
        assertNotNull(body.getData());

        UserResponseDto returnedUser = (UserResponseDto) body.getData();
        assertEquals(1L, returnedUser.getId());
        assertEquals("newUser", returnedUser.getUserName());
        assertEquals("new@example.com", returnedUser.getUserEmail());
        assertEquals("USER", returnedUser.getUserRole());
        assertTrue(returnedUser.getActive());
    }
    
    @Test
    void testUpdateUser() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setId(1L); // Assuming the ID is set for updating
        userRequestDto.setUserName("updatedUser");
        userRequestDto.setUserEmail("updated@example.com");
        userRequestDto.setUserRole("ADMIN"); // Updated role

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUserName("updatedUser");
        userResponseDto.setUserEmail("updated@example.com");
        userResponseDto.setActive(true);
        userResponseDto.setUserRole("ADMIN");

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        ResponseEntity<CommonResponse> response = userController.createUser(userRequestDto); // Assuming this handles both create and update

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CommonResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getStatus());
        assertEquals("Successfully Updated User", body.getMessage());
        assertNotNull(body.getData());

        UserResponseDto returnedUser = (UserResponseDto) body.getData();
        assertEquals(1L, returnedUser.getId());
        assertEquals("updatedUser", returnedUser.getUserName());
        assertEquals("updated@example.com", returnedUser.getUserEmail());
        assertEquals("ADMIN", returnedUser.getUserRole());
        assertTrue(returnedUser.getActive());
    }
    
    // To instantiate User
    User createUser() {
    	User user = new User();
        user.setId(1L);
        user.setUserName("testUser");
        user.setPassword("password123");
        user.setActive(true);
        user.setUserRole("USER");
        user.setUserEmail("test@example.com");
		return user;
    }
}