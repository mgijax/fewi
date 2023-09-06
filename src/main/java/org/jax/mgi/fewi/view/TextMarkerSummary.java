package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		// retrieve list of markers from controller
		@SuppressWarnings("unchecked")
		List<SolrSummaryMarker> markers = (List<SolrSummaryMarker>) model.get("markers");

		// setup
		String filename = "MGImarkerQuery_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");

		// only display "why" it matched when nomen param isn't empty
		boolean displayMatches = false;
		if (request.getParameter("nomen")!=null && !request.getParameter("nomen").equals("")){
			displayMatches = true;
		}

		// write the headers
		String[] headerTitles = {
			"Chromosome",
			"Start",
			"End",
			"cM",
			"strand GRCm39",
			"MGI ID",
			"Feature Type",
			"Symbol",
			"Name",
			""
		};
		if (displayMatches == true) {
			headerTitles[9] = "Matching Text";
		}
		for(int i=0;i<headerTitles.length;i++)
		{
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		// row generation
		for (SolrSummaryMarker marker : markers)
		{
            // write out this row to file writer
			writer.write(marker.getChromosome() + "\t");
			writer.write(marker.getCoordStartStr() + "\t");
			writer.write(marker.getCoordEndStr() + "\t");
			writer.write(marker.getCmStr() + "\t");
			writer.write(marker.getStrand() + "\t");
			writer.write(marker.getMgiId() + "\t");
			writer.write(marker.getFeatureType() + "\t");
			writer.write(marker.getSymbol() + "\t");
			writer.write(marker.getName() + "\t");
			if (displayMatches == true) {
				writer.write(StringUtils.join(marker.getHighlights(),", ") + "\t");
			}
			writer.write("\r\n");
		}
	}
}
