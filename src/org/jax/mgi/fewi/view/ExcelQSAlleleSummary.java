package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.summary.QSAlleleResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelQSAlleleSummary  extends AbstractBigExcelView
{
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(ExcelQSAlleleSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		String userAgent = request.getHeader("User-Agent");

		// get the alleles
		List<QSAlleleResultWrapper> alleles = (List<QSAlleleResultWrapper>) model.get("alleles");

		// setup
		String filename = "MGI_Alleles_" + getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// create worksheet
		Sheet sheet = workbook.createSheet();

		// define the common styles
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		this.addStyles(sheet, styles);

		// add the header row column headings
		String[] headerTitles = {
			"Type",
			"MGI ID",
			"Symbol",			// 2
			"Name",
			"Chr",				// 4
			"Start",
			"End",				// 6
			"Build",
			"Strand",			// 8
			"Best Match Type",
			"Best Match",		// 10
		};

		int[] columnWidths = { 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0 };
		int[] maxColumnWidths = { 50, 20, 50, 60, 50, 10, 15, 10, 10, 20, 50 };

		try {
			addHeaderRow(sheet, columnWidths, styles, headerTitles);
		} catch (Exception e) {}

		// create and fill the row objects
		for (QSAlleleResultWrapper allele : alleles) {
			ParsedLocation location = new ParsedLocation(allele.getLocation());
			String primaryID = "";
			if (allele.getDetailUri() != null) {
				String[] pieces = allele.getDetailUri().split("/");
				if (pieces.length > 1) {
					primaryID = pieces[pieces.length - 1];
				}
			}

			List<String> row = new ArrayList<String>();
			row.add(allele.getFeatureType());
			row.add(primaryID);

			row.add(allele.getSymbol());
			row.add(allele.getName());
			row.add(allele.getChromosome());
			
			row.add(location.getStartCoord());
			row.add(location.getEndCoord());
			row.add(location.getBuild());

			row.add(allele.getStrand());
			row.add(allele.getBestMatchType());
			row.add(allele.getBestMatchText());

			addDataRow(sheet, columnWidths, styles, row);
		}
		applyStandardFormat(sheet, columnWidths, maxColumnWidths);
	}
}
