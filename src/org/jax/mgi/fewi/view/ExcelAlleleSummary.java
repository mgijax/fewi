package org.jax.mgi.fewi.view;

import java.util.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.phenotype.AlleleSummaryDisease;
import mgi.frontend.datamodel.phenotype.AlleleSummarySystem;

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

public class ExcelAlleleSummary  extends AbstractBigExcelView
{
    //-------------------
    // instance variables
    //-------------------
    private static String NORMAL_PHENOTYPE="normal phenotype";
    private static String PHENOTYPE_NOT_ANALYZED="phenotype not analyzed";
    private static String CELL_LINE="Cell Line";
    private static String CHIMERIC="Chimeric";
    private static String NOT_APPLICABLE="Not Applicable";

	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelAlleleSummary.class);

	@Override
	public void buildExcelDocument(
			Map model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
	{
		logger.debug("buildExcelDocument");
		String filename = "MGIalleleQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

		List<Allele> alleles = (List<Allele>) model.get("alleles");

		// write the headers
		Sheet sheet = workbook.createSheet();
		String[] headerTitles = {
			"MGI Allele ID",
			"Allele Symbol",
			"Allele Name",
			"Chromosome",
			"Synonyms",
			"Allele Type",
			"Allele Attributes",
			"Transmission",
			"Abnormal Phenotypes Reported in these Systems",
			"Human Disease Models"
		};

		Row header = sheet.createRow(0);
		// add the header row
		for(int i=0;i<headerTitles.length;i++)
		{
			header.createCell(i).setCellValue(headerTitles[i]);
		}
		Row row;
		int rownum = 1;

		for (Allele allele : alleles)
		{
            // prep synonyms
            List<String> synonyms = new ArrayList<String>();
            for(AlleleSynonym synonym : allele.getSynonyms())
            {
                synonyms.add(synonym.getSynonym());
            }

            // prep systems
            List<String> systems = new ArrayList<String>();
            for(AlleleSummarySystem system : allele.getSummarySystems())
            {
              if(!NORMAL_PHENOTYPE.equals(system.getSystem())
				  && !PHENOTYPE_NOT_ANALYZED.equals(system.getSystem()))
              {
                systems.add(system.getSystem());
              }
            }

            // prep diseases
            List<String> diseases = new ArrayList<String>();
            for(AlleleSummaryDisease disease : allele.getSummaryDiseases())
            {
              diseases.add(disease.getDisease() + " " + disease.getOmimID());
            }

            // prep allele attribute
            String subtype = allele.getAlleleSubType();
            if (subtype == null) {subtype = " ";}

            // prep transmission
            String transmissionType = allele.getTransmissionType();
            if (transmissionType != null && NOT_APPLICABLE.equals(transmissionType))
            {
				transmissionType = " ";
			}

			row = sheet.createRow(rownum++);
			row.createCell(0).setCellValue(allele.getPrimaryID());
			row.createCell(1).setCellValue(allele.getSymbol());
			row.createCell(2).setCellValue(allele.getName());
			row.createCell(3).setCellValue(allele.getChromosome());
			row.createCell(4).setCellValue(StringUtils.join(synonyms," | "));
			row.createCell(5).setCellValue(allele.getAlleleType());
			row.createCell(6).setCellValue(subtype);
			row.createCell(7).setCellValue(transmissionType);
			row.createCell(8).setCellValue(StringUtils.join(systems," | "));
			row.createCell(9).setCellValue(StringUtils.join(diseases," | "));
		}

	}
}
