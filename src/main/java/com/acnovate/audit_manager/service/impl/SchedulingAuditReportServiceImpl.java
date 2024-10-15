package com.acnovate.audit_manager.service.impl;

import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.repository.SchedulingAuditReportRepository;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulingAuditReportServiceImpl extends AbstractRawService<SchedulingAuditReport> implements ISchedulingAuditReportService {

    private final SchedulingAuditReportRepository schedulingAuditReportRepository;

    @Override
    public SchedulingAuditReportResponse createSchedulingAuditReport(SchedulingAuditReportRequest request) {
        // Validate that recipients list is not empty
        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            throw new IllegalArgumentException("Email Must be Required");
        }

        // Validate each recipient's email
        for (String email : request.getRecipients()) {
            // Basic email structure validation using regex
            if (!email.matches(".+@.+\\..+")) {
                throw new IllegalArgumentException("Validation failed: " + email + " is not a valid email address.");
            }

            // Extract the domain of the email
            String domain = email.substring(email.indexOf("@"));

            // Validate domain: only accept "@gmail.com" and "@acnovate.com"
            if (!domain.equalsIgnoreCase("@gmail.com") && !domain.equalsIgnoreCase("@acnovate.com")) {
                throw new IllegalArgumentException("Invalid email: " + email + " must be from '@gmail.com' or '@acnovate.com'");
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
        return null;
    }

    @Override
    protected JpaSpecificationExecutor<SchedulingAuditReport> getSpecificationExecutor() {
        return null;
    }
}

