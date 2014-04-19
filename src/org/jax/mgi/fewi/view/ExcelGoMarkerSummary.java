package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.Reference;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelGoMarkerSummary extends AbstractExcelView {

	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, 
			HttpServletRequest request, HttpServletResponse response) {

		@SuppressWarnings("unchecked")
		List<Annotation> results = (List<Annotation>) model.get("results");		
		
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row;
	
		int rownum = 0;
		int col = 0;
		
		row = sheet.createRow(rownum++);
		row.createCell(col++).setCellValue("Category");
		row.createCell(col++).setCellValue("Classification Term");
		row.createCell(col++).setCellValue("Evidence");
		row.createCell(col++).setCellValue("Inferred From");
		row.createCell(col++).setCellValue("Reference(s)");
		
		StringBuffer inferred;
		StringBuffer refs;
		for (Annotation annot: results){
			row = sheet.createRow(rownum++);
			col = 0;
			
			row.createCell(col++).setCellValue(annot.getDagName());
			row.createCell(col++).setCellValue(annot.getTerm());
			row.createCell(col++).setCellValue(annot.getEvidenceCode());
			
			inferred = new StringBuffer();
			for (AnnotationInferredFromID inf: annot.getInferredFromList()) {
				inferred.append(inf.getAccID() + " ");
			}
			row.createCell(col++).setCellValue(inferred.toString());

			refs = new StringBuffer();
			for (Reference ref: annot.getReferences()) {
				refs.append(ref.getJnumID() + " ");
			}
			row.createCell(col++).setCellValue(refs.toString());
		}
	}

}