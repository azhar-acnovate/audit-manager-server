package com.acnovate.audit_manager.service.impl;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.repository.SchedulingAuditReportRepository;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;

import java.util.Arrays;

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
    schedulingAuditReportResponse.setReportIds(Arrays.asList(schedulingAuditReport.getReportIds()
        .split(",")));
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
      throw new CustomErrorHandleException("Email Must be Required");
    }

    // Validate each recipient's email format using regex
    for (String email : request.getRecipients()) {
      if (!email.matches(".+@.+\\..+")) {
        throw new CustomErrorHandleException("Validation failed: " + email + " is not a valid email address.");
      }
    }

    // Create the SchedulingAuditReport entity
    SchedulingAuditReport report = new SchedulingAuditReport();
    report.setReportIds(String.join(",", request.getReportIds()));
    report.setFrequency(request.getFrequency());
    report.setSchedulingHour(request.getSchedulingHour());
    report.setSchedulingMinute(request.getSchedulingMinute());
    report.setTimeMarker(request.getTimeMarker());

    // Convert the list of recipients into a comma-separated string
    report.setRecipients(String.join(",", request.getRecipients()));

    return domainToDto(schedulingAuditReportRepository.save(report));
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
