package org.jax.mgi.fewi.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGxdResultsSummary  extends AbstractBigExcelView
{
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ExcelGxdResultsSummary.class);

	// set a maximum download size for sanity's sake
	public int MAX_ROWS = 800000;
	
	@Override
	public void buildExcelDocument(
			Map model, SXSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
			logger.debug("buildExcelDocument");
			String filename = "MGIgeneExpressionQuery_"+getCurrentDate();
			response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\"");

			GxdBatchFinder finder = (GxdBatchFinder) model.get("resultFinder");

			// set the batchSize
			int batchSize = 1000;
			finder.batchSize = batchSize;

			Sheet sheet = workbook.createSheet();
			String[] headerTitles = {"MGI Gene ID",
					"Gene Symbol",
					"Gene Name",
					"MGI Assay ID",
					"Assay Type",
					"Anatomical System",
					"Age",
					"Theiler Stage",
					"Structure",
					"Detected",
					"Images",
					"Mutant Allele(s)",
					"MGI Reference ID",
					"PubMed ID",
					"Citation"};
			Row header = sheet.createRow(0);
			// add the header row
			for(int i=0;i<headerTitles.length;i++)
			{
				header.createCell(i).setCellValue(headerTitles[i]);
			}

			Row row;
			int rownum = 1;

			// need this for processing genotype;
			NotesTagConverter ntc = new NotesTagConverter();
			while(finder.hasNextResults())
			{
				if(rownum > MAX_ROWS) break;
				SearchResults<SolrAssayResult> sr = finder.getNextResults();
				for (SolrAssayResult r: sr.getResultObjects()) {
					row = sheet.createRow(rownum++);
					row.createCell(0).setCellValue(r.getMarkerMgiid());
					row.createCell(1).setCellValue(r.getMarkerSymbol());
					row.createCell(2).setCellValue(r.getMarkerName());
					row.createCell(3).setCellValue(r.getAssayMgiid());
					row.createCell(4).setCellValue(r.getAssayType());
					row.createCell(5).setCellValue(r.getAnatomicalSystem());
					row.createCell(6).setCellValue(r.getAge());
					row.createCell(7).setCellValue(r.getTheilerStage());
					row.createCell(8).setCellValue(r.getPrintname());
					row.createCell(9).setCellValue(r.getDetectionLevel());

					// generate the figure text
					String figureText = "";
					List<String> formattedFigures = new ArrayList<String>(0);
					if(r.getFiguresPlain() != null)
					{
						for(String figure : r.getFiguresPlain())
						{
							if(!figure.equals(""))
							{
								formattedFigures.add(figure);
							}
						}
					}
					if(formattedFigures.size()>0)
					{
						figureText = StringUtils.join(formattedFigures,", ");
					}
					row.createCell(10).setCellValue(figureText); // figure

					String genotypeText = "";
					if(r.getGenotype() !=null && !r.getGenotype().equals(""))
					{
						genotypeText = FormatHelper.newline2Comma(ntc.convertNotes(r.getGenotype(), '|',true,true));
					}
					row.createCell(11).setCellValue(genotypeText); // mutant alleles

					row.createCell(12).setCellValue(r.getJNum());

					row.createCell(13).setCellValue(r.getPubmedId()); //pub med id

					row.createCell(14).setCellValue(r.getShortCitation());
				}
			}

		}

	private  final static String getCurrentDate()   {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat( "yyyyMMdd_HHmmss" ) ;
		//df.setTimeZone( TimeZone.getTimeZone( "EST" )  ) ;
        String formattedDate = df.format(date);
        return (formattedDate);
	}

}
