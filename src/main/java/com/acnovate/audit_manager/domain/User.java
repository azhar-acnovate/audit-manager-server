package com.acnovate.audit_manager.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="AUDITUSER")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable=true)
	private Long ID;
	
	@Column(nullable=true)
	private String USERNAME;
	
	@Column(nullable=true)
	private String PASSWORD;
	
	@Column(nullable=true)
	private int ACTIVE;

	@Column(nullable=true)
	private String PROFILEIMAGENAME;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date CREATEDON;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date MODIFIEDON;

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getUSERNAME() {
		return USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public int getACTIVE() {
		return ACTIVE;
	}

	public void setACTIVE(int aCTIVE) {
		ACTIVE = aCTIVE;
	}

	public String getPROFILEIMAGENAME() {
		return PROFILEIMAGENAME;
	}

	public void setPROFILEIMAGENAME(String pROFILEIMAGENAME) {
		PROFILEIMAGENAME = pROFILEIMAGENAME;
	}

	public Date getCREATEDON() {
		return CREATEDON;
	}

	public void setCREATEDON(Date cREATEDON) {
		CREATEDON = cREATEDON;
	}

	public Date getMODIFIEDON() {
		return MODIFIEDON;
	}

	public void setMODIFIEDON(Date mODIFIEDON) {
		MODIFIEDON = mODIFIEDON;
	}
	
	public User() {
		
	}

	public User(Long iD, String uSERNAME, String pASSWORD, int aCTIVE, String pROFILEIMAGENAME,
			Date cREATEDON, Date mODIFIEDON) {
		super();
		ID = iD;
		USERNAME = uSERNAME;
		PASSWORD = pASSWORD;
		ACTIVE = aCTIVE;
		PROFILEIMAGENAME = pROFILEIMAGENAME;
		CREATEDON = cREATEDON;
		MODIFIEDON = mODIFIEDON;
	}

	@Override
	public String toString() {
		return "User [ID=" + ID + ", USERNAME=" + USERNAME + ", PASSWORD=" + PASSWORD
				+ ", ACTIVE=" + ACTIVE + ", PROFILEIMAGENAME=" + PROFILEIMAGENAME + ", CREATEDON=" + CREATEDON
				+ ", MODIFIEDON=" + MODIFIEDON + "]";
	}

}