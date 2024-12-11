package com.acnovate.audit_manager.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;

@Repository
public interface AuditAttributeChangeTrackerRepository extends JpaRepository<AuditAttributeChangeTracker, Long>,
		JpaSpecificationExecutor<AuditAttributeChangeTracker> {

	List<AuditAttributeChangeTracker> findByAuditObjectChangeTracker(AuditObjectChangeTracker auditObjectChangeTracker);

	// Query to get the count of attribute changes from yesterday
	@Query("SELECT COUNT(a) FROM AuditAttributeChangeTracker a WHERE a.createdAt >= :yesterdayStart AND a.createdAt <= :yesterdayEnd")
	Long countAttributeChangesYesterday(@Param("yesterdayStart") Date yesterdayStart,
			@Param("yesterdayEnd") Date yesterdayEnd);

	@Query("SELECT a.changedBy, COUNT(a) AS changesCount FROM AuditAttributeChangeTracker a GROUP BY a.changedBy ORDER BY changesCount DESC")
	List<Object[]> top5UserModifyingDataFrequently(Pageable pageable);

	@Query("SELECT a.attributeName, COUNT(a) AS changeCount " + "FROM AuditAttributeChangeTracker a "
			+ "GROUP BY a.attributeName " + "ORDER BY changeCount DESC")
	List<Object[]> findTopChangedAttributes(Pageable pageable);

	List<AuditAttributeChangeTracker> findByAuditObjectChangeTrackerOrderByUpdatedAtDesc(
			AuditObjectChangeTracker auditObjectChangeTracker);

	AuditAttributeChangeTracker findTop1ByAttributeNameAndAuditObjectChangeTrackerRefObjectIdOrderByAuditObjectChangeTrackerUpdatedAtDesc(
			String attributeName, Long refObjectId);

}
