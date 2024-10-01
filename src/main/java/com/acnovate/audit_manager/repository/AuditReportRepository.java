package  com.acnovate.audit_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.acnovate.audit_manager.domain.AuditReport;

@Repository
public interface AuditReportRepository extends JpaRepository<AuditReport, Long>, JpaSpecificationExecutor<AuditReport> {

}