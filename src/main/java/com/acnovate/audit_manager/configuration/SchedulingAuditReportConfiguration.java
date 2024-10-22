package com.acnovate.audit_manager.configuration;

import java.util.TimeZone;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;

@Component
public class SchedulingAuditReportConfiguration implements ApplicationListener<ApplicationReadyEvent> {
	protected final Logger logger = LoggerFactory.getLogger(SchedulingAuditReportConfiguration.class);

	@Autowired
	private ISchedulingAuditReportService schedulingAuditReportService;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	/**
	 * This method is executed when the application is ready to service requests. It
	 * retrieves all scheduling audit reports and schedules each one based on its
	 * configuration.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		logger.info("Application started, scheduling audit reports...");
		// Loop through all the reports and schedule them
		for (SchedulingAuditReport schedulingAuditReport : schedulingAuditReportService.findAll()) {
			logger.info("Scheduling report with IDs: {}", schedulingAuditReport.getReportIds());
			try {
				scheduleReportTask(schedulingAuditReport);
			} catch (Exception e) {
				logger.error("Error while scheduleReportTask :: Exception::{}", ExceptionUtils.getStackTrace(e));
			}
		}
		logger.info("All audit reports have been scheduled.");
		return;
	}

	/**
	 * This method handles scheduling an individual audit report based on its
	 * settings.
	 *
	 * @param schedulingAuditReport The report to be scheduled.
	 */
	private void scheduleReportTask(SchedulingAuditReport schedulingAuditReport) {
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
		String frequency = schedulingAuditReport.getFrequency().toLowerCase();

		// Build the cron expression based on the frequency
		String cronExpression = "0 " + minute + " " + hour;
		switch (frequency) {
		case "daily":
			cronExpression += " * * *"; // Every day at the specified time
			break;

		case "weekly":
			cronExpression += " * * MON"; // Every Monday at the specified time
			break;

		case "monthly":
			cronExpression += " 1 * *"; // On the 1st of each month at the specified time
			break;

		default:
			// Log an error and throw an exception if the frequency is invalid
			logger.error("Invalid frequency: {} for report: {}", frequency, schedulingAuditReport.getId());
			throw new IllegalArgumentException("Invalid frequency: " + frequency);
		}

		// Log the cron expression for debugging purposes
		logger.info("Cron expression for report {}: {}", schedulingAuditReport.getId(), cronExpression);

		// Create a CronTrigger based on the cron expression and the default timezone
		CronTrigger cronTrigger = new CronTrigger(cronExpression, TimeZone.getDefault());

		// Define the scheduling task - sending the report
		Runnable task = () -> {
			logger.info("Executing scheduled report task for: {}", schedulingAuditReport.getId());
			schedulingAuditReportService.sendScheduledReport(schedulingAuditReport);
			logger.info("Report {} sent successfully.", schedulingAuditReport.getId());
		};

		// Schedule the task with the task scheduler
		taskScheduler.schedule(task, cronTrigger);

		// Log that the task has been scheduled
		logger.info("Scheduled report task for: {}", schedulingAuditReport.getId());
	}
}
