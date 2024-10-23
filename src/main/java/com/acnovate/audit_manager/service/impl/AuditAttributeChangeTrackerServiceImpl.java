package com.acnovate.audit_manager.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnovate.audit_manager.common.persistence.service.AbstractRawService;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditAttributeChangeRequestDto;
import com.acnovate.audit_manager.dto.response.AuditAttributeChangeTrackerResponseDto;
import com.acnovate.audit_manager.repository.AuditAttributeChangeTrackerRepository;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;

@Service
@Transactional
public class AuditAttributeChangeTrackerServiceImpl extends AbstractRawService<AuditAttributeChangeTracker>
		implements IAuditAttributeChangeTrackerService {
	@Autowired
	private AuditAttributeChangeTrackerRepository repo;

	@Autowired
	private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

	@Override
	protected JpaRepository<AuditAttributeChangeTracker, Long> getDao() {
		return repo;
	}

	@Override
	protected JpaSpecificationExecutor<AuditAttributeChangeTracker> getSpecificationExecutor() {
		return repo;
	}

	@Override
	public AuditAttributeChangeTrackerResponseDto domainToDto(AuditAttributeChangeTracker auditAttributeChangeTracker) {

		AuditAttributeChangeTrackerResponseDto auditAttributeChangeTrackerResponseDto = new AuditAttributeChangeTrackerResponseDto();
		auditAttributeChangeTrackerResponseDto.setAttributeName(auditAttributeChangeTracker.getAttributeName());
		auditAttributeChangeTrackerResponseDto.setChangedBy(auditAttributeChangeTracker.getChangedBy());
		auditAttributeChangeTrackerResponseDto.setId(auditAttributeChangeTracker.getId());
		auditAttributeChangeTrackerResponseDto.setNewValue(auditAttributeChangeTracker.getNewValue());
		auditAttributeChangeTrackerResponseDto.setOldValue(auditAttributeChangeTracker.getOldValue());

		return auditAttributeChangeTrackerResponseDto;
	}

	@Override
	public List<AuditAttributeChangeTracker> findByAuditObjectChangeTracker(
			AuditObjectChangeTracker auditObjectChangeTracker) {

		return repo.findByAuditObjectChangeTracker(auditObjectChangeTracker);
	}

	@Override
	public AuditAttributeChangeTrackerResponseDto createAuditAttributeChangeTracker(
			AuditAttributeChangeRequestDto auditAttributeChangeRequestDto) {
		AuditAttributeChangeTracker auditAttributeChangeTracker = new AuditAttributeChangeTracker();
		if (auditAttributeChangeRequestDto.getId() != null) {
			auditAttributeChangeTracker = findOne(auditAttributeChangeRequestDto.getId());
		}
		auditAttributeChangeTracker.setAttributeName(auditAttributeChangeRequestDto.getAttributeName());
		auditAttributeChangeTracker.setChangedBy(auditAttributeChangeRequestDto.getChangedBy());
		auditAttributeChangeTracker.setOldValue(auditAttributeChangeRequestDto.getOldValue());
		auditAttributeChangeTracker.setNewValue(auditAttributeChangeRequestDto.getNewValue());
		auditAttributeChangeTracker.setAuditObjectChangeTracker(auditObjectChangeTrackerService
				.findOne(auditAttributeChangeRequestDto.getAuditObjectChangeTrackerId()));
		auditAttributeChangeTracker = create(auditAttributeChangeTracker);
		return domainToDto(auditAttributeChangeTracker);
	}

	@Override
	public Long countAttributeChangesYesterday() {

		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Set to yesterday
		calendar.add(Calendar.DATE, -1);

		// Set the start time to 00:00:00
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startOfYesterday = calendar.getTime();

		// Set the end time to 23:59:59
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date endOfYesterday = calendar.getTime();
		return repo.countAttributeChangesYesterday(startOfYesterday, endOfYesterday);
	}

	@Override
	public Long countAttributeChangesDayBeforeYesterday() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Set to the day before yesterday
		calendar.add(Calendar.DATE, -2);

		// Set the start time to 00:00:00
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startOfDayBeforeYesterday = calendar.getTime();

		// Set the end time to 23:59:59
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date endOfDayBeforeYesterday = calendar.getTime();
		return repo.countAttributeChangesYesterday(startOfDayBeforeYesterday, endOfDayBeforeYesterday);
	}

	@Override
	public Long countUserChangesLastQuarter() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Get the current month
		int currentMonth = calendar.get(Calendar.MONTH);

		// Determine which quarter we are in, and set to the last quarter
		int lastQuarterStartMonth;
		switch (currentMonth / 3) {
		case 0: // First quarter (Jan, Feb, Mar) -> Set last quarter to Oct, Nov, Dec (last
				// year)
			lastQuarterStartMonth = Calendar.OCTOBER;
			calendar.add(Calendar.YEAR, -1); // Go back one year
			break;
		case 1: // Second quarter (Apr, May, Jun) -> Set last quarter to Jan, Feb, Mar
			lastQuarterStartMonth = Calendar.JANUARY;
			break;
		case 2: // Third quarter (Jul, Aug, Sep) -> Set last quarter to Apr, May, Jun
			lastQuarterStartMonth = Calendar.APRIL;
			break;
		default: // Fourth quarter (Oct, Nov, Dec) -> Set last quarter to Jul, Aug, Sep
			lastQuarterStartMonth = Calendar.JULY;
			break;
		}

		// Set the calendar to the first day of the last quarter
		calendar.set(Calendar.MONTH, lastQuarterStartMonth);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date lastQuarterStart = calendar.getTime();

		// Set the calendar to the last day of the last quarter
		calendar.add(Calendar.MONTH, 3);
		calendar.add(Calendar.MILLISECOND, -1); // Go to the last millisecond of the previous quarter
		Date lastQuarterEnd = calendar.getTime();

		return repo.countAttributeChangesYesterday(lastQuarterStart, lastQuarterEnd);
	}

	@Override
	public Long countUserChangesPreviousQuarter() {
		// Get the current date
		Calendar calendar = Calendar.getInstance();

		// Get the current month
		int currentMonth = calendar.get(Calendar.MONTH);

		// Determine the start month of the previous quarter
		int previousQuarterStartMonth;
		switch (currentMonth / 3) {
		case 0: // If we're in the first quarter (Jan-Mar), set the previous quarter to Oct-Dec
				// of the previous year
			previousQuarterStartMonth = Calendar.OCTOBER;
			calendar.add(Calendar.YEAR, -1); // Move back one year
			break;
		case 1: // If we're in the second quarter (Apr-Jun), previous quarter is Jan-Mar
			previousQuarterStartMonth = Calendar.JANUARY;
			break;
		case 2: // If we're in the third quarter (Jul-Sep), previous quarter is Apr-Jun
			previousQuarterStartMonth = Calendar.APRIL;
			break;
		default: // If we're in the fourth quarter (Oct-Dec), previous quarter is Jul-Sep
			previousQuarterStartMonth = Calendar.JULY;
			break;
		}

		// Set the calendar to the first day of the previous quarter at 00:00:00
		calendar.set(Calendar.MONTH, previousQuarterStartMonth);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date previousQuarterStart = calendar.getTime();

		// Set the calendar to the last day of the previous quarter at 23:59:59
		calendar.add(Calendar.MONTH, 3); // Move forward to the next quarter
		calendar.add(Calendar.MILLISECOND, -1); // Subtract 1 millisecond to get the last millisecond of the previous
												// quarter
		Date previousQuarterEnd = calendar.getTime();
		return repo.countAttributeChangesYesterday(previousQuarterStart, previousQuarterEnd);
	}

}
