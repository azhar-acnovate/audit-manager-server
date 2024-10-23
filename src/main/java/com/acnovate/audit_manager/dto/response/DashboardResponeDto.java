package com.acnovate.audit_manager.dto.response;

public class DashboardResponeDto {
	private Double attributeChangePercentageSinceYesterday;
	private Long attributeChangesYesterday;

	private Double objectChangePercentageSinceLastWeek;
	private Long objectChangesLastWeek;

	private Double userChangePercentageSinceLastQuarter;
	private Long userChangesLastQuarter;

	private Double eventOccurrencePercentageSinceLastMonth;
	private Long eventOccurrencesLastMonth;

	public Double getAttributeChangePercentageSinceYesterday() {
		return attributeChangePercentageSinceYesterday;
	}

	public void setAttributeChangePercentageSinceYesterday(Double attributeChangePercentageSinceYesterday) {
		this.attributeChangePercentageSinceYesterday = attributeChangePercentageSinceYesterday;
	}

	public Long getAttributeChangesYesterday() {
		return attributeChangesYesterday;
	}

	public void setAttributeChangesYesterday(Long attributeChangesYesterday) {
		this.attributeChangesYesterday = attributeChangesYesterday;
	}

	public Double getObjectChangePercentageSinceLastWeek() {
		return objectChangePercentageSinceLastWeek;
	}

	public void setObjectChangePercentageSinceLastWeek(Double objectChangePercentageSinceLastWeek) {
		this.objectChangePercentageSinceLastWeek = objectChangePercentageSinceLastWeek;
	}

	public Long getObjectChangesLastWeek() {
		return objectChangesLastWeek;
	}

	public void setObjectChangesLastWeek(Long objectChangesLastWeek) {
		this.objectChangesLastWeek = objectChangesLastWeek;
	}

	public Double getUserChangePercentageSinceLastQuarter() {
		return userChangePercentageSinceLastQuarter;
	}

	public void setUserChangePercentageSinceLastQuarter(Double userChangePercentageSinceLastQuarter) {
		this.userChangePercentageSinceLastQuarter = userChangePercentageSinceLastQuarter;
	}

	public Long getUserChangesLastQuarter() {
		return userChangesLastQuarter;
	}

	public void setUserChangesLastQuarter(Long userChangesLastQuarter) {
		this.userChangesLastQuarter = userChangesLastQuarter;
	}

	public Double getEventOccurrencePercentageSinceLastMonth() {
		return eventOccurrencePercentageSinceLastMonth;
	}

	public void setEventOccurrencePercentageSinceLastMonth(Double eventOccurrencePercentageSinceLastMonth) {
		this.eventOccurrencePercentageSinceLastMonth = eventOccurrencePercentageSinceLastMonth;
	}

	public Long getEventOccurrencesLastMonth() {
		return eventOccurrencesLastMonth;
	}

	public void setEventOccurrencesLastMonth(Long eventOccurrencesLastMonth) {
		this.eventOccurrencesLastMonth = eventOccurrencesLastMonth;
	}

}
