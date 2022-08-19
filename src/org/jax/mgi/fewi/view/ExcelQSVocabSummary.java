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
import org.jax.mgi.fewi.summary.QSVocabResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelQSVocabSummary  extends AbstractBigExcelView
{
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(ExcelQSVocabSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		String userAgent = request.getHeader("User-Agent");

		// get the vocab terms
		List<QSVocabResultWrapper> terms = (List<QSVocabResultWrapper>) model.get("terms");

		// setup
		String filename = "MGI_VocabTerms_" + getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// create worksheet
		Sheet sheet = workbook.createSheet();

		// define the common styles
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		this.addStyles(sheet, styles);

		// add the header row column headings
		String[] headerTitles = {
			"Vocabulary",
			"Term ID",
			"Term",				// 2
			"Associated Data",
			"Best Match Type",	// 4
			"Best Match",
			"Match Score",  // 6
		};

		int[] columnWidths = { 0, 0, 0, 0, 0, 0, 0 };
		int[] maxColumnWidths = { 30, 20, 50, 50, 30, 40, 10 };

		try {
			addHeaderRow(sheet, columnWidths, styles, headerTitles);
		} catch (Exception e) {}

		Pattern nonZero = Pattern.compile("[1-9]");

		// create and fill the row objects
		for (QSVocabResultWrapper term : terms) {
			String primaryID = "";
			if (term.getPrimaryID() != null) {
				primaryID= term.getPrimaryID();
			}

			String annotText = "";
			if (term.getAnnotationText() != null) {
				Matcher mat = nonZero.matcher(term.getAnnotationText());
				if (mat.find()) {
					annotText = term.getAnnotationText();
				}
			}
			List<String> row = new ArrayList<String>();
			row.add(term.getVocabName());
			row.add(primaryID);

			row.add(term.getTerm());
			row.add(annotText);

			row.add(term.getBestMatchType());
			row.add(term.getBestMatchText());
			row.add(""+term.getStars().length());

			addDataRow(sheet, columnWidths, styles, row);
		}
		applyStandardFormat(sheet, columnWidths, maxColumnWidths);
	}
}
