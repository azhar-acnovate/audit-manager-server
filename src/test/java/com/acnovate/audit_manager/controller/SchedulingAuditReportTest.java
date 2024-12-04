package com.acnovate.audit_manager.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.data.PreloadDataUtils;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest // Ensures Spring context is loaded
@TestPropertySource(properties = "envTarget=test-case") // Specific test environment configuration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Test order based on @Order annotation
@Transactional // Rolls back transactions after tests
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Reloads context after class execution
public class SchedulingAuditReportTest {
	// Logger for reporting test actions and results
	protected static final Logger logger = LoggerFactory.getLogger(ExportReportTest.class);

	// ObjectMapper to serialize/deserialize JSON
	ObjectMapper mapper = new ObjectMapper();

	// Static MockMvc object to mock HTTP requests
	private static MockMvc mockMvc;

	// Holds the token and details for a logged-in user
	private static LoggedInUserDetails loggedInUserDetails;

	private static String BASE_URL = "/scheduling-audit-report";

	@Autowired
	private PreloadDataUtils preloadDataUtils;

	// Helper method to retrieve the authorization token
	private String getAccessToken() {
		return "Bearer " + loggedInUserDetails.getAccessToken();
	}

	/**
	 * Sets up the test environment once before all tests are run. This includes
	 * creating a user for authentication and obtaining a token.
	 */
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

	/**
	 * Test case for creating an audit report. Verifies if the report is
	 * successfully created and returned with correct details.
	 */
	@Test
	@Order(1) // Run this test first
	public void test_createSchedulingAuditReportDailyTest() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("DAILY");
		request.setRecipients(Lists.newArrayList("Azhar@gmail.com"));
		request.setReportIds(Lists.newArrayList(1L, 2L));
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Send POST request to create the report
		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		// Parse response and validate the created report data
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});
		SchedulingAuditReportResponse resData = mapper.convertValue(response.getData(),
				SchedulingAuditReportResponse.class);

		// Validate response data matches request data
		assertEquals(1L, resData.getId().longValue());

		assertEquals(request.getFrequencyType(), resData.getFrequencyType());
		assertEquals(request.getRecipients(), resData.getRecipients());
		assertEquals(request.getReportIds(), resData.getReportId());
		assertEquals(request.getSchedulingHour(), resData.getSchedulingHour());
		assertEquals(request.getSchedulingMinute(), resData.getSchedulingMinute());
		assertEquals(request.getTimeMarker(), resData.getTimeMarker());
	}

	/**
	 * Test case for creating an audit report. Verifies if the report is
	 * successfully created and returned with correct details.
	 */
	@Test
	@Order(2) // Run this test first
	public void test_createSchedulingAuditReportMonthlyTest() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("MONTHLY");
		request.setFrequency("1");
		request.setRecipients(Lists.newArrayList("Azhar@gmail.com"));
		request.setReportIds(Lists.newArrayList(1L, 2L));
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Send POST request to create the report
		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		// Parse response and validate the created report data
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});
		SchedulingAuditReportResponse resData = mapper.convertValue(response.getData(),
				SchedulingAuditReportResponse.class);

		// Validate response data matches request data
		assertEquals(2L, resData.getId().longValue());

		assertEquals(request.getFrequencyType(), resData.getFrequencyType());
		assertEquals(request.getRecipients(), resData.getRecipients());
		assertEquals(request.getReportIds(), resData.getReportId());
		assertEquals(request.getSchedulingHour(), resData.getSchedulingHour());
		assertEquals(request.getSchedulingMinute(), resData.getSchedulingMinute());
		assertEquals(request.getTimeMarker(), resData.getTimeMarker());
	}

	/**
	 * Test case for creating an audit report. Verifies if the report is
	 * successfully created and returned with correct details.
	 */
	@Test
	@Order(3) // Run this test first
	public void test_createSchedulingAuditReportWeeklyTest() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("WEEKLY");
		request.setFrequency("MON");
		request.setRecipients(Lists.newArrayList("Azhar@gmail.com"));
		request.setReportIds(Lists.newArrayList(1L, 2L));
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Send POST request to create the report
		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		// Parse response and validate the created report data
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});
		SchedulingAuditReportResponse resData = mapper.convertValue(response.getData(),
				SchedulingAuditReportResponse.class);

		// Validate response data matches request data
		assertEquals(3L, resData.getId().longValue());

		assertEquals(request.getFrequencyType(), resData.getFrequencyType());
		assertEquals(request.getRecipients(), resData.getRecipients());
		assertEquals(request.getReportIds(), resData.getReportId());
		assertEquals(request.getSchedulingHour(), resData.getSchedulingHour());
		assertEquals(request.getSchedulingMinute(), resData.getSchedulingMinute());
		assertEquals(request.getTimeMarker(), resData.getTimeMarker());
	}

	@Test
	@Order(4)
	public void test_createSchedulingAuditReportWithEmptyRecipients() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("WEEKLY");
		request.setRecipients(new ArrayList<>()); // Empty list
		request.setReportIds(Lists.newArrayList(1L, 2L));
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Expect CustomErrorHandleException due to empty recipients list
		mockMvc.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomErrorHandleException))
				.andExpect(result -> assertEquals("Email list must not be empty.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@Order(5)
	public void test_createSchedulingAuditReportWithInvalidEmail() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("WEEKLY");
		request.setRecipients(Lists.newArrayList("invalid-email", "Azhar@gmail.com")); // Invalid email format
		request.setReportIds(Lists.newArrayList(1L, 2L));
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Expect CustomErrorHandleException due to invalid email format
		mockMvc.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomErrorHandleException))
				.andExpect(result -> assertEquals("Validation failed: invalid-email is not a valid email address.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@Order(6)
	public void test_createSchedulingAuditReportWithInvalidFrequency() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("BIWEEKLY"); // Invalid frequency
		request.setRecipients(Lists.newArrayList("Azhar@gmail.com"));
		request.setReportIds(Lists.newArrayList(1L, 2L));
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Expect failure due to unsupported frequency
		mockMvc.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomErrorHandleException))
				.andExpect(result -> assertEquals("Invalid frequency: " + request.getFrequencyType(),
						result.getResolvedException().getMessage()));
	}

	@Test
	@Order(7)
	public void test_createSchedulingAuditReportWithMissingReportIds() throws Exception {
		SchedulingAuditReportRequest request = new SchedulingAuditReportRequest();
		request.setFrequencyType("WEEKLY");
		request.setRecipients(Lists.newArrayList("Azhar@gmail.com"));
		request.setReportIds(null); // Missing report IDs
		request.setSchedulingHour(12);
		request.setSchedulingMinute(30);
		request.setTimeMarker("AM");

		// Expect failure due to missing report IDs
		mockMvc.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomErrorHandleException))
				.andExpect(result -> assertEquals("Report IDs must not be null or empty",
						result.getResolvedException().getMessage()));
	}

	/**
	 * Test case for fetching all audit reports. Ensures that the fetched reports
	 * match the preloaded data.
	 */
	@Test
	@Order(8)
	public void test_findAllReports() throws Exception {
		// Preload some audit report data for the test
		List<SchedulingAuditReport> schedulingAuditReports = preloadDataUtils.loadSchedulingAuditReport();

		// Send GET request to fetch all reports
		ResultActions result = mockMvc
				.perform(get(BASE_URL).header("Authorization", getAccessToken()).param("size", "10")
						.param("pageNo", "1").accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		// Parse response and validate the list of fetched reports
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});

		LinkedHashMap<?, ?> data = (LinkedHashMap<?, ?>) response.getData();
		List<SchedulingAuditReportResponse> resData = mapper.convertValue(data.get("content"),
				new TypeReference<List<SchedulingAuditReportResponse>>() {
				});

		// Check that both lists (fetched and preloaded) have the same size
		assertEquals(resData.size(), schedulingAuditReports.size(), "The sizes of both lists should be equal");

		// sorting by updatedAt desc order
		Comparator<SchedulingAuditReport> comparator = Comparator.comparing(SchedulingAuditReport::getCreatedAt)
				.reversed();
		Collections.sort(schedulingAuditReports, comparator);
		// Iterate over both lists and compare individual fields
		for (int i = 0; i < resData.size(); i++) {
			SchedulingAuditReportResponse dto = resData.get(i);
			SchedulingAuditReport report = schedulingAuditReports.get(i);

			assertEquals(dto.getId(), report.getId(), "ID should match");
			assertEquals(dto.getFrequency(), report.getFrequency());
			assertEquals(dto.getRecipients(), Lists.newArrayList(report.getRecipients().split(",")));
			List<Long> reportIds = Arrays.stream(report.getReportIds().split(",")).map(String::trim) // Trim
					// any
					// whitespace
					.map(Long::valueOf) // Convert String to Long
					.collect(Collectors.toList());
			assertEquals(dto.getReportId(), reportIds);
			assertEquals(dto.getSchedulingHour(), report.getSchedulingHour());
			assertEquals(dto.getSchedulingMinute(), report.getSchedulingMinute());
			assertEquals(dto.getTimeMarker(), report.getTimeMarker());
		}
	}

}
