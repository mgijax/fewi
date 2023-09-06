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
import org.jax.mgi.fe.datamodel.MPAnnotation;
import org.jax.mgi.fe.datamodel.MPAnnotationReference;
import org.jax.mgi.fe.datamodel.MPGenotype;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fewi.util.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelMarkerPhenotypesSummary  extends AbstractBigExcelView
{
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(ExcelMarkerPhenotypesSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");

		String userAgent = request.getHeader("User-Agent");

		// get the marker
		Marker marker = (Marker) model.get("marker");

		// set the multigenic flag
		boolean isMultigenic = false;
		if (model.containsKey("multigenic")) {
			isMultigenic = true;
		}

		// retrieve list of genotypes
		List<MPGenotype> genotypes;
		if (isMultigenic) {
			genotypes = marker.getMultigenicMpGenotypes();
		} else {
			genotypes = marker.getRolledUpMpGenotypes();
		}

		// setup
		String filename = "MGImarkerPhenotypes_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		// create worksheet
		Sheet sheet = workbook.createSheet();

		// define the common styles
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		this.addStyles(sheet, styles);

		// add the header row column headings
		String[] headerTitles = {
			"Allelic Composition",
			"Genetic Background",
			"Genotype ID",
			"Qualifier",
			"Annotated Term",
			"Phenotype Summary Category",
			"Reference",
		};

		int[] columnWidths = { 0, 0, 0, 0, 0, 0, 0 };
		int[] maxColumnWidths = { 50, 50, 20, 20, 50, 50, 15 };

		try {
			addHeaderRow(sheet, columnWidths, styles,
				headerTitles);
		} catch (Exception e) {}

		// create and fill the rows objects
		int rownum = 1;

		for (MPGenotype genotype : genotypes) {

		    String strain = genotype.getStrain();
		    String alleles = FormatHelper.stripAlleleTags (
			genotype.getAllelePairs());
		    String genotypeID = genotype.getPrimaryID();

		    for (MPAnnotation annot : genotype.getMpAnnotations()) {
			String term = annot.getTerm();
			String qualifier = annot.getQualifier();

			List<String> mpHeaders = annot.getMPHeaders();

			if ((mpHeaders != null) && (mpHeaders.size() > 0)) {
			  for (String mpHeader : mpHeaders) {
			    for (MPAnnotationReference ref : annot.getMpReferences()) {
				List<String> row = new ArrayList<String>();
				row.add(alleles);
				row.add(strain);
				row.add(genotypeID);
				row.add(qualifier);
				row.add(term);
				row.add(mpHeader);
				row.add(ref.getJnumID());
				addDataRow(sheet, columnWidths, styles, row);
			    }
			  }
			} else {
			    // no MP headers, but still need refs

			    String mpHeader = "";
			    for (MPAnnotationReference ref : annot.getMpReferences()) {
				List<String> row = new ArrayList<String>();
				row.add(alleles);
				row.add(strain);
				row.add(genotypeID);
				row.add(qualifier);
				row.add(term);
				row.add(mpHeader);
				row.add(ref.getJnumID());
				addDataRow(sheet, columnWidths, styles, row);
			    }
			}
		    }
		}

		applyStandardFormat(sheet, columnWidths, maxColumnWidths);
	}
}
