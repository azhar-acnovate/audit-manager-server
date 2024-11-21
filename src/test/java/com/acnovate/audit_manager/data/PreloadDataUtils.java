package com.acnovate.audit_manager.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.domain.AuditReport;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.domain.SourceAdditionalInfo;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditReportService;
import com.acnovate.audit_manager.service.IFileGenerationService;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;
import com.google.common.collect.Lists;

@Service
public class PreloadDataUtils {

	@Autowired
	private IAuditReportService auditReportService;

	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

	@Autowired
	private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

	@Autowired
	private IAuditAttributeChangeTrackerService attributeChangeTrackerService;

	@Autowired
	private ISchedulingAuditReportService schedulingAuditReportService;

	@Autowired
	private IFileGenerationService fileGenerationService;

	public AuditReport loadAuditReportOne() {

		AuditReport report = new AuditReport();
		report.setReportName("Audit Report test");
		report.setStartDateRange(new Date());
		report.setEndDateRange(new Date(System.currentTimeMillis() + 86400000L)); // 1 day ahead

		report.setRefObjectIds(Arrays.asList(1L));

		// Creating full names for changed users
		List<String> fullUserNames = Lists.newArrayList("Alice Smith", "Bob Johnson");
		report.setChangedUserNames(fullUserNames);
		report = auditReportService.create(report);
		return report;
	}

	public SourceReferenceObject loadSourceReferenceOne() {
		SourceReferenceObject reference = new SourceReferenceObject();
		// reference.setId((long) i);
		reference.setSourceReferenceName("Source Name 1");
		reference.setSourceReferenceKey("Key-1");

		// Creating dummy additional info
		List<SourceAdditionalInfo> additionalInfoList = new ArrayList<>();
		additionalInfoList.add(new SourceAdditionalInfo("DetailKey1", "DetailValue1"));
		additionalInfoList.add(new SourceAdditionalInfo("DetailKey2", "DetailValue1"));

		reference.setAdditionalInfo(additionalInfoList);
		return sourceReferenceObjectService.create(reference);
	}

	public List<AuditReport> loadAuditReportData() {
		List<AuditReport> data = new ArrayList<>();
		// Example full names
		String[] firstNames = { "Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona", "George", "Hannah", "Isaac",
				"Julia" };
		String[] lastNames = { "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore",
				"Taylor" };

		for (int i = 0; i < 10; i++) {
			AuditReport report = new AuditReport();
			// report.setId((long) (i + 1));
			report.setReportName("Audit Report " + (i + 1));
			report.setStartDateRange(new Date());
			report.setEndDateRange(new Date(System.currentTimeMillis() + 86400000L)); // 1 day ahead

			report.setRefObjectIds(Arrays.asList((long) (i + 1)));

			// Creating full names for changed users
			List<String> fullUserNames = new ArrayList<>();
			for (int j = 0; j < 2; j++) { // Assuming two changed users per report
				String fullName = firstNames[(i + j) % firstNames.length] + " " + lastNames[(i + j) % lastNames.length];
				fullUserNames.add(fullName);
			}
			report.setChangedUserNames(fullUserNames);
			report = auditReportService.create(report);
			System.out.println(report);
			data.add(report);

		}

		return data;
	}

	public List<SourceReferenceObject> loadSourceReferenceObjectData() {
		List<SourceReferenceObject> data = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			SourceReferenceObject reference = new SourceReferenceObject();
			// reference.setId((long) i);
			reference.setSourceReferenceName("Source Name " + i);
			reference.setSourceReferenceKey("Key-" + i);

			// Creating dummy additional info
			List<SourceAdditionalInfo> additionalInfoList = new ArrayList<>();
			additionalInfoList.add(new SourceAdditionalInfo("DetailKey1", "DetailValue" + i));
			additionalInfoList.add(new SourceAdditionalInfo("DetailKey2", "DetailValue" + (i + 1)));

			reference.setAdditionalInfo(additionalInfoList);
			reference = sourceReferenceObjectService.create(reference);
			data.add(reference);
		}
		return data;
	}

	public List<SchedulingAuditReport> loadSchedulingAuditReport() {
		List<SchedulingAuditReport> data = new ArrayList<>();
		String[] frequencies = { "DAILY,WEEKLY,MONTHLY" };
		String[] timeMarkers = { "AM,PM" };
		// Create a Random object
		Random random = new Random();
		for (int i = 1; i <= 10; i++) {
			SchedulingAuditReport schedulingAuditReport = new SchedulingAuditReport();
			// reference.setId((long) i);
			schedulingAuditReport.setFrequency(frequencies[random.nextInt(frequencies.length)]);
			schedulingAuditReport.setTimeMarker(timeMarkers[random.nextInt(timeMarkers.length)]);
			schedulingAuditReport.setSchedulingHour(random.nextInt(12));
			schedulingAuditReport.setSchedulingMinute(random.nextInt(59));
			schedulingAuditReport.setRecipients("test@gmail.com");
			schedulingAuditReport.setReportIds("1");
			schedulingAuditReport = schedulingAuditReportService.create(schedulingAuditReport);
			data.add(schedulingAuditReport);
		}
		return data;
	}

	public List<Long> loadExportReportData() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String eventOccurence = "17/10/2024";
		String startDate = "15/10/2024";
		String endDate = "18/10/2024";

		SourceReferenceObject sourceReferenceObject = loadSourceReferenceOne();
		AuditObjectChangeTracker auditObjectChangeTracker = new AuditObjectChangeTracker();
		auditObjectChangeTracker.setEventOccurence(formatter.parse(eventOccurence));
		auditObjectChangeTracker.setEventType("create");
		auditObjectChangeTracker.setRefObjectId(sourceReferenceObject.getId());
		auditObjectChangeTracker = auditObjectChangeTrackerService.create(auditObjectChangeTracker);
		String[] attributeNames = { "id", "name", "price" };
		for (int i = 0; i < 3; i++) {
			AuditAttributeChangeTracker attributeChangeTracker = new AuditAttributeChangeTracker();
			attributeChangeTracker.setAttributeName(attributeNames[i]);
			attributeChangeTracker.setOldValue("oldValue-" + i);
			attributeChangeTracker.setNewValue("newValue-" + i);
			attributeChangeTracker.setChangedBy("user" + i);
			attributeChangeTracker.setAuditObjectChangeTracker(auditObjectChangeTracker);
			attributeChangeTracker = attributeChangeTrackerService.create(attributeChangeTracker);
		}

		AuditReport report = new AuditReport();
		report.setReportName("Audit Report test");
		report.setStartDateRange(formatter.parse(startDate));
		report.setEndDateRange(formatter.parse(endDate)); // 1 day ahead

		report.setRefObjectIds(Arrays.asList(1L));

		// Creating full names for changed users
		List<String> fullUserNames = Lists.newArrayList("Alice Smith", "Bob Johnson");
		report.setChangedUserNames(fullUserNames);
		report = auditReportService.create(report);
		return Lists.newArrayList(report.getId());
	}

}
