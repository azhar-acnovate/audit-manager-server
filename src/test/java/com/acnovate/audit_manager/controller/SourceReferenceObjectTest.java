package com.acnovate.audit_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.constant.MyConstant;
import com.acnovate.audit_manager.data.PreloadDataUtils;
import com.acnovate.audit_manager.domain.SourceAdditionalInfo;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.SourceReferenceObjectRequestDto;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.dto.response.SourceReferenceObjectResponseDto;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest // Ensures Spring context is loaded
@TestPropertySource(properties = "envTarget=test-case") // Specific test environment configuration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Test order based on @Order annotation
@Transactional // Rolls back transactions after tests
public class SourceReferenceObjectTest {
	// Logger for reporting test actions and results
	protected static final Logger logger = LoggerFactory.getLogger(ReportControllerTest.class);

	// ObjectMapper to serialize/deserialize JSON
	ObjectMapper mapper = new ObjectMapper();

	// Static MockMvc object to mock HTTP requests
	private static MockMvc mockMvc;

	@Autowired
	private PreloadDataUtils preloadDataUtils;

	// Holds the token and details for a logged-in user
	private static LoggedInUserDetails loggedInUserDetails;

	private static String BASE_URL = "/source-reference-object";

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
			@Autowired FilterChainProxy filterChainProxy, @Value("${spring.profiles.active}") String profile)
			throws Exception {

		logger.info("Initial Setup with acivate profile {}", profile);
		MockMvc mockMvcToken = MockMvcBuilders.webAppContextSetup(context).addFilters(filterChainProxy).build();

		// Create a new admin user for authentication
		UserRequestDto user = new UserRequestDto();
		user.setPassword("test@admin");
		user.setUserName("admin");
		user.setUserEmail("test@gmail.com");
		userService.createUser(user);
		logger.info("Created admin user for token retrieval!");

		// Retrieve access token for authentication
		ResultActions result = mockMvcToken
				.perform(get("/auth/token").with(httpBasic(user.getUserName(), user.getPassword()))
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
	 * Test case for creating an source reference Verifies if the report is
	 * successfully created and returned with correct details.
	 */
	@Test
	@Order(1) // Run this test first
	public void test_createSourceReferenceObject() throws Exception {
		SourceReferenceObjectRequestDto request = new SourceReferenceObjectRequestDto();
		request.setSourceReferenceName("Product");
		request.setSourceReferenceKey("1");

		// Creating dummy additional info
		List<SourceAdditionalInfo> additionalInfoList = new ArrayList<>();
		additionalInfoList.add(new SourceAdditionalInfo("DetailKey1", "DetailValue1"));
		additionalInfoList.add(new SourceAdditionalInfo("DetailKey2", "DetailValue1"));

		request.setAdditionalInfo(additionalInfoList);
		// Send POST request to create the report
		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		// Parse response and validate the created report data
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});

		SourceReferenceObjectResponseDto resData = mapper.convertValue(response.getData(),
				SourceReferenceObjectResponseDto.class);

		// Validate response data matches request data
		assertEquals(1L, resData.getId().longValue());
		assertEquals(true, resData.getActive());
		assertEquals(request.getSourceReferenceKey(), resData.getSourceReferenceKey());
		assertEquals(request.getSourceReferenceName(), resData.getSourceReferenceName());
		assertEquals(request.getAdditionalInfo(), resData.getAdditionalInfo());
	}

	/**
	 * Test case for fetching all source reference object. Ensures that the fetched
	 * reports match the preloaded data.
	 */
	@Test
	@Order(2)
	public void test_findAllSourceReferenceObject() throws Exception {
		// Preload some audit report data for the test
		List<SourceReferenceObject> sourceReferenceList = preloadDataUtils.loadSourceReferenceObjectData();

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
		List<SourceReferenceObjectResponseDto> resData = mapper.convertValue(data.get("content"),
				new TypeReference<List<SourceReferenceObjectResponseDto>>() {
				});

		// Check that both lists (fetched and preloaded) have the same size
		assertEquals(resData.size(), sourceReferenceList.size(), "The sizes of both lists should be equal");

		// Iterate over both lists and compare individual fields
		for (int i = 0; i < resData.size(); i++) {
			SourceReferenceObjectResponseDto dto = resData.get(i);
			SourceReferenceObject sourceReferenceObject = sourceReferenceList.get(i);

			assertEquals(dto.getId(), sourceReferenceObject.getId(), "ID should match");
			assertEquals(dto.getSourceReferenceKey(), sourceReferenceObject.getSourceReferenceKey(),
					"Source Reference Key Should be match");
			assertEquals(dto.getSourceReferenceName(), sourceReferenceObject.getSourceReferenceName(),
					"Source Reference name Should be match");
			assertEquals(dto.getAdditionalInfo(), sourceReferenceObject.getAdditionalInfo(),
					"AdditionalInfo Key Should be match");

		}
	}

	/**
	 * Test case for fetching a Source Reference Object by ID. Verifies if the
	 * correct report is returned when fetched by its ID.
	 */
	@Test
	@Order(3)
	public void test_findSourceReferenceObjectById() throws Exception {
		// Preload a single audit report for the test
		SourceReferenceObject sourceReferenceObject = preloadDataUtils.loadSourceReferenceOne();
		Long sourceReferenceObjectId = sourceReferenceObject.getId();

		// Send GET request to fetch the report by ID
		ResultActions result = mockMvc
				.perform(get(BASE_URL + "/" + sourceReferenceObjectId).header("Authorization", getAccessToken())
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		// Parse response and validate the fetched report data
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});
		SourceReferenceObjectResponseDto resData = mapper.convertValue(response.getData(),
				SourceReferenceObjectResponseDto.class);

		// Check that the fetched data matches the preloaded sourceReferenceObject
		// Validate response data matches request data
		assertEquals(sourceReferenceObject.getId(), resData.getId().longValue());
		assertEquals(true, resData.getActive());
		assertEquals(sourceReferenceObject.getSourceReferenceKey(), resData.getSourceReferenceKey());
		assertEquals(sourceReferenceObject.getSourceReferenceName(), resData.getSourceReferenceName());
		assertEquals(sourceReferenceObject.getAdditionalInfo(), resData.getAdditionalInfo());
	}

	/**
	 * Test case for fetching a SourceReferenceObject by in valid ID. Verifies if
	 * the proper way to error handled
	 */

	@Test
	@Order(4)
	public void test_findInvalidSourceReferenceObjectById() throws Exception {
		Long invalidReportId = 999L; // Assume this ID doesn't exist

		ResultActions result = mockMvc.perform(get(BASE_URL + "/" + invalidReportId)
				.header("Authorization", getAccessToken()).accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk());
		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});
		assertEquals(response.getStatus(), 500, "Internal error");
		assertEquals(response.getData(), null, "Data Sholud be null");
		assertEquals(response.getMessage(), MyConstant.EXCEPTION_MESSAGE_RESOURCE_NOT_FOUND, "Resource not found");
	}

	/**
	 * Test case for updating an source reference object. Verifies if the report is
	 * successfully updated and returned with correct details.
	 */
	@Test
	@Order(5)
	public void test_updateSourceReferenceObject() throws Exception {
		SourceReferenceObject existSourceReferenceObject = preloadDataUtils.loadSourceReferenceOne();
		SourceReferenceObjectRequestDto request = new SourceReferenceObjectRequestDto();
		request.setId(existSourceReferenceObject.getId());
		request.setSourceReferenceName("Product");
		request.setSourceReferenceKey("1");

		// Creating dummy additional info
		List<SourceAdditionalInfo> additionalInfoList = new ArrayList<>();
		additionalInfoList.add(new SourceAdditionalInfo("DetailKey1", "DetailValue1"));
		additionalInfoList.add(new SourceAdditionalInfo("DetailKey2", "DetailValue1"));

		ResultActions result = mockMvc
				.perform(post(BASE_URL).header("Authorization", getAccessToken()).contentType("application/json")
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

		String resultString = result.andReturn().getResponse().getContentAsString();
		CommonResponse response = mapper.readValue(resultString, new TypeReference<CommonResponse>() {
		});

		SourceReferenceObjectResponseDto resData = mapper.convertValue(response.getData(),
				SourceReferenceObjectResponseDto.class);
		assertEquals(existSourceReferenceObject.getId(), resData.getId().longValue());
		assertEquals(true, resData.getActive());
		assertEquals(request.getSourceReferenceKey(), resData.getSourceReferenceKey());
		assertEquals(request.getSourceReferenceName(), resData.getSourceReferenceName());
		assertEquals(request.getAdditionalInfo(), resData.getAdditionalInfo());

	}
}
