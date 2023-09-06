package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.AnnotationInferredFromID;
import org.jax.mgi.fe.datamodel.AnnotationProperty;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.util.NotesTagConverter;

public class TextGoMarkerSummary extends AbstractTextView {

	private final NotesTagConverter ntc = new NotesTagConverter();

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String filename = "GO_marker_summary_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		

		writer.write("Aspect\tCategory\tQualifier\tClassification Term\tContext\tProteoform\tEvidence\tInferred From\tReference(s)\r\n");

		@SuppressWarnings("unchecked")
		List<Annotation> results = (List<Annotation>) model.get("results");

		StringBuffer inferred;
		StringBuffer refs;
		StringBuffer proteoforms;
		for (Annotation annot: results){

			// Vocab Info
			writer.write(annot.getDagName() + "\t");
			writer.write(annot.getHeaderAbbreviations() + "\t");
			
			// qualifier
			String qualifier = annot.getQualifier();
			if (qualifier == null) { qualifier = ""; }
			writer.write(qualifier + "\t");
			
			// term
			writer.write(annot.getTerm() + "\t");

			// Additional Term Context
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
			
			// Evidence
			writer.write(annot.getEvidenceCode() + "\t");

			// Inferred From
			inferred = new StringBuffer();
			for (AnnotationInferredFromID inf: annot.getInferredFromList()) {
				inferred.append(inf.getAccID() + " ");
			}
			writer.write(inferred.toString() + "\t");

			// References
			refs = new StringBuffer();
			for (Reference ref: annot.getReferences()) {
				refs.append(ref.getJnumID() + " ");
			}
			writer.write(refs.toString() + "\t");

			// close row
			writer.write("\r\n");
		}
		writer.write("\r\n");
	}
}
