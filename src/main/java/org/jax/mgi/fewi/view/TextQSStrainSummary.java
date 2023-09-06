package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.summary.QSStrainResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;
import org.jax.mgi.fewi.util.FormatHelper;

public class TextQSStrainSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"MGI_Strains.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<QSStrainResultWrapper> strains = (List<QSStrainResultWrapper>) model.get("strains");
		
		writer.write("Primary ID\tName\tReferences\tBest Match Type\tBest Match\tMatch Score\r\n");
		
		Pattern nonZero = Pattern.compile("[1-9]");
		for (QSStrainResultWrapper strain : strains) {
			String primaryID = "";
			if (strain.getPrimaryID() != null) {
				primaryID= strain.getPrimaryID();
			}
			
			String references = "";
			if (strain.getReferenceCount() != null) {
				if (strain.getReferenceCount() > 0) {
					references = strain.getReferenceCount() + FormatHelper.plural(strain.getReferenceCount(), " reference");
				}
			}
			writer.write(primaryID + "\t");
			writer.write(strain.getName() + "\t");
			writer.write(references + "\t");

			writer.write(strain.getBestMatchType() + "\t");
			writer.write(strain.getBestMatchText() + "\t");
			writer.write(strain.getStars().length() + "\r\n");
		}
	}
}
