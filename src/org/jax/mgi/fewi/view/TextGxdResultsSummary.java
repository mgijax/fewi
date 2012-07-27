package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;

import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.ImagePane;
import mgi.frontend.datamodel.Reference;

public class TextGxdResultsSummary extends AbstractTextView 
{
	
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String filename = "MGIgeneExpressionQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());
		
		GxdBatchFinder finder = (GxdBatchFinder) model.get("resultFinder");

		// set the batchSize
		int batchSize = 1000;
		finder.batchSize = batchSize;
		
		// write the headers
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
				"Figure(s)",
				"Mutant Allele(s)",
				"MGI Reference ID",
				"PubMed ID",
				"Citation"};
		for(int i=0;i<headerTitles.length;i++)
		{
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		// need this for processing genotypes
		NotesTagConverter ntc = new NotesTagConverter();
		while(finder.hasNextResults())
		{
			SearchResults<SolrAssayResult> results = finder.getNextResults();
			for (SolrAssayResult r : results.getResultObjects()) 
			{	
				// for now we will steal logic from the summary row class
				writer.write(r.getMarkerMgiid()+"\t");
				writer.write(r.getMarkerSymbol() + "\t");
				writer.write(r.getMarkerName()+"\t");
				writer.write(r.getAssayMgiid()+"\t");
				writer.write(r.getAssayType() + "\t"); 
				writer.write(r.getAnatomicalSystem() + "\t"); 
				writer.write(r.getAge() + "\t"); 
				writer.write(r.getTheilerStage() + "\t"); 
				writer.write(r.getPrintname() + "\t"); 
				writer.write(format(r.getDetectionLevel()) + "\t"); 
				
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
				writer.write(figureText+"\t");
				//genotype
				String genotypeText = "";
				if(r.getGenotype() !=null && !r.getGenotype().equals(""))
				{
					genotypeText = FormatHelper.newline2Comma(ntc.convertNotes(r.getGenotype(), '|',true,true));
				}
				writer.write(genotypeText+"\t");
				writer.write(r.getJNum() + "\t"); 
				writer.write(format(r.getPubmedId()) + "\t"); //pub med id
				writer.write(r.getShortCitation());
				
				writer.write("\r\n");
			}
		}
	}
	private String format(String str)
	{
		if(str == null) return "";
		return str;
	}
	
	private  final static String getCurrentDate()   {  
        DateFormat df = new SimpleDateFormat( "yyyyMMdd_kkmmss" ) ;  
        df.setTimeZone( TimeZone.getTimeZone( "EST" )  ) ;  
        return ( df.format( new Date(  )  )  ) ;  
	}
}
