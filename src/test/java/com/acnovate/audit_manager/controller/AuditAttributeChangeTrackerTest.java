package com.acnovate.audit_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.FilterDto;
import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.AuditAttributeChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditAttributeChangeTrackerResponseDto;
import com.acnovate.audit_manager.repository.AuditAttributeChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "envTarget=test-case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditAttributeChangeTrackerTest {

	// Logger for reporting test actions and results
	protected static final Logger logger = LoggerFactory.getLogger(AuditAttributeChangeTrackerTest.class);

	@Autowired
	private static MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String BASE_URL = "/audit-attibute-change-tracker"; // this is the base URL for operations

	@Autowired
	private AuditAttributeChangeTrackerRepository attRepository;

	@MockBean // Use MockBean for the service
	private IAuditAttributeChangeTrackerService auditAttributeChangeTrackerService;

	private static LoggedInUserDetails loggedInUserDetails;

	private String getAccessToken() {
		return "Bearer " + loggedInUserDetails.getAccessToken();
	}

	@BeforeEach
	public void setUp() {
		attRepository.deleteAll();
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

	// TEST FAILED
	@Test
	@Order(1)
	void testCreateAuditAttributeChangeTracker() throws Exception {
		// Create a request DTO
		AuditAttributeChangeRequestDto attributeChangeTrackerRequest = new AuditAttributeChangeRequestDto();
		attributeChangeTrackerRequest.setAttributeName("status");
		attributeChangeTrackerRequest.setOldValue("pending");
		attributeChangeTrackerRequest.setNewValue("approved");
		attributeChangeTrackerRequest.setChangedBy("user123");

		// Create a mock response DTO
		AuditAttributeChangeTrackerResponseDto attributeChangeTrackerResponse = new AuditAttributeChangeTrackerResponseDto();
		attributeChangeTrackerResponse.setId(1L);
		attributeChangeTrackerResponse.setAttributeName("status");
		attributeChangeTrackerResponse.setOldValue("pending");
		attributeChangeTrackerResponse.setNewValue("approved");
		attributeChangeTrackerResponse.setChangedBy("user123");

		// Mock the service method
		when(auditAttributeChangeTrackerService
				.createAuditAttributeChangeTracker(any(AuditAttributeChangeRequestDto.class)))
				.thenReturn(attributeChangeTrackerResponse);

		// Perform the POST request
		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(objectMapper.writeValueAsString(attributeChangeTrackerRequest)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		// Deserialize the response and validate the returned data
		String responseString = result.andReturn().getResponse().getContentAsString();
		CommonResponse body = objectMapper.readValue(responseString, CommonResponse.class);

		Assertions.assertNotNull(body.getData(), "Response should not be null");
		Assertions.assertEquals(HttpStatus.OK.value(), body.getStatus(), "Response status should be 200");
		Assertions.assertEquals("Successfully Created Audit Attribute Change Tracker", body.getMessage(),
				"Message should match");

		// Validate the data returned in the response
		AuditAttributeChangeTrackerResponseDto returnedLog = objectMapper.convertValue(body.getData(),
				AuditAttributeChangeTrackerResponseDto.class);
		Assertions.assertNotNull(returnedLog, "Returned log should not be null");
		Assertions.assertEquals("status", returnedLog.getAttributeName(), "Returned attribute name should match");
		Assertions.assertEquals("pending", returnedLog.getOldValue(), "Returned old value should match");
		Assertions.assertEquals("approved", returnedLog.getNewValue(), "Returned new value should match");
		Assertions.assertEquals("user123", returnedLog.getChangedBy(), "Returned changed by should match");
	}

	// TEST PASSED
	@Test
	@Order(2)
	void testFindAll() throws Exception {
		// Create a mock AuditAttributeChangeTracker object
		AuditAttributeChangeTracker audit = createAuditAttribute();

		// Prepare the corresponding DTO
		AuditAttributeChangeTrackerResponseDto auditResponseDto = new AuditAttributeChangeTrackerResponseDto();
		auditResponseDto.setId(audit.getId());
		auditResponseDto.setAttributeName(audit.getAttributeName());
		auditResponseDto.setChangedBy(audit.getChangedBy());
		auditResponseDto.setOldValue(audit.getOldValue());
		auditResponseDto.setNewValue(audit.getNewValue());

		// Prepare the FilterDto
		FilterDto filter = new FilterDto();
		filter.getFilter().put("auditObjectChangeTracker.id", 1L); // Sample ID for filtering

		// Mocking the service for the case with no pagination (just a list)
		when(auditAttributeChangeTrackerService.findAll(any(FilterDto.class))).thenReturn(Arrays.asList(audit)); // Return
																													// a
																													// list
																													// with
																													// the
																													// mock
																													// audit

		// Mock the DTO conversion
		when(auditAttributeChangeTrackerService.domainToDto(any(AuditAttributeChangeTracker.class)))
				.thenReturn(auditResponseDto);

		// Perform the GET request
		ResultActions result = mockMvc
				.perform(get(BASE_URL).header("Authorization", getAccessToken())
						.param("auditObjectChangeTrackerId", "1").accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		// Read the response and parse it
		String responseString = result.andReturn().getResponse().getContentAsString();
		CommonResponse body = objectMapper.readValue(responseString, CommonResponse.class);

		System.out.println("PRINTING REPONSE STRING: " + responseString);

		// Assertions
		assertNotNull(body);
		assertEquals(200, body.getStatus());
		assertEquals("Successfully fetched audit-module Data..", body.getMessage());
		assertNotNull(body.getData());

		System.out.println("BODY: " + body);

		// Deserialize the response DTOs
		List<AuditAttributeChangeTrackerResponseDto> returnedResps = objectMapper.convertValue(body.getData(),
				new TypeReference<List<AuditAttributeChangeTrackerResponseDto>>() {
				});
		assertEquals(1, returnedResps.size()); // Expecting 1 result

		// Validate the returned DTO values
		AuditAttributeChangeTrackerResponseDto returnedResp = returnedResps.get(0);
		assertEquals(audit.getId(), returnedResp.getId());
		assertEquals(audit.getChangedBy(), returnedResp.getChangedBy());
		assertEquals(audit.getAttributeName(), returnedResp.getAttributeName());
		assertEquals(audit.getOldValue(), returnedResp.getOldValue());
		assertEquals(audit.getNewValue(), returnedResp.getNewValue());
	}

	private AuditAttributeChangeTracker createAuditAttribute() {

		AuditAttributeChangeTracker obj = new AuditAttributeChangeTracker();

		obj.setId(1L);
		obj.setAttributeName("status");
		obj.setOldValue("pending");
		obj.setNewValue("approved");
		obj.setChangedBy("user123");

		return obj;
	}

}
