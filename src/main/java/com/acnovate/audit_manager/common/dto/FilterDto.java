package com.acnovate.audit_manager.common.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;

public class FilterDto {

	private Map<String, String> sort = new HashMap<>();
	private Map<String, Object> filter = new HashMap<>();

	public Map<String, String> getSort() {
		return sort;
	}

	public void setSort(Map<String, String> sort) {
		this.sort = sort;
	}

	public Sort getSortOrders() {

		return Sort.by(sort.entrySet().stream().filter(entry -> entry.getValue() != "") // Filter out entries with empty
																						// string
																						// values
				.map(entry -> new Sort.Order(Sort.Direction.fromString(entry.getValue()), entry.getKey()))
				.toArray(Sort.Order[]::new));

	}

	public Map<String, Object> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, Object> filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return "FilterDto [sort=" + sort + ", filter=" + filter + "]";
	}

}
