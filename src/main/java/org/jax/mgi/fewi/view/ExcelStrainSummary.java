package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.shr.jsonmodel.AccessionID;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelStrainSummary  extends AbstractBigExcelView
{
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(ExcelStrainSummary.class);

	// return the contents of 'items' as a single, pipe-delimited String
	private String pipeDelimited(List<String> items) {
		if ((items == null) || (items.size() == 0)) {
			return "";
		}
		return StringUtils.join(items, "|");
	}
	
	// return a list of just the ID strings for the IDs of the given 'strain'
	private List<String> getIDs (SimpleStrain strain) {
		List<String> ids = new ArrayList<String>();
		for (AccessionID id : strain.getAccessionIDs()) {
			String displayID = id.getAccID();
			try {
				Integer i = Integer.parseInt(displayID);
				if (id.getLogicalDB().equals("JAX Registry")) {
					ids.add("JAX:" + displayID);
				} else {
					ids.add(id.getLogicalDB() + ":" + displayID);
				}
			} catch (Exception e) {
				// ID has non-numeric characters, so trust it has a prefix
				ids.add(displayID);
			}
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		String userAgent = request.getHeader("User-Agent");

		// get the strains
		List<SimpleStrain> strains = (List<SimpleStrain>) model.get("strains");

		// setup
		String filename = "MGIstrains_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// create worksheet
		Sheet sheet = workbook.createSheet();

		// define the common styles
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		this.addStyles(sheet, styles);

		// add the header row column headings
		String[] headerTitles = {
			"Official Strain Name",
			"MGI ID",
			"Synonyms",
			"Attributes",
			"IDs",
			"References",
		};

		int[] columnWidths = { 0, 0, 0, 0, 0, 0 };
		int[] maxColumnWidths = { 50, 20, 50, 50, 50, 15 };

		try {
			addHeaderRow(sheet, columnWidths, styles, headerTitles);
		} catch (Exception e) {}

		// create and fill the row objects
		for (SimpleStrain strain : strains) {
			List<String> row = new ArrayList<String>();
			row.add(strain.getName());
			row.add(strain.getPrimaryID());
			row.add(pipeDelimited(strain.getSynonyms()));
			row.add(pipeDelimited(strain.getAttributes()));
			row.add(pipeDelimited(getIDs(strain)));
			row.add("" + strain.getReferenceCount());
			addDataRow(sheet, columnWidths, styles, row);
		}
		applyStandardFormat(sheet, columnWidths, maxColumnWidths);
	}
}
