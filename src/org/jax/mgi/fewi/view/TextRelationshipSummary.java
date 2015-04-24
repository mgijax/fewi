package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;

public class TextRelationshipSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(
			Map<String, Object> model,
			BufferedWriter writer,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"relationshipReport.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<SolrInteraction> relationships = (List<SolrInteraction>) model.get("results");
		
		writer.write("OrganizerID\tOrganizerSymbol\tRelationshipTerm\tParticipantID\tParticipantSymbol\tEvidenceCode\tScore\tScoreSource\tValidation\tReferenceId\tNotes\r\n");

		for (SolrInteraction r: relationships) {
			
			writer.write(r.getOrganizerID() + "\t");
			writer.write(r.getOrganizerSymbol() + "\t");
			writer.write(r.getRelationshipTerm() + "\t");
			writer.write(r.getParticipantID() + "\t");
			writer.write(r.getParticipantSymbol() + "\t");
			writer.write(r.getEvidenceCode() + "\t");
			writer.write(r.getScore() + "\t");
			writer.write(r.getScoreSource() + "\t");
			writer.write(r.getValidation() + "\t");
			writer.write(r.getJnumID() + "\t");
			
			writer.write(r.getParticipantProductID() + "\t");
			writer.write(r.getOrganizerProductID() + "\t");
			writer.write(r.getAlgorithm() + "\t");
			writer.write(r.getOtherReferences() + "\t");
			
			writer.write(r.getNotes() + "\r\n");
			//writer.newLine();
		}

	}

}
