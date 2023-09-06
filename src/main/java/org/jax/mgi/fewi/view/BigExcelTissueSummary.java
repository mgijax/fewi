package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;

public class BigExcelTissueSummary extends AbstractBigExcelView {

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String,Object> model, SXSSFWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Sheet sheet = workbook.createSheet();			
		Row row;
	
		int rownum = 0;
		int col = 0;
		
		row = sheet.createRow(rownum++);
		row.createCell(col++).setCellValue("Structure");
		row.createCell(col++).setCellValue("All");
		row.createCell(col++).setCellValue("Detected");
		row.createCell(col++).setCellValue("Not Detected");

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