package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.summary.QSVocabResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;

public class TextQSVocabSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"MGI_VocabTerms.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<QSVocabResultWrapper> terms = (List<QSVocabResultWrapper>) model.get("terms");
		
		writer.write("Vocabulary\tTerm ID\tTerm\tAssociated Data\tBest Match Type\tBest Match\tMatch Score\r\n");
		
		Pattern nonZero = Pattern.compile("[1-9]");
		for (QSVocabResultWrapper term : terms) {
			String primaryID = "";
			if (term.getPrimaryID() != null) {
				primaryID= term.getPrimaryID();
			}
			
			String annotText = "";
			if (term.getAnnotationText() != null) {
				Matcher mat = nonZero.matcher(term.getAnnotationText());
				if (mat.find()) {
					annotText = term.getAnnotationText();
				}
			}
			writer.write(term.getVocabName() + "\t");
			writer.write(primaryID + "\t");

			writer.write(term.getTerm() + "\t");
			writer.write(annotText + "\t");

			writer.write(term.getBestMatchType() + "\t");
			writer.write(term.getBestMatchText() + "\t");
			writer.write(term.getStars().length() + "\r\n");
		}
	}
}
