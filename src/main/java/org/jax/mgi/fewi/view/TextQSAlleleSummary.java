package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.summary.QSAlleleResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;

public class TextQSAlleleSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"MGI_Alleles.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<QSAlleleResultWrapper> alleles = (List<QSAlleleResultWrapper>) model.get("alleles");
		
		writer.write("Type\tMGI ID\tSymbol\tName\tChr\tStart\tEnd\tBuild\tStrand\tBest Match Type\tBest Match\tMatch Score\r\n");
		
		for (QSAlleleResultWrapper allele : alleles) {
			ParsedLocation location = new ParsedLocation(allele.getLocation());
			String primaryID = "";
			if (allele.getDetailUri() != null) {
				String[] pieces = allele.getDetailUri().split("/");
				if (pieces.length > 1) {
					primaryID = pieces[pieces.length - 1];
				}
			}
			
			writer.write(allele.getFeatureType() + "\t");
			writer.write(primaryID + "\t");

			writer.write(allele.getSymbol() + "\t");
			writer.write(allele.getName() + "\t");
			writer.write(allele.getChromosome() + "\t");

			writer.write(location.getStartCoord() + "\t");
			writer.write(location.getEndCoord() + "\t");
			writer.write(location.getBuild() + "\t");

			writer.write(allele.getStrand() + "\t");
			writer.write(allele.getBestMatchType() + "\t");
			writer.write(allele.getBestMatchText() + "\t");
                        writer.write(allele.getStars().length() + "\r\n");
		}
	}
}
