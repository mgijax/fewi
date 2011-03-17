package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Reference;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelReferenceSummary extends AbstractExcelView {
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelReferenceSummary.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("buildExcelDocument");
		List<Reference> references = (List<Reference>) model.get("results");

		HSSFSheet sheet = workbook.createSheet();
		
		HSSFRow header = sheet.createRow(0);
		header.createCell(0).setCellValue("Citation");
		
		HSSFRow row;
		int rownum = 1;
		for (Reference ref: references) {
			row = sheet.createRow(rownum++);
			row.createCell(0).setCellValue(ref.getLongCitation());
		}

	}

}