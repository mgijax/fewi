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
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;

import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.ImagePane;
import mgi.frontend.datamodel.Reference;

public class TextGxdMarkersSummary extends AbstractTextView 
{
	
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String filename = "MGIgeneExpressionQuery_markers_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());
		
		GxdBatchFinder finder = (GxdBatchFinder) model.get("markerFinder");

		// set the batchSize
		int batchSize = 5000;
		finder.batchSize = batchSize;
		
		// write the headers
		String[] headerTitles = {"MGI Gene ID",
				"Gene Symbol", 
				"Gene Name", 
				"Type",
				"Chr",
				"Genome Location-NCBI Build 37",
				"cM",
				"Strand"};
		for(int i=0;i<headerTitles.length;i++)
		{
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		while(finder.hasNextMarkers())
		{
			SearchResults<SolrGxdMarker> markers = finder.getNextMarkers();
			for (SolrGxdMarker m : markers.getResultObjects()) 
			{	
				// use the Marker Summary row to get the correct Genome Location text
				GxdMarkerSummaryRow mr = new GxdMarkerSummaryRow(m);
				// for now we will steal logic from the summary row class
				writer.write(mr.getPrimaryID()+"\t");
				writer.write(m.getSymbol() + "\t");
				writer.write(mr.getName()+"\t");
				writer.write(mr.getType()+"\t");
				writer.write(mr.getChr() + "\t"); 
				writer.write(format(mr.getLocation())+ "\t"); 
				writer.write(format(mr.getCM()) + "\t"); 
				writer.write(format(mr.getStrand()) + "\t"); 
				
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
