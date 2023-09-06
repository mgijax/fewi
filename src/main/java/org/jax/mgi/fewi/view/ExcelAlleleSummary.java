package org.jax.mgi.fewi.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.AlleleSynonym;
import org.jax.mgi.fe.datamodel.phenotype.AlleleSummaryDisease;
import org.jax.mgi.fe.datamodel.phenotype.AlleleSummarySystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelAlleleSummary  extends AbstractBigExcelView
{
    //-------------------
    // instance variables
    //-------------------
    private static String NORMAL_PHENOTYPE="normal phenotype";
    private static String PHENOTYPE_NOT_ANALYZED="phenotype not analyzed";
    //private static String CELL_LINE="Cell Line";
    //private static String CHIMERIC="Chimeric";
    private static String NOT_APPLICABLE="Not Applicable";

	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelAlleleSummary.class);

	@SuppressWarnings("unchecked")
	@Override
	public void buildExcelDocument(
			Map<String,Object> model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
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
              diseases.add(disease.getDisease() + " " + disease.getDoID());
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
			this.addNextCellValue(row,allele.getPrimaryID());
			this.addNextCellValue(row,allele.getSymbol());
			this.addNextCellValue(row,allele.getName());
			this.addNextCellValue(row,allele.getChromosome());
			this.addNextCellValue(row,StringUtils.join(synonyms," | "));
			this.addNextCellValue(row,allele.getAlleleType());
			this.addNextCellValue(row,subtype);
			this.addNextCellValue(row,transmissionType);
			this.addNextCellValue(row,StringUtils.join(systems," | "));
			this.addNextCellValue(row,StringUtils.join(diseases," | "));
		}

	}
}
