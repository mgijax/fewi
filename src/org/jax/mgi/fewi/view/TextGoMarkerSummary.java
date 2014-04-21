package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.Reference;

public class TextGoMarkerSummary extends AbstractTextView {

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Content-Disposition","attachment; filename=\"goMarkerReport.txt\"");

		writer.write("Category\tClassification Term\tEvidence\tInferred From\tReference(s)\r\n");
		
		@SuppressWarnings("unchecked")
		List<Annotation> results = (List<Annotation>) model.get("results");		
		
		StringBuffer inferred;
		StringBuffer refs;
		for (Annotation annot: results){
			writer.write(annot.getDagName() + "\t");
			writer.write(annot.getTerm() + "\t");
			writer.write(annot.getEvidenceCode() + "\t");
			
			inferred = new StringBuffer();
			for (AnnotationInferredFromID inf: annot.getInferredFromList()) {
				inferred.append(inf.getAccID() + " ");
			}
			writer.write(inferred.toString() + "\t");

			refs = new StringBuffer();
			for (Reference ref: annot.getReferences()) {
				refs.append(ref.getJnumID() + " ");
			}
			writer.write(refs.toString() + "\t");
			writer.write("\r\n");

		}

		

		writer.write("\r\n");
	}
}
