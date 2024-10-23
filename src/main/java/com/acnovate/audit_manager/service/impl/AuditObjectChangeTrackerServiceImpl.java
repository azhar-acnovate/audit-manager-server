package com.acnovate.audit_manager.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditLogActivityResponseDto;
import com.acnovate.audit_manager.dto.response.DashboardResponeDto;
import com.acnovate.audit_manager.repository.AuditObjectChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;

@Service
@Transactional
public class AuditObjectChangeTrackerServiceImpl extends AbstractRawService<AuditObjectChangeTracker>
		implements IAuditObjectChangeTrackerService {
	@Autowired
	private AuditObjectChangeTrackerRepository repo;

	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

	@Autowired
	private IAuditAttributeChangeTrackerService auditAttributeChangeTrackerService;

	@Override
	protected JpaRepository<AuditObjectChangeTracker, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<AuditObjectChangeTracker> getSpecificationExecutor() {
		return repo;
	}

	public AuditLogActivityResponseDto domainToDto(AuditObjectChangeTracker auditObjectChangeTracker) {
		AuditLogActivityResponseDto auditLogActivityResponseDto = new AuditLogActivityResponseDto();
		auditLogActivityResponseDto.setId(auditObjectChangeTracker.getId());
		auditLogActivityResponseDto.setEventOccurence(auditObjectChangeTracker.getEventOccurence());
		auditLogActivityResponseDto.setEventType(auditObjectChangeTracker.getEventType());
		auditLogActivityResponseDto.setRefObjectId(auditObjectChangeTracker.getRefObjectId());
		List<AuditAttributeChangeTracker> auditAttributeChangeTrackers = auditAttributeChangeTrackerService
				.findByAuditObjectChangeTracker(auditObjectChangeTracker);
		auditLogActivityResponseDto.setAttributeChanges(
				auditAttributeChangeTrackers.stream().map(auditAttributeChangeTrackerService::domainToDto).toList());
		SourceReferenceObject sourceReference = sourceReferenceObjectService
				.findOne(auditObjectChangeTracker.getRefObjectId());
		auditLogActivityResponseDto.setSourceReference(sourceReferenceObjectService.domainToDto(sourceReference));
		return auditLogActivityResponseDto;
	}

	@Override
	public AuditLogActivityResponseDto createAuditObjectChangeTracker(
			AuditObjectChangeRequestDto auditObjectChangeRequestDto) {
		AuditObjectChangeTracker auditObjectChangeTracker = new AuditObjectChangeTracker();
		if (auditObjectChangeRequestDto.getId() != null) {
			auditObjectChangeTracker = findOne(auditObjectChangeRequestDto.getId());
		}
		auditObjectChangeTracker.setRefObjectId(auditObjectChangeRequestDto.getRefObjectId());
		auditObjectChangeTracker.setEventType(auditObjectChangeRequestDto.getEventType());
		auditObjectChangeTracker.setEventOccurence(auditObjectChangeRequestDto.getEventOccurence());
		auditObjectChangeTracker = create(auditObjectChangeTracker);
		return domainToDto(auditObjectChangeTracker);
	}

	@Override
	public List<AuditObjectChangeTracker> getFilteredReportData(List<Long> refObjectIds, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return repo.findByRefObjectIdInAndEventOccurenceBetween(refObjectIds, startDate, endDate);
	}

	// Calculate percentage change
	double getPercentage(Long yesterdayCount, Long dayBeforeCount) {
		if (dayBeforeCount == 0) {
			return 100.00; // Avoid division by zero
		}

		double percentage = ((yesterdayCount - dayBeforeCount) / (double) dayBeforeCount) * 100;
		return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private long countObjectChangesPreviousWeek() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Set the calendar to the start of the current week (Monday)
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		// Move the calendar back by 1 week to get the previous week
		calendar.add(Calendar.WEEK_OF_YEAR, -1);

		// Set the start time to 00:00:00 for the previous week (Monday)
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date previousWeekStart = calendar.getTime();

		// Move the calendar to the end of the previous week (Sunday)
		calendar.add(Calendar.DAY_OF_WEEK, 6); // Add 6 days to reach Sunday
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date previousWeekEnd = calendar.getTime();

		return repo.countByCreatedAtBetween(previousWeekStart, previousWeekEnd);
	}

	private long countObjectChangesLastWeek() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Set the calendar to the start of the current week (Monday)
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		// Go back 1 week to get the start of last week
		calendar.add(Calendar.WEEK_OF_YEAR, -1);

		// Set the start time to 00:00:00
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date lastWeekStart = calendar.getTime();

		// Move the calendar to the end of the week (Sunday)
		calendar.add(Calendar.DAY_OF_WEEK, 6); // Add 6 days to reach Sunday
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date lastWeekEnd = calendar.getTime();

		return repo.countByCreatedAtBetween(lastWeekStart, lastWeekEnd);

	}

	@Override
	public DashboardResponeDto getDashboardData() {

		DashboardResponeDto responseDto = new DashboardResponeDto();
		try {
			long attributeChangesYesterday = auditAttributeChangeTrackerService.countAttributeChangesYesterday();
			long attributeChangesDayBefore = auditAttributeChangeTrackerService
					.countAttributeChangesDayBeforeYesterday();
			double attributeChangePercentage = getPercentage(attributeChangesYesterday, attributeChangesDayBefore);
			responseDto.setAttributeChangesYesterday(attributeChangesYesterday);
			responseDto.setAttributeChangePercentageSinceYesterday(attributeChangePercentage);

			Long objectChangesLastWeek = countObjectChangesLastWeek();
			Long objectChangesPreviousWeek = countObjectChangesPreviousWeek();
			double objectChangePercentage = getPercentage(objectChangesLastWeek, objectChangesPreviousWeek);
			responseDto.setObjectChangesLastWeek(objectChangesLastWeek);
			responseDto.setObjectChangePercentageSinceLastWeek(objectChangePercentage);

			Long userChangesLastQuarter = auditAttributeChangeTrackerService.countUserChangesLastQuarter();
			Long userChangesPreviousQuarter = auditAttributeChangeTrackerService.countUserChangesPreviousQuarter();
			double userChangePercentage = getPercentage(userChangesLastQuarter, userChangesPreviousQuarter);
			responseDto.setUserChangesLastQuarter(userChangesLastQuarter);
			responseDto.setUserChangePercentageSinceLastQuarter(userChangePercentage);

			Long eventOccurrencesLastMonth = countEventOccurrencesLastMonth();
			Long eventOccurrencesPreviousMonth = countEventOccurrencesPreviousMonth();
			double eventOccurrencePercentage = getPercentage(eventOccurrencesLastMonth, eventOccurrencesPreviousMonth);

			responseDto.setEventOccurrencesLastMonth(eventOccurrencesLastMonth);
			responseDto.setEventOccurrencePercentageSinceLastMonth(eventOccurrencePercentage);

		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomErrorHandleException(e);
		}
		return responseDto;
	}

	private Long countEventOccurrencesPreviousMonth() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Set the calendar to the first day of the month before last month
		calendar.add(Calendar.MONTH, -2); // Move to the month before last month
		calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the 1st of that month
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date previousMonthStart = calendar.getTime();

		// Set the calendar to the last day of the previous month
		calendar.add(Calendar.MONTH, 1); // Move to the first day of last month
		calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the 1st of last month
		calendar.add(Calendar.MILLISECOND, -1); // Go back 1 millisecond to get the last moment of the previous month
		Date previousMonthEnd = calendar.getTime();
		return repo.countByCreatedAtBetween(previousMonthStart, previousMonthEnd);
	}

	private Long countEventOccurrencesLastMonth() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Set the calendar to the first day of last month
		calendar.add(Calendar.MONTH, -1); // Move to last month
		calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the 1st of the month
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date lastMonthStart = calendar.getTime();

		// Set the calendar to the last day of last month
		calendar.add(Calendar.MONTH, 1); // Move to the current month
		calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the 1st of the current month
		calendar.add(Calendar.MILLISECOND, -1); // Go back 1 millisecond to get the last moment of the previous month
		Date lastMonthEnd = calendar.getTime();
		return repo.countByCreatedAtBetween(lastMonthStart, lastMonthEnd);
	}

}
