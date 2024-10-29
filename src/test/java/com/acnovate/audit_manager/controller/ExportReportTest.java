package com.acnovate.audit_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.data.PreloadDataUtils;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.service.IAuditReportService;
import com.acnovate.audit_manager.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest // Ensures Spring context is loaded
@TestPropertySource(properties = "envTarget=test-case") // Specific test environment configuration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Test order based on @Order annotation
@Transactional // Rolls back transactions after tests
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Reloads context after class execution
public class ExportReportTest {

	// Logger for reporting test actions and results
	protected static final Logger logger = LoggerFactory.getLogger(ExportReportTest.class);

	// ObjectMapper to serialize/deserialize JSON
	ObjectMapper mapper = new ObjectMapper();

	// Static MockMvc object to mock HTTP requests
	private static MockMvc mockMvc;

	// Holds the token and details for a logged-in user
	private static LoggedInUserDetails loggedInUserDetails;

	@Autowired
	private PreloadDataUtils preloadDataUtils;

	@Autowired
	private IAuditReportService auditReportService;

	private static String BASE_URL = "/audit-report";

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
	 * Test case for export csv report . Verifies if the report is successfully
	 * exported as per file type csv.
	 */
	@Test
	@Order(1) // Run this test first
	public void test_export_csv_report() throws Exception {
		List<Long> reportIds = preloadDataUtils.loadExportReportData();
		String reportIdsAsString = reportIds.stream().map(String::valueOf) // Convert Long to String
				.collect(Collectors.joining(","));
		String fileType = "csv";
		byte[] mockFileBytes = auditReportService.genereteReport(1L, reportIds, fileType);
		// Perform the GET request to the "/generate-report" endpoint
		mockMvc.perform(get(BASE_URL + "/generate-report").header("Authorization", getAccessToken())
				.param("fileType", fileType).param("reportIds", reportIdsAsString)) // passing
				// mock
				// report
				// IDs
				.andExpect(status().isOk()) // Expect 200 OK status
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report." + fileType)) // Check
				// header
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv")) // Check Content-Type header
				.andExpect(content().bytes(mockFileBytes)); // Check file content matches mock data
	}

	/**
	 * Test case for export xlsx report . Verifies if the report is successfully
	 * exported as per file type xlsx.
	 */
	@Test
	@Order(2) // Run this test first
	public void test_export_xlsx_report() throws Exception {
		List<Long> reportIds = preloadDataUtils.loadExportReportData();
		String reportIdsAsString = reportIds.stream().map(String::valueOf) // Convert Long to String
				.collect(Collectors.joining(","));
		String fileType = "xlsx";
		byte[] mockFileBytes = auditReportService.genereteReport(1L, reportIds, fileType);

		ResultActions result = mockMvc
				.perform(get(BASE_URL + "/generate-report").header("Authorization", getAccessToken())
						.param("fileType", fileType).param("reportIds", reportIdsAsString))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report." + fileType))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE,
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

		byte[] fileBytes = result.andReturn().getResponse().getContentAsByteArray();

		// Use Apache POI to read and verify XLSX content
		try (ByteArrayInputStream resInputStream = new ByteArrayInputStream(fileBytes);
				ByteArrayInputStream mockInputStream = new ByteArrayInputStream(mockFileBytes)) {

			Workbook resWorkbook = WorkbookFactory.create(resInputStream);
			Workbook mockWorkbook = WorkbookFactory.create(mockInputStream);
			Sheet resSheet = resWorkbook.getSheetAt(0); // Assuming single sheet
			Sheet mockSheet = mockWorkbook.getSheetAt(0); // Assuming single sheet

			// 1. Compare the number of rows
			int resRowCount = resSheet.getPhysicalNumberOfRows();
			int mockRowCount = mockSheet.getPhysicalNumberOfRows();
			assertEquals(mockRowCount, resRowCount, "Row counts do not match!");

			// 2. Iterate through each row and compare cell-by-cell
			for (int i = 0; i < resRowCount; i++) {
				Row resRow = resSheet.getRow(i);
				Row mockRow = mockSheet.getRow(i);

				// Compare the number of cells in each row
				int resCellCount = resRow.getPhysicalNumberOfCells();
				int mockCellCount = mockRow.getPhysicalNumberOfCells();
				assertEquals(mockCellCount, resCellCount, "Cell counts do not match at row " + i);

				// 3. Compare each cell within the row
				for (int j = 0; j < resCellCount; j++) {
					Cell resCell = resRow.getCell(j);
					Cell mockCell = mockRow.getCell(j);

					// Compare the cell values
					String resCellValue = getCellValueAsString(resCell);
					String mockCellValue = getCellValueAsString(mockCell);
					assertEquals(mockCellValue, resCellValue,
							"Cell values do not match at row " + i + " and column " + j);
				}
			}
		}
	}

	// Helper method to get cell value as String
	private String getCellValueAsString(Cell cell) {
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				return String.valueOf(cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return "";
		default:
			return "Unsupported cell type";
		}
	}
}
