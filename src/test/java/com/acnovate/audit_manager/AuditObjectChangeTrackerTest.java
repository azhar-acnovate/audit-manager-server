package com.acnovate.audit_manager;

import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditLogActivityResponseDto;
import com.acnovate.audit_manager.repository.AuditObjectChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.acnovate.audit_manager.common.dto.CommonResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuditObjectChangeTrackerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditObjectChangeTrackerRepository objectRepository;

    @BeforeEach
    public void setUp() {
        objectRepository.deleteAll();
    }

    @Test
    void testCreateAuditObjectChangeTracker() throws Exception {
        // Create a request DTO to send in the POST request
        AuditObjectChangeRequestDto requestDto = new AuditObjectChangeRequestDto();
        requestDto.setRefObjectId(1L);
        requestDto.setEventType("CREATE");
        requestDto.setEventOccurence(new Date());

        // Create a mock response DTO that should be returned by the service
        AuditLogActivityResponseDto responseDto = new AuditLogActivityResponseDto();
        responseDto.setId(1L); // Assuming this is the ID of the created log
        responseDto.setRefObjectId(1L);
        responseDto.setEventType("CREATE");
        responseDto.setEventOccurence(requestDto.getEventOccurence());

        // Mock the service method
        when(auditObjectChangeTrackerService.createAuditObjectChangeTracker(any(AuditObjectChangeRequestDto.class)))
            .thenReturn(responseDto);

        // Perform the POST request
        MvcResult result = mockMvc.perform(post("/audit-object-change-tracker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        // Check the status of the response
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus(), "Status should be Created");
        
        // Deserialize the response and validate the returned data
        String jsonResponse = result.getResponse().getContentAsString();
        CommonResponse response = objectMapper.readValue(jsonResponse, CommonResponse.class);
        
        Assertions.assertNotNull(response, "Response should not be null");
        Assertions.assertEquals(200, response.getStatus(), "Response status should be 200");
        Assertions.assertEquals("Successfully Created Audit Object Change Tracker", response.getMessage(), "Message should match");
        
        // Validate the data returned in the response
        AuditLogActivityResponseDto returnedLog = (AuditLogActivityResponseDto) response.getData();
        Assertions.assertNotNull(returnedLog, "Returned log should not be null");
        Assertions.assertEquals(1L, returnedLog.getId(), "Returned log ID should be 1");
        Assertions.assertEquals(1L, returnedLog.getRefObjectId(), "Returned log refObjectId should be 1");
        Assertions.assertEquals("CREATE", returnedLog.getEventType(), "Returned log eventType should be CREATE");
    }

    // Test to find all the object change logs
    @Test
    void testFindAll() throws Exception {
        // Create mock data
        AuditObjectChangeTracker objectChangeTracker1 = new AuditObjectChangeTracker();
        objectChangeTracker1.setId(1L); // Set ID for the mock
        objectChangeTracker1.setRefObjectId(1L);
        objectChangeTracker1.setEventType("CREATE");
        objectChangeTracker1.setEventOccurence(new Date());

        AuditObjectChangeTracker objectChangeTracker2 = new AuditObjectChangeTracker();
        objectChangeTracker2.setId(2L); // Set ID for the mock
        objectChangeTracker2.setRefObjectId(2L);
        objectChangeTracker2.setEventType("UPDATE");
        objectChangeTracker2.setEventOccurence(new Date());

        // Mock the service method to return the list of AuditObjectChangeTracker
        when(auditObjectChangeTrackerService.findAll()).thenReturn(Arrays.asList(objectChangeTracker1, objectChangeTracker2));

        // Perform the request
        MvcResult result = mockMvc.perform(get("/audit-object-change-tracker"))
                .andReturn();

        // Check the status of the response
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(), "Status should be OK");
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType(), "Content type should be application/json");

        // Deserialize the response and validate the attributes
        String jsonResponse = result.getResponse().getContentAsString();
        AuditObjectChangeTracker[] logs = objectMapper.readValue(jsonResponse, AuditObjectChangeTracker[].class);

        Assertions.assertNotNull(logs, "Log list should not be null");
        Assertions.assertEquals(2, logs.length, "Log list should contain two entries");

        // Assert properties of the first log
        Assertions.assertEquals(1L, logs[0].getId(), "First log ID should be 1");
        Assertions.assertEquals(1L, logs[0].getRefObjectId(), "First log refObjectId should be 1");
        Assertions.assertEquals("CREATE", logs[0].getEventType(), "First log eventType should be CREATE");

        // Assert properties of the second log
        Assertions.assertEquals(2L, logs[1].getId(), "Second log ID should be 2");
        Assertions.assertEquals(2L, logs[1].getRefObjectId(), "Second log refObjectId should be 2");
        Assertions.assertEquals("UPDATE", logs[1].getEventType(), "Second log eventType should be UPDATE");
    }

    // Test to find one of the object change logs using ID
    @Test
    void testFindOne() throws Exception {
        AuditObjectChangeTracker objectChangeTracker = new AuditObjectChangeTracker();
        objectChangeTracker.setRefObjectId(1L);
        objectChangeTracker.setEventType("CREATE");
        objectChangeTracker.setEventOccurence(new Date());
        objectChangeTracker = objectRepository.save(objectChangeTracker); // Save to retrieve the ID

        when(auditObjectChangeTrackerService.findOne(1L)).thenReturn(objectChangeTracker);
        
        MvcResult result = mockMvc.perform(get("/audit-object-change-tracker/{id}", objectChangeTracker.getId()))
                .andReturn();

        // Check the status of the response
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(), "Status should be OK");
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType(), "Content type should be application/json");

        // Deserialize the response and validate the attributes
        String jsonResponse = result.getResponse().getContentAsString();
        AuditObjectChangeTracker retrievedLog = objectMapper.readValue(jsonResponse, AuditObjectChangeTracker.class);

        Assertions.assertNotNull(retrievedLog, "Retrieved log should not be null");
        Assertions.assertEquals("CREATE", retrievedLog.getEventType(), "Event type should match");
    }
}
