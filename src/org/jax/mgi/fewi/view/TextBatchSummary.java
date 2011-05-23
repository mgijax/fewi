package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Marker;

public class TextBatchSummary extends AbstractTextView {

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Content-Disposition","attachment; filename=\"batchReport.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		List<BatchMarkerId> results = (List<BatchMarkerId>) model.get("results");
		
		Marker m;
		for (BatchMarkerId id : results) {
			m = id.getMarker();
			if (m != null){
				writer.write(m.getSymbol()); 
			} else {
				writer.write(id.getTerm()); 
			}

			writer.write("\n\r");
			//writer.newLine();
		}

	}

}
