package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.summary.QSOtherResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelQSOtherIDSummary  extends AbstractBigExcelView
{
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(ExcelQSOtherIDSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		String userAgent = request.getHeader("User-Agent");

		// get the other IDs
		List<QSOtherResultWrapper> otherIDs = (List<QSOtherResultWrapper>) model.get("otherIDs");

		// setup
		String filename = "MGI_OtherIDs_" + getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// create worksheet
		Sheet sheet = workbook.createSheet();

		// define the common styles
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		this.addStyles(sheet, styles);

		// add the header row column headings
		String[] headerTitles = {
			"Type",
			"Subtype",
			"Primary ID",		// 2
			"Name/Description",	
			"Best Match Type",	// 4
			"Best Match"	
		};

		int[] columnWidths = { 0, 0, 0, 0, 0, 0 };
		int[] maxColumnWidths = { 30, 30, 20, 50, 30, 40 };

		try {
			addHeaderRow(sheet, columnWidths, styles, headerTitles);
		} catch (Exception e) {}

		Pattern nonZero = Pattern.compile("[1-9]");

		// create and fill the row objects
		for (QSOtherResultWrapper otherID : otherIDs) {
			String primaryID = "";
			if (otherID.getPrimaryID() != null) {
				primaryID= otherID.getPrimaryID();
			}

			List<String> row = new ArrayList<String>();
			row.add(otherID.getObjectType());
			row.add(otherID.getObjectSubtype());
			row.add(primaryID);

			row.add(otherID.getName());
			row.add(otherID.getBestMatchType());
			row.add(otherID.getBestMatchText());

			addDataRow(sheet, columnWidths, styles, row);
		}
		applyStandardFormat(sheet, columnWidths, maxColumnWidths);
	}
}
