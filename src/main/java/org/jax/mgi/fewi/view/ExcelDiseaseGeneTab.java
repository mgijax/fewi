package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.jax.mgi.fe.datamodel.*;
import org.jax.mgi.fewi.util.DiseaseModelFilter;
import org.jax.mgi.fewi.util.NotesTagConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelDiseaseGeneTab extends AbstractBigExcelView {


	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelDiseaseModelTab.class);
	
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, 
			SXSSFWorkbook workbook, 
			HttpServletRequest request, 
			HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");
		
		response.setHeader("Content-Disposition","attachment; filename=\"geneTabReport.xlsx\"");
		//System.out.println(response.getCharacterEncoding());
		
		// pull in the disease for this report
		@SuppressWarnings("unchecked")
		Disease disease = (Disease) model.get("disease");
		
		// group identifiers to display where/how this model was found
		String mouseAndHuman = "MouseAndHuman";
		String mouseOnly     = "MouseOnly";
		String humanOnly     = "HumanOnly";

		// order to display
		List<String> groupIdentifiers = new ArrayList<String>();
		groupIdentifiers.add(mouseAndHuman);
		groupIdentifiers.add(mouseOnly);
		groupIdentifiers.add(humanOnly);
		
		// map group identifiers to list of disease models for this disease
		Map<String, List<DiseaseGroupRow>> diseaseGroupRows = new HashMap<String, List<DiseaseGroupRow>>();
		if (disease.getMouseHumanGroup() != null) {
			diseaseGroupRows.put(mouseAndHuman, disease.getMouseHumanGroup().getDiseaseGroupRows());
		}
		if (disease.getMouseOnlyGroup() != null) {
			diseaseGroupRows.put(mouseOnly,     disease.getMouseOnlyGroup().getDiseaseGroupRows());
		}
		if (disease.getHumanOnlyGroup() != null) {
			diseaseGroupRows.put(humanOnly,     disease.getHumanOnlyGroup().getDiseaseGroupRows());
		}
		
		Sheet sheet = workbook.createSheet();
		String[] headerTitles = {"GeneCategory",
				"Disease Term",
				"Mouse Homologs",
				"Human Homologs",
				"Transgenes And Other Features",
				"Mouse Models",
				"Homology Source"};

	
		
		Row header = sheet.createRow(0);
		// add the header row
		for(int i=0;i<headerTitles.length;i++)
		{
			header.createCell(i).setCellValue(headerTitles[i]);
		}

		Row row;
		int rownum = 1;
		
		// loop though ordered list of groups
		for (String groupIdentifier : groupIdentifiers) {
			
			List<DiseaseGroupRow> dgRows = diseaseGroupRows.get(groupIdentifier);

			// if we have any, loop through these models and output to file writer
			if (dgRows != null) {
			for (DiseaseGroupRow dgRow : dgRows) {
			
				DiseaseRow dRow = dgRow.getDiseaseRow();

				row = sheet.createRow(rownum++);

				// add group identifier and disease term
				row.createCell(0).setCellValue(groupIdentifier);
				row.createCell(1).setCellValue(dgRow.getAnnotatedDisease());

				// mouse markers
				StringBuffer  mouseMarkerBuffer = new StringBuffer(" ");
				for (DiseaseRowToMarker mMarker : dRow.getMouseMarkers()) {
					mouseMarkerBuffer.append(mMarker.getSymbol());
					if (mMarker.getIsCausative() == 1){
						mouseMarkerBuffer.append("*");					
					}
					mouseMarkerBuffer.append(" ");
				}
				row.createCell(2).setCellValue(mouseMarkerBuffer.toString());
				
				// human markers
				StringBuffer  humanMarkerBuffer = new StringBuffer(" ");
				for (DiseaseRowToMarker hMarker : dRow.getHumanMarkers()) {
					humanMarkerBuffer.append(hMarker.getSymbol());
					if (hMarker.getIsCausative() == 1){
						humanMarkerBuffer.append("*");					
					}
					humanMarkerBuffer.append(" ");
				}
				row.createCell(3).setCellValue(humanMarkerBuffer.toString());

				// trangenes column empty
				row.createCell(4).setCellValue(" ");
						
				// model count
				row.createCell(5).setCellValue(dRow.getMouseModels().size());

				// homology cluster source
				String source = "";
				if (dRow.getHomologyCluster() != null){
				  source = dRow.getHomologyCluster().getSecondarySource();
				}
				row.createCell(6).setCellValue(source);
				
			}
			}
		}

		// transgene rows fill different columns
		if (disease.getOtherGroup() != null) {
		for (DiseaseGroupRow dgRow : disease.getOtherGroup().getDiseaseGroupRows()) {
			
			DiseaseRow dRow = dgRow.getDiseaseRow();

			row = sheet.createRow(rownum++);

			// add group identifier and disease term
			row.createCell(0).setCellValue("TransgenesAndOtherMutations");
			row.createCell(1).setCellValue(dgRow.getAnnotatedDisease());

			// mouse markers are empty
			row.createCell(2).setCellValue(" ");

			// human markers are empty
			row.createCell(3).setCellValue(" ");

			// trangenes column
			StringBuffer  mouseMarkerBuffer = new StringBuffer(" ");
			for (DiseaseRowToMarker marker : dRow.getMouseMarkers()) {
				mouseMarkerBuffer.append(marker.getSymbol());
				mouseMarkerBuffer.append(" ");
			}
			row.createCell(4).setCellValue(mouseMarkerBuffer.toString());
					
			// model count
			row.createCell(5).setCellValue(dRow.getMouseModels().size());

			// homology cluster source is empty for this group
			String source = "";
			if (dRow.getHomologyCluster() != null){
			  source = dRow.getHomologyCluster().getSecondarySource();
			}
			row.createCell(6).setCellValue(" ");

		}
		}
				
	} // buildExcelDocument class

}
