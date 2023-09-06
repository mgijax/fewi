package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.summary.QSStrainResultWrapper;
import org.jax.mgi.fewi.util.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelQSStrainSummary  extends AbstractBigExcelView
{
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(ExcelQSStrainSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		String userAgent = request.getHeader("User-Agent");

		// get the strains
		List<QSStrainResultWrapper> strains = (List<QSStrainResultWrapper>) model.get("strains");

		// setup
		String filename = "MGI_Strains_" + getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// create worksheet
		Sheet sheet = workbook.createSheet();

		// define the common styles
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		this.addStyles(sheet, styles);

		// add the header row column headings
		String[] headerTitles = {
			"Primary ID",
			"Name",
			"References",		// 2
			"Best Match Type",
			"Best Match",		// 4
			"Match Score",		// 5
		};

		int[] columnWidths = { 0, 0, 0, 0, 0, 0 };
		int[] maxColumnWidths = { 20, 40, 20, 30, 50, 10 };

		try {
			addHeaderRow(sheet, columnWidths, styles, headerTitles);
		} catch (Exception e) {}

		Pattern nonZero = Pattern.compile("[1-9]");

		// create and fill the row objects
		for (QSStrainResultWrapper strain : strains) {
			String primaryID = "";
			if (strain.getPrimaryID() != null) {
				primaryID= strain.getPrimaryID();
			}

			String references = "";
			if (strain.getReferenceCount() != null) {
				if (strain.getReferenceCount() > 0) {
					references = strain.getReferenceCount() + FormatHelper.plural(strain.getReferenceCount(), " reference");
				}
			}
			List<String> row = new ArrayList<String>();
			row.add(primaryID);
			row.add(strain.getName());
			row.add(references);

			row.add(strain.getBestMatchType());
			row.add(strain.getBestMatchText());
			row.add(""+strain.getStars().length());

			addDataRow(sheet, columnWidths, styles, row);
		}
		applyStandardFormat(sheet, columnWidths, maxColumnWidths);
	}
}
