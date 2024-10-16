package com.acnovate.audit_manager.service.impl;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.repository.SchedulingAuditReportRepository;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public class SchedulingAuditReportServiceImpl extends AbstractRawService<SchedulingAuditReport> implements ISchedulingAuditReportService {

  @Autowired
  private SchedulingAuditReportRepository schedulingAuditReportRepository;

  @Override
  public SchedulingAuditReportResponse createSchedulingAuditReport(SchedulingAuditReportRequest request) {
    // Validate that recipients list is not empty
    if (request.getRecipients() == null || request.getRecipients()
        .isEmpty()) {
      throw new CustomErrorHandleException("Email Must be Required");
    }

    // Validate each recipient's email
    for (String email : request.getRecipients()) {
      // Basic email structure validation using regex
      if (!email.matches(".+@.+\\..+")) {
        throw new CustomErrorHandleException("Validation failed: " + email + " is not a valid email address.");
      }
    }

    // Proceed with creating and saving the report
    SchedulingAuditReport report = new SchedulingAuditReport();
    report.setReportId(request.getReportId());
    report.setFrequency(request.getFrequency());
    report.setSchedulingHour(request.getSchedulingHour());
    report.setSchedulingMinute(request.getSchedulingMinute());
    report.setTimeMarker(request.getTimeMarker());
    report.setRecipients(String.join(",", request.getRecipients()));

    // Convert the saved entity to a response and return it
    return new SchedulingAuditReportResponse(schedulingAuditReportRepository.save(report));
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
