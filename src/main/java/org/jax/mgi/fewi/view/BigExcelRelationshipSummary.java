package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;

public class BigExcelRelationshipSummary extends AbstractBigExcelView {

	@Override
	protected void buildExcelDocument(Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Sheet sheet = workbook.createSheet();			
		Row row;
	
		int rownum = 0;
		int col = 0;
		
		row = sheet.createRow(rownum++);
		row.createCell(col++).setCellValue("OrganizerID");
		row.createCell(col++).setCellValue("OrganizerSymbol");
		row.createCell(col++).setCellValue("RelationshipTerm");
		row.createCell(col++).setCellValue("ParticipantID");
		row.createCell(col++).setCellValue("ParticipantSymbol");
		row.createCell(col++).setCellValue("EvidenceCode");
		row.createCell(col++).setCellValue("Score");
		row.createCell(col++).setCellValue("ScoreSource");
		row.createCell(col++).setCellValue("Validation");
		row.createCell(col++).setCellValue("ReferenceId");
		
		row.createCell(col++).setCellValue("ParticipantProductID");
		row.createCell(col++).setCellValue("OrganizerProductID");
		row.createCell(col++).setCellValue("Algorithm");
		row.createCell(col++).setCellValue("OtherReferences");
		
		row.createCell(col++).setCellValue("Notes");

		@SuppressWarnings("unchecked")
		List<SolrInteraction> relationships = (List<SolrInteraction>) model.get("results");
		for (SolrInteraction r : relationships) {
			row = sheet.createRow(rownum++);
			col = 0;
			row.createCell(col++).setCellValue(r.getOrganizerID());
			row.createCell(col++).setCellValue(r.getOrganizerSymbol());
			row.createCell(col++).setCellValue(r.getRelationshipTerm());
			row.createCell(col++).setCellValue(r.getParticipantID());
			row.createCell(col++).setCellValue(r.getParticipantSymbol());
			row.createCell(col++).setCellValue(r.getEvidenceCode());
			row.createCell(col++).setCellValue(r.getScore());
			row.createCell(col++).setCellValue(r.getScoreSource());
			row.createCell(col++).setCellValue(r.getValidation());
			row.createCell(col++).setCellValue(r.getJnumID());
			
			row.createCell(col++).setCellValue(r.getParticipantProductID());
			row.createCell(col++).setCellValue(r.getOrganizerProductID());
			row.createCell(col++).setCellValue(r.getAlgorithm());
			row.createCell(col++).setCellValue(r.getOtherReferences());
			
			row.createCell(col++).setCellValue(r.getNotes());
		}
	}

}