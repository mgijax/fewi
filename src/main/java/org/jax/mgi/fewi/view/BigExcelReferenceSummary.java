package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fe.datamodel.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigExcelReferenceSummary extends AbstractBigExcelView {
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(BigExcelReferenceSummary.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String,Object> model, SXSSFWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("buildExcelDocument");
		List<Reference> references = (List<Reference>) model.get("results");

		Sheet sheet = workbook.createSheet();
		
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Citation");
		
		Row row;
		int rownum = 1;
		for (Reference ref: references) {
			row = sheet.createRow(rownum++);
			row.createCell(0).setCellValue(ref.getLongCitation());
		}

	}

}