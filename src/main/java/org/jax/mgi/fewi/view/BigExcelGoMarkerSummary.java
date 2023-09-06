package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.AnnotationInferredFromID;
import org.jax.mgi.fe.datamodel.AnnotationProperty;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.util.NotesTagConverter;

public class BigExcelGoMarkerSummary extends AbstractBigExcelView {

	private final NotesTagConverter ntc = new NotesTagConverter();

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String,Object> model, SXSSFWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String filename = "GO_marker_summary_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");
		

		List<Annotation> results = (List<Annotation>) model.get("results");

		Sheet sheet = workbook.createSheet();
		Row row;

		int rownum = 0;
		int col = 0;

		row = sheet.createRow(rownum++);
		row.createCell(col++).setCellValue("Aspect");
		row.createCell(col++).setCellValue("Category");
		row.createCell(col++).setCellValue("Qualifier");
		row.createCell(col++).setCellValue("Classification Term");
		row.createCell(col++).setCellValue("Context");
		row.createCell(col++).setCellValue("Proteoform");
		row.createCell(col++).setCellValue("Evidence");
		row.createCell(col++).setCellValue("Inferred From");
		row.createCell(col++).setCellValue("Reference(s)");

		StringBuffer inferred;
		StringBuffer refs;
		StringBuffer proteoforms;

		for (Annotation annot: results){
			row = sheet.createRow(rownum++);
			col = 0;

			// Aspect
			row.createCell(col++).setCellValue(annot.getDagName());
			
			// Category
			row.createCell(col++).setCellValue(annot.getHeaderAbbreviations());
			
			// Qualifier
			if (annot.getQualifier() != null) {
			    row.createCell(col++).setCellValue(annot.getQualifier());
			} else {
			    row.createCell(col++).setCellValue("");
			}
			
			// Classification Term
			row.createCell(col++).setCellValue(annot.getTerm());
			
			// Additional term context
			String annotExtensions = ntc.convertNotes(annot.getAnnotationExtensionTextOutput(),'|',true);
			row.createCell(col++).setCellValue(annotExtensions);

			// Proteoform
			proteoforms = new StringBuffer();
			for (AnnotationProperty prop: annot.getIsoforms()) {
				String displayItem = prop.getValue();
				displayItem = ntc.convertNotes(displayItem, '|', true);
				proteoforms.append(displayItem);
			}
			row.createCell(col++).setCellValue(proteoforms.toString());
			
			// evidence
			row.createCell(col++).setCellValue(annot.getEvidenceCode());

			// inferredfrom
			inferred = new StringBuffer();
			for (AnnotationInferredFromID inf: annot.getInferredFromList()) {
				inferred.append(inf.getAccID() + " ");
			}
			row.createCell(col++).setCellValue(inferred.toString());

			// references
			refs = new StringBuffer();
			for (Reference ref: annot.getReferences()) {
				refs.append(ref.getJnumID() + " ");
			}
			row.createCell(col++).setCellValue(refs.toString());
		}
	}

}
