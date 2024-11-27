package com.acnovate.audit_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acnovate.audit_manager.domain.AuditReport;

@Repository
public interface AuditReportRepository extends JpaRepository<AuditReport, Long>, JpaSpecificationExecutor<AuditReport> {

	@Query("SELECT a.reportName FROM AuditReport a WHERE a.id IN :ids")
	List<String> findReportNamesByIds(@Param("ids") List<Long> ids);

}
