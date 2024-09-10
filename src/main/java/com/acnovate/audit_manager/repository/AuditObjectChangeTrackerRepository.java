package  com.acnovate.audit_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;

@Repository
public interface AuditObjectChangeTrackerRepository extends JpaRepository<AuditObjectChangeTracker, Long>, JpaSpecificationExecutor<AuditObjectChangeTracker> {

}