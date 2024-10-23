package com.acnovate.audit_manager.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.dto.response.SchedulingAuditReportResponse;
import com.acnovate.audit_manager.repository.SchedulingAuditReportRepository;
import com.acnovate.audit_manager.service.IAuditReportService;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;

@Service
public class SchedulingAuditReportServiceImpl extends AbstractRawService<SchedulingAuditReport>
		implements ISchedulingAuditReportService {

	protected final Logger logger = LoggerFactory.getLogger(SchedulingAuditReportServiceImpl.class);

	@Autowired
	private SchedulingAuditReportRepository schedulingAuditReportRepository;

	@Autowired
	private IAuditReportService auditReportService;

	@Autowired
	private EmailService emailService;

	@Override
	protected JpaRepository<SchedulingAuditReport, Long> getDao() {
		return schedulingAuditReportRepository;
	}

	@Override
	protected JpaSpecificationExecutor<SchedulingAuditReport> getSpecificationExecutor() {
		return schedulingAuditReportRepository;
	}

	@Override
	public SchedulingAuditReportResponse domainToDto(SchedulingAuditReport schedulingAuditReport) {
		SchedulingAuditReportResponse schedulingAuditReportResponse = new SchedulingAuditReportResponse();
		schedulingAuditReportResponse.setId(schedulingAuditReport.getId());

		// Convert report IDs from a comma-separated string to a List<Integer>
		if (schedulingAuditReport.getReportIds() != null && !schedulingAuditReport.getReportIds().isEmpty()) {
			List<Integer> reportIds = Arrays.stream(schedulingAuditReport.getReportIds().split(",")).map(String::trim) // Trim
																														// any
																														// whitespace
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
		schedulingAuditReportResponse.setRecipients(Arrays.asList(schedulingAuditReport.getRecipients().split(",")));

		return schedulingAuditReportResponse;
	}

	@Override
	public SchedulingAuditReportResponse createSchedulingAuditReport(SchedulingAuditReportRequest request) {
		// Validate that the recipients list is not empty
		if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
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
		report.setReportIds(request.getReportIds().stream().map(String::valueOf) // Convert Integer to String
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
	public void sendScheduledReport(SchedulingAuditReport schedulingAuditReport) {
		List<Long> reportIds = Arrays.stream(schedulingAuditReport.getReportIds().split(",")) // Split the string
				.map(Long::valueOf) // Convert String to Long
				.collect(Collectors.toList());
		byte[] reportBytes = auditReportService.genereteReport(1L, reportIds, "xlsx");

		// Path where the XLSX file will be saved
		File filePath = new File(System.getProperty("java.io.tmpdir"), "report.xlsx");

		// Convert the byte[] to XLSX file
		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
			fileOutputStream.write(reportBytes);
			logger.info("XLSX file written successfully to {}", filePath);
			emailService.sendEmailWithAttachment(schedulingAuditReport.getRecipients(),
					"Audit scheduled report is ready",
					constructScheduleReportBody(auditReportService.getReportNamesByIds(reportIds)), filePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			filePath.delete();// remove sent file
		}

	}

	private String constructScheduleReportBody(List<String> reportNames) {
		StringBuilder reportListBuilder = new StringBuilder();
		reportListBuilder.append("<!DOCTYPE html>");
		reportListBuilder.append("<html>");
		reportListBuilder.append("<head>");
		reportListBuilder.append("    <style>");
		reportListBuilder.append("        body { font-family: Arial, sans-serif; }");
		reportListBuilder.append("        .report-list { margin-left: 20px; }");
		reportListBuilder.append("        .report-list li { margin-bottom: 5px; }");
		reportListBuilder.append("    </style>");
		reportListBuilder.append("</head>");
		reportListBuilder.append("<body>");
		reportListBuilder.append("    <p>Dear User,</p>");
		reportListBuilder.append(
				"    <p>Your scheduled report has been successfully generated. The following reports are available for download:</p>");

		// Build the HTML list items for report names
		reportListBuilder.append("<ul class='report-list'>");
		for (String reportName : reportNames) {
			reportListBuilder.append("<li>").append(reportName).append("</li>");
		}
		reportListBuilder.append("</ul>");

		reportListBuilder
				.append("    <p>You can download the reports in the attached <strong>xlsx</strong> format.</p>");
		reportListBuilder.append("    <p>Best regards,</p>");

		reportListBuilder.append("    <p>The Audit Management Team</p>");
		reportListBuilder.append("</body>");
		reportListBuilder.append("</html>");

		return reportListBuilder.toString();
	}

}
