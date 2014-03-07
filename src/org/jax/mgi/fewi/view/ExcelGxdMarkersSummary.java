package org.jax.mgi.fewi.view;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.ImagePane;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGxdMarkersSummary  extends AbstractBigExcelView
{
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelGxdMarkersSummary.class);
	
	String assemblyBuild = ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION");

	@Override
	public void buildExcelDocument(
			Map model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
			logger.debug("buildExcelDocument");
			String filename = "MGIgeneExpressionQuery_markers_"+getCurrentDate();
			response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

			GxdBatchFinder finder = (GxdBatchFinder) model.get("markerFinder");

			// set the batchSize
			int batchSize = 5000;
			finder.batchSize = batchSize;

			Sheet sheet = workbook.createSheet();
			String[] headerTitles = {"MGI Gene ID",
					"Gene Symbol",
					"Gene Name",
					"Type",
					"Chr",
					"Genome Location-" + assemblyBuild,
					"cM",
					"Strand"};
			Row header = sheet.createRow(0);
			// add the header row
			for(int i=0;i<headerTitles.length;i++)
			{
				header.createCell(i).setCellValue(headerTitles[i]);
			}

			Row row;
			int rownum = 1;

			try
			{
			// need this for processing genotype;
			NotesTagConverter ntc = new NotesTagConverter();
			while(finder.hasNextMarkers())
			{
				SearchResults<SolrGxdMarker> markers = finder.getNextMarkers();
				for (SolrGxdMarker m: markers.getResultObjects()) {
					// use the Marker Summary row to get the correct Genome Location text
					GxdMarkerSummaryRow mr = new GxdMarkerSummaryRow(m);

					row = sheet.createRow(rownum++);

					// for now we will steal logic from the summary row class
					row.createCell(0).setCellValue(mr.getPrimaryID());
					row.createCell(1).setCellValue(m.getSymbol());
					row.createCell(2).setCellValue(mr.getName());
					row.createCell(3).setCellValue(mr.getType());
					row.createCell(4).setCellValue(mr.getChr());
					row.createCell(5).setCellValue(mr.getLocation());
					row.createCell(6).setCellValue(mr.getCM());
					row.createCell(7).setCellValue(mr.getStrand());
				}
			}
			}
			catch (IOException e)
			{
				// meh
			}

		}
}
