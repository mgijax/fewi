package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelMarkerSummary  extends AbstractBigExcelView
{
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelMarkerSummary.class);

	@Override
	public void buildExcelDocument(
			Map model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		// retrieve list of markers from controller
		List<SolrSummaryMarker> markers = (List<SolrSummaryMarker>) model.get("markers");

		// setup
		String filename = "MGImarkerQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// only display "why" it matched when nomen param isn't empty
		boolean displayMatches = false;
		if (!request.getParameter("nomen").equals("")){
			displayMatches = true;
		}

		// create worksheet add the header row column headings
		Sheet sheet = workbook.createSheet();
		String[] headerTitles = {
			"Chromosome",
			"Start",
			"End",
			"cM",
			"strand GRCm38",
			"MGI ID",
			"Feature Type",
			"Symbol",
			"Name",
			""
		};
		if (displayMatches == true) {
			headerTitles[9] = "Matching Text";
		}
		Row header = sheet.createRow(0);
		for(int i=0;i<headerTitles.length;i++)
		{
			header.createCell(i).setCellValue(headerTitles[i]);
		}

		// create and fill the rows objects
		Row row;
		int rownum = 1;
		for (SolrSummaryMarker marker : markers)
		{
			row = sheet.createRow(rownum++);
			row.createCell(0).setCellValue(marker.getChromosome());
            row.createCell(3).setCellValue(marker.getCoordStartStr());
            row.createCell(4).setCellValue(marker.getCoordEndStr());
			row.createCell(1).setCellValue(marker.getCmStr());
			row.createCell(5).setCellValue(marker.getStrand());
			row.createCell(6).setCellValue(marker.getMgiId());
			row.createCell(7).setCellValue(marker.getFeatureType());
			row.createCell(8).setCellValue(marker.getSymbol());
			row.createCell(9).setCellValue(marker.getName());
			if (displayMatches == true) {
				row.createCell(10).setCellValue(StringUtils.join(marker.getHighlights(),", "));
			}
		}
	}
}
