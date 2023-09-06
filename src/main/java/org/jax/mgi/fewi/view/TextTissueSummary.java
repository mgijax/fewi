package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.MarkerTissueCount;

public class TextTissueSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"tissueReport.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<MarkerTissueCount> tissues = (List<MarkerTissueCount>) model.get("results");
		
		writer.write("Structure\tAll\tDetected\tNot Detected\r\n");
		
		for (MarkerTissueCount mtc : tissues) {
			
			writer.write("TS" + mtc.getStructure() + "\t");
			writer.write(mtc.getAllResultCount() + "\t");
			writer.write(mtc.getDetectedResultCount() + "\t");
			writer.write(mtc.getNotDetectedResultCount() + "\r\n");
			//writer.newLine();
		}

	}

}
