package com.acnovate.audit_manager.service.impl;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.repository.SchedulingAuditReportRepository;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public class SchedulingAuditReportServiceImpl extends AbstractRawService<SchedulingAuditReport> implements ISchedulingAuditReportService {

  @Autowired
  private SchedulingAuditReportRepository schedulingAuditReportRepository;

  @Override
  public SchedulingAuditReportResponse domainToDto(SchedulingAuditReport schedulingAuditReport) {
    SchedulingAuditReportResponse schedulingAuditReportResponse = new SchedulingAuditReportResponse();
    schedulingAuditReportResponse.setId(schedulingAuditReport.getId());

    // Convert report IDs from a comma-separated string to a List<Integer>
    if (schedulingAuditReport.getReportIds() != null && !schedulingAuditReport.getReportIds()
        .isEmpty()) {
      List<Integer> reportIds = Arrays.stream(schedulingAuditReport.getReportIds()
          .split(","))
          .map(String::trim) // Trim any whitespace
          .map(Integer::valueOf) // Convert String to Integer
          .collect(Collectors.toList());
      schedulingAuditReportResponse.setReportId(reportIds);
    } else {
      schedulingAuditReportResponse.setReportId(new ArrayList<>()); // Set empty list if no IDs
    }

    schedulingAuditReportResponse.setFrequency(schedulingAuditReport.getFrequency());
    schedulingAuditReportResponse.setSchedulingHour(schedulingAuditReport.getSchedulingHour());
    schedulingAuditReportResponse.setSchedulingMinute(schedulingAuditReport.getSchedulingMinute());
    schedulingAuditReportResponse.setTimeMarker(schedulingAuditReport.getTimeMarker());
    // Convert the recipients string back to a list
    schedulingAuditReportResponse.setRecipients(Arrays.asList(schedulingAuditReport.getRecipients()
        .split(",")));

    return schedulingAuditReportResponse;
  }

  @Override
  public SchedulingAuditReportResponse createSchedulingAuditReport(SchedulingAuditReportRequest request) {
    // Validate that the recipients list is not empty
    if (request.getRecipients() == null || request.getRecipients()
        .isEmpty()) {
      throw new CustomErrorHandleException("Email list must not be empty.");
    }

    // Validate each recipient's email format using regex
    for (String email : request.getRecipients()) {
      if (!isValidEmail(email)) {
        throw new CustomErrorHandleException("Validation failed: " + email + " is not a valid email address.");
      }
    }

    // Create the SchedulingAuditReport entity
    SchedulingAuditReport report = new SchedulingAuditReport();

    // Join report IDs into a comma-separated string
    report.setReportIds(request.getReportId()
        .stream()
        .map(String::valueOf) // Convert Integer to String
        .collect(Collectors.joining(",")));

    report.setFrequency(request.getFrequency());
    report.setSchedulingHour(request.getSchedulingHour());
    report.setSchedulingMinute(request.getSchedulingMinute());
    report.setTimeMarker(request.getTimeMarker());

    // Convert the list of recipients into a comma-separated string
    report.setRecipients(String.join(",", request.getRecipients()));

    // Save the report and return the corresponding DTO
    return domainToDto(schedulingAuditReportRepository.save(report));
  }

  // Email validation method
  private boolean isValidEmail(String email) {
    // A more comprehensive regex for email validation
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
  }


  @Override
  protected JpaRepository<SchedulingAuditReport, Long> getDao() {
    return schedulingAuditReportRepository;
  }

  @Override
  protected JpaSpecificationExecutor<SchedulingAuditReport> getSpecificationExecutor() {
    return schedulingAuditReportRepository;
  }
}
