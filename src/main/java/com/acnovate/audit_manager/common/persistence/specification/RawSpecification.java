package com.acnovate.audit_manager.common.persistence.specification;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.data.jpa.domain.Specification;

import com.acnovate.audit_manager.common.dto.FilterDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RawSpecification<T> implements Specification<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5886210512985738395L;

	private FilterDto filter;

	public final static String CRITERIA_NOT = "!";
	public final static String CRITERIA_LIKE = "*";
	public final static String CRITERIA_LESS_THAN_EQUAL = "<=";
	public final static String CRITERIA_GREATER_THAN_EQUAL = ">=";
	public final static String CRITERIA_LESS_THAN = "<";
	public final static String CRITERIA_GREATER_THAN = ">";
	public final static String CRITERIA_IN = "INCLAUSE-";
	public final static String CRITERIA_NOT_IN = "NOT-INCLAUSE-";

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	public RawSpecification(FilterDto filter) {
		this.filter = filter;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		Predicate predicate = criteriaBuilder.conjunction();

		for (Entry<String, Object> entry : filter.getFilter().entrySet()) {
			String fieldName = entry.getKey();

			if (entry.getKey() != null && entry.getValue() != null) {
				if (entry.getKey().startsWith(CRITERIA_NOT)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_NOT, "");
					Object value = getValue(root, fieldName, entry.getValue());
					if (value instanceof String && value.toString().contains(CRITERIA_LIKE)) {
						predicate = criteriaBuilder.and(predicate, criteriaBuilder.notLike(getRoot(root, fieldName),
								value.toString().replace(CRITERIA_LIKE, "%")));
					} else {
						predicate = criteriaBuilder.and(predicate, criteriaBuilder
								.notEqual(getRoot(root, entry.getKey().replaceFirst(CRITERIA_NOT, "")), value));
					}

				} else if (entry.getKey().startsWith(CRITERIA_LESS_THAN_EQUAL)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_LESS_THAN_EQUAL, "");
					Object value = getValue(root, fieldName, entry.getValue());
					if (value instanceof Date) {
						predicate = criteriaBuilder.and(predicate, criteriaBuilder
								.lessThanOrEqualTo(getRoot(root, fieldName).as(Date.class), (Date) value));
					} else if (value instanceof String) {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.lessThanOrEqualTo(getRoot(root, fieldName), (String) value));
					}

				} else if (entry.getKey().startsWith(CRITERIA_LESS_THAN)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_LESS_THAN, "");
					Object value = getValue(root, fieldName, entry.getValue());
					if (value instanceof Date) {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.lessThan(getRoot(root, fieldName).as(Date.class), (Date) value));
					} else if (value instanceof String) {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.lessThan(getRoot(root, fieldName), (String) value));
					}

				} else if (entry.getKey().startsWith(CRITERIA_GREATER_THAN_EQUAL)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_GREATER_THAN_EQUAL, "");
					Object value = getValue(root, fieldName, entry.getValue());

					if (value instanceof Date) {
						predicate = criteriaBuilder.and(predicate, criteriaBuilder
								.greaterThanOrEqualTo(getRoot(root, fieldName).as(Date.class), (Date) value));
					} else if (value instanceof String) {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.greaterThanOrEqualTo(getRoot(root, fieldName), (String) value));
					}

				} else if (entry.getKey().startsWith(CRITERIA_GREATER_THAN)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_GREATER_THAN, "");
					Object value = getValue(root, fieldName, entry.getValue());
					if (value instanceof Date) {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.greaterThan(getRoot(root, fieldName).as(Date.class), (Date) value));
					} else if (value instanceof String) {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.greaterThan(getRoot(root, fieldName), (String) value));
					}

				} else if (entry.getKey().startsWith(CRITERIA_IN)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_IN, "");
					Object value = getValue(root, fieldName, entry.getValue());
					if (value instanceof List<?>) {
						List<?> valueList = (List<?>) value;
						Expression<String> inExpression = getRoot(root, fieldName);
						predicate = criteriaBuilder.and(predicate, inExpression.in(valueList));
					}

				} else if (entry.getKey().startsWith(CRITERIA_NOT_IN)) {
					fieldName = entry.getKey().replaceFirst(CRITERIA_NOT_IN, "");
					Object value = getValue(root, fieldName, entry.getValue());
					if (value instanceof List<?>) {
						List<?> valueList = (List<?>) value;
						Expression<String> inExpression = getRoot(root, fieldName);
						predicate = criteriaBuilder.and(predicate, inExpression.in(valueList).not());
					}

				} else {
					fieldName = entry.getKey();
					Object value = getValue(root, fieldName, entry.getValue());

					if (value instanceof String && value.toString().contains(CRITERIA_LIKE)) {
						predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(getRoot(root, entry.getKey()),
								value.toString().replace(CRITERIA_LIKE, "%")));
					} else {
						predicate = criteriaBuilder.and(predicate,
								criteriaBuilder.equal(getRoot(root, fieldName), value));
					}
				}
			}
		}
		return predicate;

	}

	public Object getValue(Root<?> root, String fieldName, Object value) {
		try {
			if (fieldName.contains(".")) {
				String[] fieldNames = fieldName.split("\\.");
				return getNestedFieldValue(root, fieldNames, value);
			} else {
				Class<?> rootClass = root.getModel().getJavaType();
				return getFieldValue(rootClass, fieldName, value);

			}
		} catch (Exception e) {
			// Handle the case where the field doesn't exist in the entity
			e.printStackTrace();
		}
		return value.toString();
	}

//	private Object getFieldValue(Class<?> rootClass, String fieldName, Object value)
//			throws NoSuchFieldException, SecurityException, ParseException {
//
//		Field field = rootClass.getDeclaredField(fieldName);
//		if (field.getType() == Date.class) {
//			if (value instanceof Date) {
//				return value;
//			} else {
//				return dateFormat.parse(value.toString());
//			}
//		} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
//			return Boolean.parseBoolean(value.toString());
//		}
//
//		return value;
//	}
	private Object getFieldValue(Class<?> rootClass, String fieldName, Object value)
			throws NoSuchFieldException, SecurityException, ParseException {

		Field field = null;
		Class<?> currentClass = rootClass;

		// Traverse the class hierarchy to find the field
		while (currentClass != null) {
			try {
				field = currentClass.getDeclaredField(fieldName);
				break; // Field found, break the loop
			} catch (NoSuchFieldException e) {
				currentClass = currentClass.getSuperclass(); // Move to the parent class
			}
		}

		if (field == null) {
			throw new NoSuchFieldException(fieldName + " not found in class hierarchy");
		}

		if (field.getType() == Date.class) {
			if (value instanceof Date) {
				return value;
			} else {
				return dateFormat.parse(value.toString());
			}
		} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
			return Boolean.parseBoolean(value.toString());
		}

		return value;
	}

	private Object getNestedFieldValue(Path<?> path, String[] fieldNames, Object value)
			throws NoSuchFieldException, ParseException {
		if (fieldNames.length == 1) {
			Class<?> rootClass = path.getModel().getBindableJavaType();
			return getFieldValue(rootClass, fieldNames[0], value);

		} else {
			String fieldName = fieldNames[0];
			Path<Object> nestedPath = path.get(fieldName);
			String[] remainingFieldNames = Arrays.copyOfRange(fieldNames, 1, fieldNames.length);
			return getNestedFieldValue(nestedPath, remainingFieldNames, value);
		}
	}

	private Expression<String> getRoot(Root<T> root, String fieldName) {
		if (fieldName.contains(".")) {
			String[] splitKeys = fieldName.split("\\."); // Need to escape the dot in the regex
			Expression<String> pathObj = root.get(splitKeys[0]);

			for (int i = 1; i < splitKeys.length; i++) {
				pathObj = ((Path<?>) pathObj).get(splitKeys[i]);
			}
			return pathObj;
		} else {
			return root.get(fieldName);
		}
	}

}