//package com.acnovate.audit_manager.domain;
//
//import java.util.Date;
//
//import com.acnovate.audit_manager.domain.enums.AuditLogEntityTypeEnum;
//import com.acnovate.audit_manager.domain.enums.AuditLogSourceTypeEnum;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "audit_log")
//public class AuditLog extends AuditEntity {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	private AuditLogSourceTypeEnum sourceType = AuditLogSourceTypeEnum.OTHERS;
//	private AuditLogEntityTypeEnum entityType = AuditLogEntityTypeEnum.OTHERS;
//	private String uniqueAuditingKey;
//	private String oldValue;
//	private String newValue;
//	private String changedBy;
//	private Date changeDateTime;
//	private int version = 0;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public AuditLogSourceTypeEnum getSourceType() {
//		return sourceType;
//	}
//
//	public void setSourceType(AuditLogSourceTypeEnum sourceType) {
//		this.sourceType = sourceType;
//	}
//
//	public AuditLogEntityTypeEnum getEntityType() {
//		return entityType;
//	}
//
//	public void setEntityType(AuditLogEntityTypeEnum entityType) {
//		this.entityType = entityType;
//	}
//
//	public String getUniqueAuditingKey() {
//		return uniqueAuditingKey;
//	}
//
//	public void setUniqueAuditingKey(String uniqueAuditingKey) {
//		this.uniqueAuditingKey = uniqueAuditingKey;
//	}
//
//	public String getOldValue() {
//		return oldValue;
//	}
//
//	public void setOldValue(String oldValue) {
//		this.oldValue = oldValue;
//	}
//
//	public String getNewValue() {
//		return newValue;
//	}
//
//	public void setNewValue(String newValue) {
//		this.newValue = newValue;
//	}
//
//	public String getChangedBy() {
//		return changedBy;
//	}
//
//	public void setChangedBy(String changedBy) {
//		this.changedBy = changedBy;
//	}
//
//	public Date getChangeDateTime() {
//		return changeDateTime;
//	}
//
//	public void setChangeDateTime(Date changeDateTime) {
//		this.changeDateTime = changeDateTime;
//	}
//
//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}
//
//}
