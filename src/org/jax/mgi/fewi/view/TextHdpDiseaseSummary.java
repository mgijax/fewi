package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.util.FormatHelper;


public class TextHdpDiseaseSummary extends AbstractTextView {

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition","attachment; filename=\"MGIhdpQuery_disease_" + getCurrentDate() + ".txt\"");

		List<SolrHdpDisease> diseaseList = (List<SolrHdpDisease>) model.get("diseaseList");

		// write the headers
		String[] headerTitles = {
			    "Disease",
				"DO Id",
				"OMIM Id",
				"Mouse Models",
				"Associated Mouse Genes",
				"Associated Human Genes"
		};
		for (int i = 0; i < headerTitles.length; i++) {
			writeIfNotNull(headerTitles[i]);
		}
		endLine();

		for(SolrHdpDisease d : diseaseList) {
			
			writeIfNotNull(d.getTerm());
			writeIfNotNull(d.getPrimaryId());
			writeIfNotNull(FormatHelper.pipeDelimit(d.getOmimIds()));
			writeIfNotNull(d.getDiseaseModelCount());
			writeIfNotNull(FormatHelper.pipeDelimit(d.getDiseaseMouseMarkers()));
			writeIfNotNull(FormatHelper.pipeDelimit(d.getDiseaseHumanMarkers()));
			endLine();
		}

	}

}
