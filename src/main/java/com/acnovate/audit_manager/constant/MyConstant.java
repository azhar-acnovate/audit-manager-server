package com.acnovate.audit_manager.constant;

import java.text.SimpleDateFormat;

public class MyConstant {
	public static final String RESPONSE_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	public static final String REQUEST_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	public static final String REPORT_DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
	public static final SimpleDateFormat REPORT_DATE_FORMATOR = new SimpleDateFormat(REPORT_DATE_PATTERN);
	public static final String FILE_NAME_DELIMITER = "----";
	public static final String EXCEPTION_MESSAGE_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
}
