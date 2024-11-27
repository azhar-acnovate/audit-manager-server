package com.acnovate.audit_manager.service.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // For .xlsx files
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.persistence.exception.CustomErrorHandleException;
import com.acnovate.audit_manager.constant.MyConstant;
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;
import com.acnovate.audit_manager.service.IFileGenerationService;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;

@Service
public class FileGenerationServiceImpl implements IFileGenerationService {

	@Autowired
	private IAuditAttributeChangeTrackerService auditAttributeChangeTrackerService;

	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

	// Array of header titles
	public static String[] AUDUIT_REPORT_HEADER = { "Source Name", "Source Key", "Event Type", "Event Occurrence",
			"Attribute Name", "Old Value", "New Value", "Changed By" };

	@Override
	public byte[] generateFile(String fileType, LinkedHashMap<String, String> headerInfo,
			List<AuditObjectChangeTracker> auditObjectList) {
		if ("csv".equalsIgnoreCase(fileType)) {
			return generateCsv(headerInfo, auditObjectList);
		} else {
			return generateAuditReport(headerInfo, auditObjectList);
		}

	}

	private byte[] generateCsv(LinkedHashMap<String, String> headerInfo,
			List<AuditObjectChangeTracker> auditObjectList) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (PrintWriter writer = new PrintWriter(outputStream)) {
			writer.println(String.join(",", AUDUIT_REPORT_HEADER));
			for (AuditObjectChangeTracker auditObject : auditObjectList) {
				SourceReferenceObject refObject = sourceReferenceObjectService.findOne(auditObject.getRefObjectId());
				List<AuditAttributeChangeTracker> list = auditAttributeChangeTrackerService
						.findByAuditObjectChangeTracker(auditObject);
				StringBuilder newRow = new StringBuilder();
				newRow.append(refObject.getSourceReferenceName()).append(",");
				newRow.append(refObject.getSourceReferenceKey()).append(",");
				newRow.append(auditObject.getEventType()).append(",");
				newRow.append(MyConstant.REPORT_DATE_FORMATOR.format(auditObject.getEventOccurence())).append(",");
				for (AuditAttributeChangeTracker auditAttribute : list) {
					newRow.append(auditAttribute.getAttributeName().toUpperCase()).append(",");
					newRow.append(auditAttribute.getOldValue()).append(",");
					newRow.append(auditAttribute.getNewValue()).append(",");
					newRow.append(auditAttribute.getChangedBy()).append("");
					writer.println(newRow.toString());
					newRow = new StringBuilder();
					newRow.append("").append(",");
					newRow.append("").append(",");
					newRow.append("").append(",");
					newRow.append("").append(",");
				}
			}
			writer.flush();
		}
		return outputStream.toByteArray();
	}

	private void setBorderAllSide(CellStyle cellStyle) {
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
	}

	@Override
	public byte[] generateAuditReport(LinkedHashMap<String, String> headerInfo,
			List<AuditObjectChangeTracker> auditObjectList) {

		// Set the font color using RGB values (131, 146, 171)

		try (XSSFWorkbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			// Create a font color
			XSSFColor fontColor = new XSSFColor(Color.decode("#8392AB"), null);
			Sheet sheet = workbook.createSheet("Audit Report");

			sheet.setDefaultColumnWidth(20);
			sheet.autoSizeColumn(0);
			// Create styles for normal, red, and green text
			CellStyle normalStyle = workbook.createCellStyle();
			XSSFFont normalFont = workbook.createFont();
			normalFont.setFontName("Arial"); // Set the font to Arial
			normalFont.setBold(true);
			normalFont.setFontHeightInPoints((short) 9); // Set the font size to 9
			normalFont.setColor(fontColor);
			setBorderAllSide(normalStyle);
			normalStyle.setFont(normalFont);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center vertically

			XSSFFont headerFont = workbook.createFont();
			headerFont.setFontName("Arial"); // Set the font to Arial
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 9); // Set the font size to 9
			headerFont.setColor(IndexedColors.BLACK.getIndex());

			setBorderAllSide(headerStyle); // Assuming this is a custom method to set borders
			headerStyle.setFont(headerFont);

			CellStyle redStyle = workbook.createCellStyle();
			XSSFFont redFont = workbook.createFont();
			redFont.setFontName("Arial"); // Set the font to Arial
			redFont.setFontHeightInPoints((short) 9); // Set the font size to 9
			redFont.setBold(true);
			redFont.setColor(IndexedColors.RED.getIndex());
			setBorderAllSide(redStyle);
			redStyle.setFont(redFont);

			CellStyle greenStyle = workbook.createCellStyle();
			XSSFFont greenFont = workbook.createFont();
			greenFont.setFontName("Arial"); // Set the font to Arial
			greenFont.setFontHeightInPoints((short) 9); // Set the font size to 9
			greenFont.setBold(true);
			greenFont.setColor(IndexedColors.GREEN.getIndex());
			setBorderAllSide(greenStyle);
			greenStyle.setFont(greenFont);

			int rowIndex = 1;

			// Create the header row
			Row headerRow = sheet.createRow(0);
			headerRow.setHeightInPoints(39);

			// Loop through the headers array and create header cells
			for (int i = 0; i < AUDUIT_REPORT_HEADER.length; i++) {
				Cell headerCell = headerRow.createCell(i);
				headerCell.setCellValue(AUDUIT_REPORT_HEADER[i].toUpperCase());
				headerCell.setCellStyle(headerStyle);// Apply the global style to each cell
			}

			for (AuditObjectChangeTracker auditObject : auditObjectList) {
				SourceReferenceObject refObject = sourceReferenceObjectService.findOne(auditObject.getRefObjectId());
				List<AuditAttributeChangeTracker> list = auditAttributeChangeTrackerService
						.findByAuditObjectChangeTracker(auditObject);
				Row row = sheet.createRow(rowIndex++);
				// Fill in the data for Source Name, Source Key, Event Type, and Event
				// Occurrence
				// Create and style each cell for the row
				Cell cell0 = row.createCell(0);
				cell0.setCellValue(refObject.getSourceReferenceName());
				cell0.setCellStyle(normalStyle); // Apply the global style

				Cell cell1 = row.createCell(1);
				cell1.setCellValue(refObject.getSourceReferenceKey());
				cell1.setCellStyle(normalStyle);

				Cell cell2 = row.createCell(2);
				cell2.setCellValue(auditObject.getEventType());
				cell2.setCellStyle(normalStyle);

				Cell cell3 = row.createCell(3);
				cell3.setCellValue(MyConstant.REPORT_DATE_FORMATOR.format(auditObject.getEventOccurence()));
				cell3.setCellStyle(normalStyle);

				for (AuditAttributeChangeTracker auditAttribute : list) {

					// Fill in the data for Attribute Name, Old Value, New Value, and Changed By

					Cell cell4 = row.createCell(4);
					cell4.setCellValue(auditAttribute.getAttributeName().toUpperCase());
					cell4.setCellStyle(normalStyle);
					Cell oldValueCell = row.createCell(5);
//					if (!auditAttribute.getOldValue().equals(auditAttribute.getNewValue())) {
//						oldValueCell.setCellValue(auditAttribute.getOldValue().toString());
//					} else {
//						oldValueCell.setCellValue("");
//					}

					Cell newValueCell = row.createCell(6);
					newValueCell.setCellValue(auditAttribute.getNewValue().toString());

//					row.createCell(7).setCellValue(auditAttribute.getChangedBy());

					Cell cell7 = row.createCell(7);
					cell7.setCellValue(auditAttribute.getChangedBy());
					cell7.setCellStyle(normalStyle);
					// Apply styles based on value comparison
					if (auditAttribute.getOldValue().equals(auditAttribute.getNewValue())) {
						oldValueCell.setCellStyle(normalStyle);
						newValueCell.setCellStyle(normalStyle);
						oldValueCell.setCellValue("");
					} else {
						oldValueCell.setCellStyle(redStyle);
						newValueCell.setCellStyle(greenStyle);
						oldValueCell.setCellValue(auditAttribute.getOldValue().toString());
					}
					row = sheet.createRow(rowIndex++);
					cell0 = row.createCell(0);
//					cell0.setCellValue("");
					cell0.setCellStyle(normalStyle); // Apply the global style
					cell1 = row.createCell(1);
//					cell1.setCellValue("");
					cell1.setCellStyle(normalStyle);

					cell2 = row.createCell(2);
//					cell2.setCellValue("");
					cell2.setCellStyle(normalStyle);

					cell3 = row.createCell(3);
//					cell3.setCellValue("");
					cell3.setCellStyle(normalStyle);
				}
			}

			workbook.write(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new CustomErrorHandleException(e.getMessage());
		}

	}

}
