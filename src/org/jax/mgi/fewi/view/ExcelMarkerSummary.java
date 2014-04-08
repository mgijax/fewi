package org.jax.mgi.fewi.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String filename = "MGImarkerQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		List<SolrSummaryMarker> markers = (List<SolrSummaryMarker>) model.get("markers");

		// write the headers
		Sheet sheet = workbook.createSheet();
		String[] headerTitles = {
			"Genetic Chr",
			"cM",
			"Genomic Chr",
			"start",
			"end",
			"strand GRCm38",
			"MGI ID",
			"Feature Type",
			"Symbol",
			"Name"
		};

		Row header = sheet.createRow(0);
		// add the header row
		for(int i=0;i<headerTitles.length;i++)
		{
			header.createCell(i).setCellValue(headerTitles[i]);
		}
		Row row;
		int rownum = 1;

		// create and fill the row object
		for (SolrSummaryMarker marker : markers)
		{
			row = sheet.createRow(rownum++);
			row.createCell(0).setCellValue(marker.getChromosome());
			row.createCell(1).setCellValue(marker.getCmStr());
			row.createCell(2).setCellValue(marker.getChromosome());
            row.createCell(3).setCellValue(marker.getCoordStartStr());
            row.createCell(4).setCellValue(marker.getCoordEndStr());
			row.createCell(5).setCellValue(marker.getStrand());
			row.createCell(6).setCellValue(marker.getMgiId());
			row.createCell(7).setCellValue(marker.getFeatureType());
			row.createCell(8).setCellValue(marker.getSymbol());
			row.createCell(9).setCellValue(marker.getName());
		}
	}
}
