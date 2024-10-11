package com.acnovate.audit_manager;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditAttributeChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditAttributeChangeTrackerResponseDto;
import com.acnovate.audit_manager.repository.AuditAttributeChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
class AuditAttributeChangeTrackerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditAttributeChangeTrackerRepository attRepository;

    @MockBean // Change this to MockBean
    private IAuditAttributeChangeTrackerService auditAttributeChangeTrackerService;

    @BeforeEach
    public void setUp() {
        attRepository.deleteAll();
    }

    // Test to generate a new change attribute log
    @Test
    void testCreateAuditAttributeChangeTracker() throws Exception {
        // Create a request DTO to send in the POST request
        AuditAttributeChangeRequestDto attributeChangeTrackerRequest = new AuditAttributeChangeRequestDto();
        attributeChangeTrackerRequest.setAttributeName("status");
        attributeChangeTrackerRequest.setOldValue("pending");
        attributeChangeTrackerRequest.setNewValue("approved");
        attributeChangeTrackerRequest.setChangedBy("user123");
        
        // Create a mock response DTO that should be returned by the service
        AuditAttributeChangeTrackerResponseDto attributeChangeTrackerResponse = new AuditAttributeChangeTrackerResponseDto();
        attributeChangeTrackerResponse.setId(1L); // Assuming this is the ID of the created log
        attributeChangeTrackerResponse.setAttributeName("status");
        attributeChangeTrackerResponse.setOldValue("pending");
        attributeChangeTrackerResponse.setNewValue("approved");
        attributeChangeTrackerResponse.setChangedBy("user123");

        // Mock the service method
        when(auditAttributeChangeTrackerService.createAuditAttributeChangeTracker(any(AuditAttributeChangeRequestDto.class)))
            .thenReturn(attributeChangeTrackerResponse);
        
        // Perform the POST request
        MvcResult result = mockMvc.perform(post("/audit-attribute-change-tracker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attributeChangeTrackerRequest)))
                .andReturn();

        // Check the status of the response
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus(), "Status should be Created");
        
        // Deserialize the response and validate the returned data
        String jsonResponse = result.getResponse().getContentAsString();
        CommonResponse response = objectMapper.readValue(jsonResponse, CommonResponse.class);
        
        Assertions.assertNotNull(response, "Response should not be null");
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus(), "Response status should be 201");
        Assertions.assertEquals("Successfully Created Audit Attribute Change Tracker", response.getMessage(), "Message should match");

        // Validate the data returned in the response
        AuditAttributeChangeTrackerResponseDto returnedLog = (AuditAttributeChangeTrackerResponseDto) response.getData();
        Assertions.assertNotNull(returnedLog, "Returned log should not be null");
        Assertions.assertEquals("status", returnedLog.getAttributeName(), "Returned attribute name should match");
        Assertions.assertEquals("pending", returnedLog.getOldValue(), "Returned old value should match");
        Assertions.assertEquals("approved", returnedLog.getNewValue(), "Returned new value should match");
        Assertions.assertEquals("user123", returnedLog.getChangedBy(), "Returned changed by should match");
    }

    // Test to find all the attribute change logs
    @Test
    void testFindAll() throws Exception {
        // Create and save two change attribute logs
        AuditAttributeChangeTracker attributeChangeTracker1 = new AuditAttributeChangeTracker();
        attributeChangeTracker1.setAttributeName("status");
        attributeChangeTracker1.setOldValue("pending");
        attributeChangeTracker1.setNewValue("approved");
        attributeChangeTracker1.setChangedBy("user123");
        attRepository.save(attributeChangeTracker1);
        
        AuditAttributeChangeTracker attributeChangeTracker2 = new AuditAttributeChangeTracker();
        attributeChangeTracker2.setAttributeName("priority");
        attributeChangeTracker2.setOldValue("low");
        attributeChangeTracker2.setNewValue("high");
        attributeChangeTracker2.setChangedBy("user456");
        attRepository.save(attributeChangeTracker2);

        // Mock the service method to return the list of AuditAttributeChangeTracker
        when(auditAttributeChangeTrackerService.findAll()).thenReturn(Arrays.asList(attributeChangeTracker1, attributeChangeTracker2));

        // Perform the request
        MvcResult result = mockMvc.perform(get("/audit-attribute-change-tracker"))
                .andReturn();

        // Check the status of the response
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(), "Status should be OK");
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType(), "Content type should be application/json");

        // Deserialize the response and validate the attributes
        String jsonResponse = result.getResponse().getContentAsString();
        AuditAttributeChangeTracker[] logs = objectMapper.readValue(jsonResponse, AuditAttributeChangeTracker[].class);

        Assertions.assertNotNull(logs, "Log list should not be null");
        Assertions.assertEquals(2, logs.length, "Log list should contain two entries");

        // Validate first entry
        Assertions.assertEquals("status", logs[0].getAttributeName(), "First log's attribute name should match");
        Assertions.assertEquals("pending", logs[0].getOldValue(), "First log's old value should match");
        Assertions.assertEquals("approved", logs[0].getNewValue(), "First log's new value should match");
        Assertions.assertEquals("user123", logs[0].getChangedBy(), "First log's changed by should match");

        // Validate second entry
        Assertions.assertEquals("priority", logs[1].getAttributeName(), "Second log's attribute name should match");
        Assertions.assertEquals("low", logs[1].getOldValue(), "Second log's old value should match");
        Assertions.assertEquals("high", logs[1].getNewValue(), "Second log's new value should match");
        Assertions.assertEquals("user456", logs[1].getChangedBy(), "Second log's changed by should match");
    }
}
