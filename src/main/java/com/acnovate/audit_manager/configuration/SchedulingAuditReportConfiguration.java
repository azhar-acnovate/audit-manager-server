package com.acnovate.audit_manager.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.acnovate.audit_manager.common.dto.FilterDto;
import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.service.IAuditReportService;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;
import com.acnovate.audit_manager.service.impl.EmailService;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

@Component
public class SchedulingAuditReportConfiguration implements ApplicationListener<ApplicationReadyEvent> {
	protected final Logger logger = LoggerFactory.getLogger(SchedulingAuditReportConfiguration.class);

	@Autowired
	private ISchedulingAuditReportService schedulingAuditReportService;

	private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>(); // Store scheduled tasks by
	// report ID
	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private IAuditReportService auditReportService;

	@Autowired
	private EmailService emailService;

	// Mapping abbreviated days to full names
	private static Map<String, String> dayMap = new HashMap<>();
	static {
		dayMap.put("MON", "Monday");
		dayMap.put("TUE", "Tuesday");
		dayMap.put("WED", "Wednesday");
		dayMap.put("THU", "Thursday");
		dayMap.put("FRI", "Friday");
		dayMap.put("SAT", "Saturday");
		dayMap.put("SUN", "Sunday");
	}

	/**
	 * This method is executed when the application is ready to service requests. It
	 * retrieves all scheduling audit reports and schedules each one based on its
	 * configuration.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		logger.info("Application started, scheduling audit reports...");
		FilterDto filterDto = new FilterDto();
		filterDto.getFilter().put("active", true);
		// Loop through all the reports and schedule them
		for (SchedulingAuditReport schedulingAuditReport : schedulingAuditReportService.findAll(filterDto)) {
			logger.info("Scheduling report with IDs: {}", schedulingAuditReport.getReportIds());
			try {
				scheduleTask(schedulingAuditReport);
			} catch (Exception e) {
				logger.error("Error while scheduleReportTask :: Exception::{}", ExceptionUtils.getStackTrace(e));
			}
		}
		logger.info("All audit reports have been scheduled.");

	}

	// Schedule a new task
	public void scheduleTask(SchedulingAuditReport schedulingAuditReport) {
		CronTrigger cronTrigger = new CronTrigger(generateCronExpression(schedulingAuditReport), TimeZone.getDefault());

		// Define the scheduling task - sending the report
		Runnable task = () -> {
			logger.info("Executing scheduled report task for: {}", schedulingAuditReport.getId());
			sendScheduledReport(schedulingAuditReport);
			logger.info("Report {} sent successfully.", schedulingAuditReport.getId());
		};

		// Schedule the task and store it
		ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, cronTrigger);
		logger.info("New Scheduling report added to scheduler with id {} and cron trigger {}",
				schedulingAuditReport.getId(), getReadableCron(schedulingAuditReport));
		scheduledTasks.put(schedulingAuditReport.getId(), scheduledTask);

		schedulingAuditReport.setActive(true);
	}

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

	public String getReadableCron(SchedulingAuditReport schedulingAuditReport) {
		CronTrigger cronTrigger = new CronTrigger(generateCronExpression(schedulingAuditReport), TimeZone.getDefault());
		String cronExpression = cronTrigger.getExpression();
		// Define a cron definition for UNIX cron expressions
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);

		// Create a CronParser based on the definition
		CronParser parser = new CronParser(cronDefinition);

		// Parse the cron expression
		Cron cron = parser.parse(cronExpression);

		// Use CronDescriptor to get a human-readable description
		CronDescriptor descriptor = CronDescriptor.instance(Locale.ENGLISH);
		String readableDescription = descriptor.describe(cron);

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");

		LocalTime time = LocalTime.parse(readableDescription.split("at")[1].trim(), inputFormatter);
		String time12Hour = time.format(outputFormatter);
		switch (schedulingAuditReport.getFrequencyType()) {
		case "DAILY":
			readableDescription = "every day at " + time12Hour;
			break;

		case "WEEKLY":
			readableDescription = "every week on "
					+ dayMap.getOrDefault(schedulingAuditReport.getFrequency(), schedulingAuditReport.getFrequency())
					+ " at " + time12Hour;
			break;

		case "MONTHLY":
			readableDescription = "on the " + schedulingAuditReport.getFrequency() + " of every month at " + time12Hour;
			break;

		default:
			// Log an error and throw an exception if the frequency is invalid
			logger.error("Invalid frequency: {} for report: {}", schedulingAuditReport.getFrequencyType(),
					schedulingAuditReport.getId());
			throw new CustomErrorHandleException("Invalid frequency: " + schedulingAuditReport.getFrequencyType());
		}

		return readableDescription;
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

	// Cancel an existing task
	public void cancelTask(Long reportId) {
		ScheduledFuture<?> scheduledTask = scheduledTasks.get(reportId);

		if (scheduledTask != null && !scheduledTask.isCancelled()) {
			logger.info("Report id {} is cancalled..!", reportId);
			scheduledTask.cancel(true);
		}
		SchedulingAuditReport schedulingAuditReport = schedulingAuditReportService.findOne(reportId);
		schedulingAuditReport.setActive(false);
	}

	// Reschedule a task (cancel and schedule again)
	public void rescheduleTask(SchedulingAuditReport schedulingAuditReport) {
		cancelTask(schedulingAuditReport.getId());
		scheduleTask(schedulingAuditReport);
	}

	// Generate the cron expression based on report's settings
	private String generateCronExpression(SchedulingAuditReport schedulingAuditReport) {
		// Get scheduling time details from the report
		int hour = schedulingAuditReport.getSchedulingHour();
		int minute = schedulingAuditReport.getSchedulingMinute();
		String timeMarker = schedulingAuditReport.getTimeMarker(); // "AM" or "PM"

		// Adjust the hour value for AM/PM format
		if ("PM".equalsIgnoreCase(timeMarker) && hour != 12) {
			hour += 12; // Convert PM to 24-hour format
		}
		if ("AM".equalsIgnoreCase(timeMarker) && hour == 12) {
			hour = 0; // Midnight
		}

		// Get the frequency (daily, weekly, or monthly)
		String frequency = schedulingAuditReport.getFrequencyType();

		// Build the cron expression based on the frequency
		String cronExpression = "0 " + minute + " " + hour;
		switch (frequency) {
		case "DAILY":
			cronExpression += " * * *"; // Every day at the specified time
			break;

		case "WEEKLY":
			cronExpression += " * * " + schedulingAuditReport.getFrequency();
			break;

		case "MONTHLY":
			cronExpression += " " + schedulingAuditReport.getFrequency() + " * *"; // On the 1st of each month at the
																					// specified time
			break;

		default:
			// Log an error and throw an exception if the frequency is invalid
			logger.error("Invalid frequency: {} for report: {}", frequency, schedulingAuditReport.getId());
			throw new CustomErrorHandleException("Invalid frequency: " + frequency);
		}

		return cronExpression;
	}

}
