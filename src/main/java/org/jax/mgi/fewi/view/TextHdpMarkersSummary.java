package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.util.FormatHelper;


public class TextHdpMarkersSummary extends AbstractTextView {

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition","attachment; filename=\"MGIhdpQuery_markers_" + getCurrentDate() + ".txt\"");

		List<SolrHdpMarker> geneList = (List<SolrHdpMarker>) model.get("geneList");
		
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
		for(int i = 0; i < headerTitles.length; i++) {
			writeIfNotNull(headerTitles[i]);
		}
		endLine();

		for(SolrHdpMarker m: geneList) {
			writeIfNotNull(m.getOrganism());
			writeIfNotNull(m.getSymbol());
			writeIfNotNull(m.getMgiId());
			writeIfNotNull(m.getLocation());
			writeIfNotNull(m.getCoordinate());
			writeIfNotNull(m.getCoordinateBuild());
			writeIfNotNull(FormatHelper.pipeDelimit(m.getDisease()));
			writeIfNotNull(FormatHelper.pipeDelimit(m.getMouseSystem()));
			endLine();
		}
	}

}
