package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.DiseasePortalBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.util.FormatHelper;


public class TextHdpMarkersSummary extends AbstractTextView
{

	String assemblyBuild = ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION");

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String filename = "MGIhdpQuery_markers_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		System.out.println(response.getCharacterEncoding());


		DiseasePortalBatchFinder finder = (DiseasePortalBatchFinder) model.get("markerFinder");

		// set the batchSize
		int batchSize = 5000;
		finder.batchSize = batchSize;

		// write the headers
		String[] headerTitles = {
			    "Organism",
				"Gene Symbol",
				"ID",
				"Genetic Location",
				"Genome Coordinates",
				"Build",
				"Associated Human Diseases",
				"Abnormal Mouse Phenotypes"
		};
		for (int i=0;i<headerTitles.length;i++) {
			writer.write(headerTitles[i]+"\t");
		}
		writer.write("\r\n");

		while(finder.hasNextMarkers())
		{
			SearchResults<SolrDiseasePortalMarker> markers = finder.getNextMarkers();
			for(SolrDiseasePortalMarker m : markers.getResultObjects())
			{
				// gather and format data lists for this marker
				List<String> diseases = m.getDisease();
				List<String> systems = m.getSystem();
				String diseaseOut = new String();
				String systemsOut = new String();
				if (diseases != null) {
					diseaseOut = FormatHelper.pipeDelimit(diseases);
				}
				if (systems != null) {
					systemsOut = FormatHelper.pipeDelimit(systems);
				}

				// write out this row
				writer.write(m.getOrganism()+"\t");
				writer.write(m.getSymbol() + "\t");
				writer.write(m.getMgiId() + "\t");
				writer.write(m.getLocation() + "\t");
				writer.write(format(m.getCoordinate()) + "\t");
				writer.write(format(m.getCoordinateBuild()) + "\t");
				writer.write(diseaseOut + "\t");
				writer.write(systemsOut + "\t");
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
