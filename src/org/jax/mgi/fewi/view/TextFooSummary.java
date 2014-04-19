package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Marker;

public class TextFooSummary extends AbstractTextView {

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Content-Disposition","attachment; filename=\"markerReport.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		List<Marker> markers = (List<Marker>) model.get("results");
		
		String pubmedId = "";
		
		for (Marker mrk : markers) {
			writer.write(mrk.getSymbol() + "\t");
			writer.write(mrk.getName()); 
			writer.write("\r\n");
		}

	}

}
