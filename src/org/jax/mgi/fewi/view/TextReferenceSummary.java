package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Reference;

public class TextReferenceSummary extends AbstractTextView {

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Content-Disposition","attachment; filename=\"referenceReport.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<Reference> references = (List<Reference>) model.get("results");
		
		String pubmedId = "";
		
		for (Reference ref : references) {
                        pubmedId = "";
			if (ref.getPubMedID() != null){
				pubmedId = ref.getPubMedID();
			}
			
			writer.write(pubmedId + "\t");
			writer.write(ref.getLongCitation()); 
			writer.write("\r\n");
			//writer.newLine();
		}

	}

}
