package com.acnovate.audit_manager.controller;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.controller.UserController;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.dto.response.UserResponseDto;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "envTarget=test-case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    // Logger for reporting test actions and results
    protected static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

 // ObjectMapper to serialize/deserialize JSON
 	ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks
    private UserController userController;

    @Mock
    private IUserService userService;

    @Autowired
    private static MockMvc mockMvc;

    private static final String BASE_URL = "/user"; // this is the base URL for user operations

 // Holds the token and details for a logged-in user
 	private static LoggedInUserDetails loggedInUserDetails;
    
 // Helper method to retrieve the authorization token
 	private String getAccessToken() {
 		return "Bearer " + loggedInUserDetails.getAccessToken();
 	}
    
 	@BeforeAll // Static method to be called before all tests
	public static void setupAll(@Autowired IUserService userService, @Autowired WebApplicationContext context,
			@Autowired FilterChainProxy filterChainProxy, @Value("${spring.profiles.active}") String profile,
			@Autowired PasswordEncoder passwordEncoder) throws Exception {
 
		logger.info("Initial Setup with acivate profile {}", profile);
		MockMvc mockMvcToken = MockMvcBuilders.webAppContextSetup(context).addFilters(filterChainProxy).build();
 
		String password = "test@admin";
		// Create a new admin user for authentication
		User user = new User();
		user.setPassword(passwordEncoder.encode(password));
		user.setUserName("admin");
		user.setUserEmail("test@gmail.com");
		user.setUserRole("USER");
		userService.create(user);
		logger.info("Created admin user for token retrieval!");
 
		// Retrieve access token for authentication
		ResultActions result = mockMvcToken
				.perform(get("/auth/token").with(httpBasic(user.getUserName(), password))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));
 
		// Parse the token from the response
		ObjectMapper mapper = new ObjectMapper();
		String resultString = result.andReturn().getResponse().getContentAsString();
		loggedInUserDetails = mapper.readValue(resultString, new TypeReference<LoggedInUserDetails>() {
		});
 
		// Setup MockMvc for making requests in the tests
		logger.info("Setting up MockMvc for further test cases");
		mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(filterChainProxy).build();
	}
 
//TEST PASSED
 	@Test
 	@Order(1)
 	void testFindAll() throws Exception {
 	    User user = createUser();
 	    
 	    UserResponseDto userResponseDto = new UserResponseDto();
 	    userResponseDto.setId(user.getId());
 	    userResponseDto.setUserName(user.getUserName());
 	    userResponseDto.setUserEmail(user.getUserEmail());
 	    userResponseDto.setUserRole(user.getUserRole());
 	    userResponseDto.setActive(user.getActive());

 	    when(userService.findAll()).thenReturn(Arrays.asList(user));
 	    when(userService.domainToDto(any(User.class))).thenReturn(userResponseDto);

 	    ResultActions result = mockMvc
 	            .perform(get(BASE_URL).header("Authorization", getAccessToken())
 	            .accept("application/json;charset=UTF-8"))
 	            .andExpect(status().isOk())
 	            .andExpect(content().contentType("application/json;charset=UTF-8"));

 	    String responseString = result.andReturn().getResponse().getContentAsString();
 	    CommonResponse body = mapper.readValue(responseString, CommonResponse.class);

 	    assertNotNull(body);
 	    assertEquals(200, body.getStatus());
 	    assertEquals("Successfully fetched user Data..", body.getMessage());
 	    assertNotNull(body.getData());
 	    assertEquals(1, ((List<?>) body.getData()).size());

 	    // Deserialize the user response DTOs directly from the List
 	    List<UserResponseDto> returnedUsers = mapper.convertValue(body.getData(), new TypeReference<List<UserResponseDto>>() {});
 	    UserResponseDto returnedUser = returnedUsers.get(0);

 	    assertEquals(1L, returnedUser.getId());
 	    assertEquals("admin", returnedUser.getUserName());
 	    assertEquals("test@gmail.com", returnedUser.getUserEmail());
 	    assertEquals("USER", returnedUser.getUserRole());
 	    assertTrue(returnedUser.getActive());
 	}

 	// TEST PASSED
 	@Test
 	@Order(2)
 	void testFindOne() throws Exception {
 	    User user = createUser();

 	    UserResponseDto userResponseDto = new UserResponseDto();
 	    userResponseDto.setId(user.getId());
 	    userResponseDto.setUserName(user.getUserName());
 	    userResponseDto.setUserEmail(user.getUserEmail());
 	    userResponseDto.setUserRole(user.getUserRole());
 	    userResponseDto.setActive(user.getActive());

 	    when(userService.findOne(1L)).thenReturn(user);
 	    when(userService.domainToDto(any(User.class))).thenReturn(userResponseDto);

 	    ResultActions result = mockMvc
 	            .perform(get(BASE_URL + "/" + user.getId()).header("Authorization", getAccessToken())
 	            .accept("application/json;charset=UTF-8"))
 	            .andExpect(status().isOk())
 	            .andExpect(content().contentType("application/json;charset=UTF-8"));
 	    
 	    String responseString = result.andReturn().getResponse().getContentAsString();
 	    CommonResponse body = mapper.readValue(responseString, CommonResponse.class);
 	    
 	    assertNotNull(body);
 	    assertEquals(200, body.getStatus());
 	    assertEquals("Successfully fetched user data..", body.getMessage());
 	    assertNotNull(body.getData());

 	    // Deserialize the returned user directly from the response data
 	    UserResponseDto returnedUser = mapper.convertValue(body.getData(), UserResponseDto.class);
 	    
 	    assertEquals(1L, returnedUser.getId());
 	    assertEquals("admin", returnedUser.getUserName());
 	    assertEquals("test@gmail.com", returnedUser.getUserEmail());
 	    assertEquals("USER", returnedUser.getUserRole());
 	    assertTrue(returnedUser.getActive()); // Add this if you want to check if user is active
 	}

 	// TEST PASSED
 	@Test
 	@Order(3)
 	void testCreateUser() throws Exception {
 	    UserRequestDto userRequestDto = new UserRequestDto();
 	    userRequestDto.setUserName("newUser");
 	    userRequestDto.setUserEmail("new@example.com");
 	    userRequestDto.setPassword("password123");
 	    userRequestDto.setUserRole("USER");
 	    
 	    UserResponseDto userResponseDto = new UserResponseDto();
 	    userResponseDto.setId(2L);
 	    userResponseDto.setUserName("newUser");
 	    userResponseDto.setUserEmail("new@example.com");
 	    userResponseDto.setActive(true);
 	    userResponseDto.setUserRole("USER");

 	    when(userService.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

 	    ResultActions result = mockMvc
 	            .perform(post(BASE_URL)
 	            .header("Authorization", getAccessToken())
 	            .contentType("application/json")
 	            .content(mapper.writeValueAsString(userRequestDto)))
 	            .andExpect(status().isOk())
 	            .andExpect(content().contentType("application/json"));
 	    
 	    String responseString = result.andReturn().getResponse().getContentAsString();
 	    CommonResponse body = mapper.readValue(responseString, CommonResponse.class);
 	    
 	    assertNotNull(body);
 	    assertEquals(200, body.getStatus());
 	    assertEquals("Successfully Created User", body.getMessage());
 	    assertNotNull(body.getData());

 	    // Deserialize the returned user directly from the response data
 	    UserResponseDto returnedUser = mapper.convertValue(body.getData(), UserResponseDto.class);
 	    
 	    assertEquals(2L, returnedUser.getId());
 	    assertEquals("newUser", returnedUser.getUserName());
 	    assertEquals("new@example.com", returnedUser.getUserEmail());
 	    assertEquals("USER", returnedUser.getUserRole());
 	    assertTrue(returnedUser.getActive());
 	}

// TEST PASSED
 	@Test
 	@Order(4)
 	void testUpdateUser() throws Exception {
 	    // Create a UserRequestDto for the update
 	    UserRequestDto userRequestDto = new UserRequestDto();
 	    userRequestDto.setId(1L);
 	    userRequestDto.setUserName("updatedUser");
 	    userRequestDto.setUserEmail("updated@example.com");
 	    userRequestDto.setUserRole("ADMIN");
 	    userRequestDto.setPassword("test@admin");
 	    
 	    // Create a corresponding UserResponseDto for the expected response
 	    UserResponseDto userResponseDto = new UserResponseDto();
 	    userResponseDto.setId(1L);
 	    userResponseDto.setUserName("updatedUser");
 	    userResponseDto.setUserEmail("updated@example.com");
 	    userResponseDto.setActive(true); // Assume the user is active
 	    userResponseDto.setUserRole("ADMIN");

 	    // Mock the service call to return the expected DTO
 	    when(userService.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

 	    // Perform the POST request to update the user
 	    ResultActions result = mockMvc.perform(post(BASE_URL)
 	            .header("Authorization", getAccessToken())
 	            .contentType("application/json")
 	            .content(mapper.writeValueAsString(userRequestDto)))
 	            .andExpect(status().isOk())
 	            .andExpect(content().contentType("application/json"));

 	    // Read the response and parse it
 	    String responseString = result.andReturn().getResponse().getContentAsString();
 	    CommonResponse body = mapper.readValue(responseString, CommonResponse.class);

 	    // Assertions
 	    assertNotNull(body);
 	    assertEquals(200, body.getStatus());
 	    assertEquals("Successfully Updated User", body.getMessage());
 	    assertNotNull(body.getData());

 	    // Deserialize the returned user directly from the response data
 	    UserResponseDto returnedUser = mapper.convertValue(body.getData(), UserResponseDto.class);

 	    // Assertions on the returned user data
 	    assertEquals(1L, returnedUser.getId());
 	    assertEquals("updatedUser", returnedUser.getUserName());
 	    assertEquals("updated@example.com", returnedUser.getUserEmail());
 	    assertEquals("ADMIN", returnedUser.getUserRole());
 	    assertTrue(returnedUser.getActive());
 	}


    // Helper method to instantiate User
    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testUser");
        //user.setPassword("test@admin");
        user.setActive(true);
        user.setUserRole("USER");
        user.setUserEmail("test@example.com");
        return user;
    }
}