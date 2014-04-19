package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;

public class TextGxdMarkersSummary extends AbstractTextView
{

	String assemblyBuild = ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION");
	
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
				"Genome Location-" + assemblyBuild,
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
}
