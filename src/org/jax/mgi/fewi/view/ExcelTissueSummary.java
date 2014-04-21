package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.MarkerTissueCount;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelTissueSummary extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HSSFSheet sheet = workbook.createSheet();			
		HSSFRow row;
	
		int rownum = 0;
		int col = 0;
		
		row = sheet.createRow(rownum++);
		row.createCell(col++).setCellValue("Structure");
		row.createCell(col++).setCellValue("All");
		row.createCell(col++).setCellValue("Detected");
		row.createCell(col++).setCellValue("Not Detected");

		@SuppressWarnings("unchecked")
		List<MarkerTissueCount> tissues = (List<MarkerTissueCount>) model.get("results");
		for (MarkerTissueCount mtc : tissues) {
			row = sheet.createRow(rownum++);
			col = 0;
			row.createCell(col++).setCellValue("TS" + mtc.getStructure());
			row.createCell(col++).setCellValue(mtc.getAllResultCount());
			row.createCell(col++).setCellValue(mtc.getDetectedResultCount());
			row.createCell(col++).setCellValue(mtc.getNotDetectedResultCount());			
		}
	}

}
