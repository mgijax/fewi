package org.jax.mgi.fewi.view;

import java.util.*;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.DiseasePortalBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.util.FormatHelper;


public class TextHdpDiseaseSummary extends AbstractTextView
{

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String filename = "MGIhdpQuery_disease_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());

		DiseasePortalBatchFinder finder = (DiseasePortalBatchFinder) model.get("diseaseFinder");

		// set the batchSize
		int batchSize = 5000;
		finder.batchSize = batchSize;

		// write the headers
		String[] headerTitles = {
			    "Disease",
				"OMIM Id",
				"Mouse Models",
				"Associated Mouse Markers",
				"Associated Human Markers"
		};
		for (int i=0;i<headerTitles.length;i++) {
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");


		while(finder.hasNextDiseases())
		{
			SearchResults<SolrVocTerm> diseases = finder.getNextDiseases();
			for(SolrVocTerm d : diseases.getResultObjects())
			{
				// gather and format data lists for this disease
				List<String> mouseMarkers = d.getDiseaseMouseMarkers();
				List<String> humanMarkers = d.getDiseaseHumanMarkers();
				String mouseMarkersOut = new String();
				String humanMarkersOut = new String();
				if (mouseMarkers != null) {
					mouseMarkersOut = FormatHelper.pipeDelimit(mouseMarkers);
				}
				if (humanMarkers != null) {
					humanMarkersOut = FormatHelper.pipeDelimit(humanMarkers);
				}
	
				// write out this row 
				writer.write(d.getTerm()+"\t");
				writer.write(d.getPrimaryId() + "\t");
				writer.write(format(d.getDiseaseModelCount()) + "\t");
				writer.write(mouseMarkersOut + "\t");
				writer.write(humanMarkersOut + "\t");
	
				writer.write("\r\n");
			}
		}
	}

	private String format(Integer i)
	{
		if(i == null) return "";
		return i.toString();
	}
	private String format(String str)
	{
		if(str == null) return "";
		return str;
	}
}
