package com.acnovate.audit_manager.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;

@Repository
public interface AuditObjectChangeTrackerRepository
		extends JpaRepository<AuditObjectChangeTracker, Long>, JpaSpecificationExecutor<AuditObjectChangeTracker> {

	List<AuditObjectChangeTracker> findByRefObjectIdIn(List<Long> refObjectIds);

	List<AuditObjectChangeTracker> findByRefObjectIdInAndEventOccurenceBetween(List<Long> refObjectIds, Date startDate,
			Date endDate);

	// Query to get the count of event occurrences from last month
	@Query("SELECT COUNT(e) FROM AuditObjectChangeTracker e WHERE e.createdAt >= :lastMonthStart AND e.createdAt <= :lastMonthEnd")
	Long countByCreatedAtBetween(@Param("lastMonthStart") Date lastMonthStart,
			@Param("lastMonthEnd") Date lastMonthEnd);

}
