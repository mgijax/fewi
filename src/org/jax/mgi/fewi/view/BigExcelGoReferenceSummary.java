package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class BigExcelGoReferenceSummary extends AbstractBigExcelView 
{	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String,Object> model, SXSSFWorkbook workbook, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Annotation> results = (List<Annotation>) model.get("results");	
		
		Sheet sheet = workbook.createSheet();
		Row row;
	
		int rownum = 0;
		int col = 0;
		
		row = sheet.createRow(rownum++);
		row.createCell(col++).setCellValue("MGI Gene/Marker ID");
		row.createCell(col++).setCellValue("Symbol");
		row.createCell(col++).setCellValue("Name");
		row.createCell(col++).setCellValue("Chr");
		row.createCell(col++).setCellValue("Qualifier");
		row.createCell(col++).setCellValue("Annotated Term");
		row.createCell(col++).setCellValue("Category");
		row.createCell(col++).setCellValue("Evidence");
		
		Marker m;
		MarkerLocation ml;

		for (Annotation annot: results){
			row = sheet.createRow(rownum++);
			col = 0;
			
			m = annot.getMarkers().get(0);
			ml = m.getPreferredCentimorgans();
			if (ml == null) { ml = m.getPreferredLocation(); }

			row.createCell(col++).setCellValue(m.getPrimaryID());
			row.createCell(col++).setCellValue(m.getSymbol());
			row.createCell(col++).setCellValue(m.getName());

			if (ml != null) {
			    row.createCell(col++).setCellValue(
				ml.getChromosome());
			} else {
			    row.createCell(col++).setCellValue("Unknown");
			}

			row.createCell(col++).setCellValue(
				annot.getQualifier());
			row.createCell(col++).setCellValue(annot.getTerm());
			row.createCell(col++).setCellValue(annot.getDagName());
			row.createCell(col++).setCellValue(
				annot.getEvidenceCode());
		}
	}

}
