package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;

public class TextGoReferenceSummary extends AbstractTextView {
	
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Content-Disposition","attachment; filename=\"goReferenceReport.txt\"");

		writer.write("MGI Gene/Marker ID\tSymbol\tName\tChr\tQualifier\tAnnotated Term\tCategory\tEvidence\r\n");
		
		@SuppressWarnings("unchecked")
		List<Annotation> results = (List<Annotation>) model.get("results");
		
		Marker m;
		MarkerLocation ml;

		for (Annotation annot: results){
			m = annot.getMarkers().get(0);
			ml = m.getPreferredCentimorgans();
			if (ml == null) { ml = m.getPreferredLocation(); }

			writer.write(m.getPrimaryID() + "\t");
			writer.write(m.getSymbol() + "\t");
			writer.write(m.getName() + "\t");
			if (ml != null) {
			    writer.write(ml.getChromosome() + "\t");
			} else {
			    writer.write("Unknown\t");
			}

			if (annot.getQualifier() != null) {
			    writer.write(annot.getQualifier() + "\t");
			} else {
			    writer.write("\t");
			}
			writer.write(annot.getTerm() + "\t");
			writer.write(annot.getDagName() + "\t");
			writer.write(annot.getEvidenceCode() + "\t");
			writer.write("\r\n");
		}
		writer.write("\r\n");
	}
}
