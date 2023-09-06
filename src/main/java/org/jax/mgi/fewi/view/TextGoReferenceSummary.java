package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.AnnotationProperty;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerLocation;
import org.jax.mgi.fewi.util.NotesTagConverter;

public class TextGoReferenceSummary extends AbstractTextView {

	private final NotesTagConverter ntc = new NotesTagConverter();

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		
		String filename = "GO_reference_summary_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		
		writer.write("MGI Gene/Marker ID\tSymbol\tName\tChr\tQualifier\tAnnotated Term\tContext\tProteoform\tAspect\tEvidence\tInferred From\r\n");

		@SuppressWarnings("unchecked")
		List<Annotation> results = (List<Annotation>) model.get("results");

		Marker m;
		MarkerLocation ml;
		StringBuffer proteoforms;

		for (Annotation annot: results){
			m = annot.getMarkers().get(0);
			ml = m.getPreferredCentimorgans();
			if (ml == null) { ml = m.getPreferredLocation(); }

			// Marker Info
			writer.write(m.getPrimaryID() + "\t");
			writer.write(m.getSymbol() + "\t");
			writer.write(m.getName() + "\t");

			// CHR location
			if (ml != null) {
			    writer.write(ml.getChromosome() + "\t");
			} else {
			    writer.write("Unknown\t");
			}

			// Qualifier
			if (annot.getQualifier() != null) {
			    writer.write(annot.getQualifier() + "\t");
			} else {
			    writer.write("\t");
			}
			// Term
			writer.write(annot.getTerm() + "\t");

			// context
			String annotExtensions = ntc.convertNotes(annot.getAnnotationExtensionTextOutput(),'|',true);
			writer.write(annotExtensions);
			writer.write("\t");
			
			// Proteoform
			proteoforms = new StringBuffer();
			for (AnnotationProperty prop: annot.getIsoforms()) {
				String displayItem = prop.getValue();
				displayItem = ntc.convertNotes(displayItem, '|', true);
				proteoforms.append(displayItem);
			}
			writer.write(proteoforms.toString() + "\t");

			// aspect
			writer.write(annot.getDagName() + "\t");

			// Evidence & Inferred From
			writer.write(annot.getEvidenceCode() + "\t");
			writer.write(annot.getInferredFrom() + "\t");

			// close the row
			writer.write("\r\n");
		}
		writer.write("\r\n");
	}
}
