package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.phenotype.AlleleSummaryDisease;
import mgi.frontend.datamodel.phenotype.AlleleSummarySystem;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;

public class TextMarkerSummary extends AbstractTextView
{
    // method to generate text document
    //-----------------------------------
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String filename = "MGImarkerQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());

		List<SolrSummaryMarker> markers = (List<SolrSummaryMarker>) model.get("markers");

		// write the headers
		String[] headerTitles = {
			"Genetic Chr",
			"cM",
			"Genomic Chr",
			"start",
			"end",
			"strand GRCm38",
			"MGI ID",
			"Feature Type",
			"Symbol",
			"Name"
		};

		for(int i=0;i<headerTitles.length;i++)
		{
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		// row generation
		for (SolrSummaryMarker marker : markers)
		{
            // write out this row to file writer
			writer.write("#geneticChr" + "\t");
			writer.write("#cM" + "\t");
			writer.write(marker.getChromosome() + "\t");
			writer.write(marker.getCoordStart() + "\t");
			writer.write(marker.getCoordEnd() + "\t");
			writer.write("#strand" + "\t");
			writer.write("#MGIID" + "\t");
			writer.write(marker.getFeatureType() + "\t");
			writer.write(marker.getSymbol() + "\t");
			writer.write(marker.getName() + "\t");

			
			writer.write("\r\n");
		}
	}

	private String format(String str)
	{
		if(str == null) return "";
		return str;
	}
}
