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
	private final Logger logger = LoggerFactory.getLogger(ExcelMarkerSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		// retrieve list of markers from controller
		List<SolrSummaryMarker> markers = (List<SolrSummaryMarker>) model.get("markers");

		// setup
		String filename = "MGImarkerQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// only display "why" it matched when nomen param isn't empty
		boolean displayMatches = false;
		if (request.getParameter("nomen")!=null && !request.getParameter("nomen").equals("")){
			displayMatches = true;
		}

		// create worksheet add the header row column headings
		Sheet sheet = workbook.createSheet();
		String[] headerTitles = {
			"Chromosome",
			"Start",
			"End",
			"cM",
			"strand GRCm39",
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
			this.addNextCellValue(row,marker.getChromosome());
			this.addNextCellValue(row,marker.getCoordStartStr());
			this.addNextCellValue(row,marker.getCoordEndStr());
			this.addNextCellValue(row,marker.getCmStr());
			this.addNextCellValue(row,marker.getStrand());
			this.addNextCellValue(row,marker.getMgiId());
			this.addNextCellValue(row,marker.getFeatureType());
			this.addNextCellValue(row,marker.getSymbol());
			this.addNextCellValue(row,marker.getName());
			if (displayMatches == true) {
				this.addNextCellValue(row,StringUtils.join(marker.getHighlights(),", "));
			}
		}
	}
}
