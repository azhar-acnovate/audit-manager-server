package com.acnovate.audit_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditLogActivityResponseDto;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "envTarget=test-case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuditObjectChangeTrackerTest {

	protected static final Logger logger = LoggerFactory.getLogger(AuditObjectChangeTrackerTest.class);

	@Autowired
	private static MockMvc mockMvc;

	static ObjectMapper mapper = new ObjectMapper();

	private static final String BASE_URL = "/audit-object-change-tracker";

	@MockBean
	private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

	private static LoggedInUserDetails loggedInUserDetails;

	private String getAccessToken() {
		return "Bearer " + loggedInUserDetails.getAccessToken();
	}

	@BeforeAll
	public static void setupAll(@Autowired IUserService userService, @Autowired WebApplicationContext context,
			@Autowired FilterChainProxy filterChainProxy, @Value("${spring.profiles.active}") String profile,
			@Autowired PasswordEncoder passwordEncoder) throws Exception {

		logger.info("Initial Setup with activate profile {}", profile);
		MockMvc mockMvcToken = MockMvcBuilders.webAppContextSetup(context).addFilters(filterChainProxy).build();

		String password = "test@admin";
		User user = new User();
		user.setPassword(passwordEncoder.encode(password));
		user.setUserName("admin");
		user.setUserEmail("test@gmail.com");
		user.setUserRole("USER");
		userService.create(user);
		logger.info("Created admin user for token retrieval!");

		ResultActions result = mockMvcToken
				.perform(get("/auth/token").with(httpBasic(user.getUserName(), password))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String resultString = result.andReturn().getResponse().getContentAsString();
		loggedInUserDetails = mapper.readValue(resultString, new TypeReference<LoggedInUserDetails>() {
		});

		logger.info("Setting up MockMvc for further test cases");
		mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(filterChainProxy).build();
	}

	@BeforeEach
	public void setUp() {
		// Clear mock interactions before each test
		reset(auditObjectChangeTrackerService);
	}

// TEST PASSED
	@Test
	@Order(1)
	void testCreateAuditObjectChangeTracker() throws Exception {
		AuditObjectChangeRequestDto requestDto = new AuditObjectChangeRequestDto();
		requestDto.setRefObjectId(1L);
		requestDto.setEventType("CREATE");
		requestDto.setEventOccurence(new Date());

		AuditLogActivityResponseDto responseDto = new AuditLogActivityResponseDto();
		responseDto.setId(1L);
		responseDto.setRefObjectId(1L);
		responseDto.setEventType("CREATE");
		responseDto.setEventOccurence(requestDto.getEventOccurence());

		when(auditObjectChangeTrackerService.createAuditObjectChangeTracker(any(AuditObjectChangeRequestDto.class)))
				.thenReturn(responseDto);

		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(mapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		String responseString = result.andReturn().getResponse().getContentAsString();
		CommonResponse body = mapper.readValue(responseString, CommonResponse.class);

		assertNotNull(body);
		assertEquals(200, body.getStatus());
		assertEquals("Successfully Created Audit Object Change Tracker", body.getMessage());

		AuditLogActivityResponseDto returnedLog = mapper.convertValue(body.getData(),
				AuditLogActivityResponseDto.class);
		assertNotNull(returnedLog);
		assertEquals(1L, returnedLog.getId());
		assertEquals(1L, returnedLog.getRefObjectId());
		assertEquals("CREATE", returnedLog.getEventType());
	}

	// TEST PASSED
	@Test
	@Order(2)
	void testFindAll() throws Exception {
		// Create mock AuditObjectChangeTracker objects
		AuditObjectChangeTracker objectChangeTracker1 = createAuditObjectChangeTracker(1L, "CREATE");
		AuditObjectChangeTracker objectChangeTracker2 = createAuditObjectChangeTracker(2L, "UPDATE");

		// Prepare corresponding DTOs
		AuditLogActivityResponseDto responseDto1 = new AuditLogActivityResponseDto();
		responseDto1.setId(objectChangeTracker1.getId());
		responseDto1.setRefObjectId(objectChangeTracker1.getRefObjectId());
		responseDto1.setEventType(objectChangeTracker1.getEventType());
		responseDto1.setEventOccurence(objectChangeTracker1.getEventOccurence());

		AuditLogActivityResponseDto responseDto2 = new AuditLogActivityResponseDto();
		responseDto2.setId(objectChangeTracker2.getId());
		responseDto2.setRefObjectId(objectChangeTracker2.getRefObjectId());
		responseDto2.setEventType(objectChangeTracker2.getEventType());
		responseDto2.setEventOccurence(objectChangeTracker2.getEventOccurence());

		// Mocking the service to return the list
		when(auditObjectChangeTrackerService.findAll())
				.thenReturn(Arrays.asList(objectChangeTracker1, objectChangeTracker2));

		// Mock the DTO conversion
		when(auditObjectChangeTrackerService.domainToDto(objectChangeTracker1)).thenReturn(responseDto1);
		when(auditObjectChangeTrackerService.domainToDto(objectChangeTracker2)).thenReturn(responseDto2);

		// Perform the GET request
		ResultActions result = mockMvc
				.perform(get(BASE_URL).header("Authorization", getAccessToken())
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		// Read the response and parse it
		String responseString = result.andReturn().getResponse().getContentAsString();
		CommonResponse body = mapper.readValue(responseString, CommonResponse.class);

		// Assertions
		assertNotNull(body);
		assertEquals(200, body.getStatus());
		assertEquals("Successfully fetched audit-module Data..", body.getMessage());
		assertNotNull(body.getData());

		// Deserialize the response DTOs
		List<AuditLogActivityResponseDto> returnedResps = mapper.convertValue(body.getData(),
				new TypeReference<List<AuditLogActivityResponseDto>>() {
				});
		assertEquals(2, returnedResps.size()); // Expecting 2 results

		// Validate the returned DTO values for the first log
		AuditLogActivityResponseDto returnedResp1 = returnedResps.get(0);
		assertEquals(objectChangeTracker1.getId(), returnedResp1.getId());
		assertEquals(objectChangeTracker1.getEventType(), returnedResp1.getEventType());

		// Validate the returned DTO values for the second log
		AuditLogActivityResponseDto returnedResp2 = returnedResps.get(1);
		assertEquals(objectChangeTracker2.getId(), returnedResp2.getId());
		assertEquals(objectChangeTracker2.getEventType(), returnedResp2.getEventType());
	}

	// TEST PASSED
	@Test
	@Order(3)
	void testFindOne() throws Exception {
		// Create a mock AuditObjectChangeTracker object
		AuditObjectChangeTracker objectChangeTracker = createAuditObjectChangeTracker(1L, "CREATE");

		// Prepare the corresponding DTO
		AuditLogActivityResponseDto responseDto = new AuditLogActivityResponseDto();
		responseDto.setId(objectChangeTracker.getId());
		responseDto.setRefObjectId(objectChangeTracker.getRefObjectId());
		responseDto.setEventType(objectChangeTracker.getEventType());
		responseDto.setEventOccurence(objectChangeTracker.getEventOccurence());

		// Mocking the service method
		when(auditObjectChangeTrackerService.findOne(1L)).thenReturn(objectChangeTracker);
		when(auditObjectChangeTrackerService.domainToDto(objectChangeTracker)).thenReturn(responseDto);

		// Perform the GET request
		ResultActions result = mockMvc
				.perform(get(BASE_URL + "/1").header("Authorization", getAccessToken())
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		// Read the response and parse it
		String responseString = result.andReturn().getResponse().getContentAsString();
		CommonResponse body = mapper.readValue(responseString, CommonResponse.class);

		// Assertions
		assertNotNull(body);
		assertEquals(200, body.getStatus());
		assertEquals("Successfully fetched audit-module Data..", body.getMessage());
		assertNotNull(body.getData());

		// Deserialize the response DTO
		AuditLogActivityResponseDto returnedResponse = mapper.convertValue(body.getData(),
				AuditLogActivityResponseDto.class);

		// Assertions on the returned response
		assertNotNull(returnedResponse);
		assertEquals(objectChangeTracker.getId(), returnedResponse.getId());
		assertEquals(objectChangeTracker.getEventType(), returnedResponse.getEventType());
	}

	private AuditObjectChangeTracker createAuditObjectChangeTracker(Long id, String eventType) {
		AuditObjectChangeTracker auditObject = new AuditObjectChangeTracker();
		auditObject.setId(id);
		auditObject.setRefObjectId(1L);
		auditObject.setEventType(eventType);
		auditObject.setEventOccurence(new Date());
		return auditObject;
	}
}
