package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.summary.QSFeatureResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;

public class TextQSFeatureSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"MGI_Features.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<QSFeatureResultWrapper> features = (List<QSFeatureResultWrapper>) model.get("features");
		
		writer.write("Type\tMGI ID\tSymbol\tName\tChr\tStart\tEnd\tBuild\tStrand\tBest Match Type\tBest Match\tMatch Score\r\n");
		
		for (QSFeatureResultWrapper feature : features) {
			ParsedLocation location = new ParsedLocation(feature.getLocation());
			String primaryID = "";
			if (feature.getDetailUri() != null) {
				String[] pieces = feature.getDetailUri().split("/");
				if (pieces.length > 1) {
					primaryID = pieces[pieces.length - 1];
				}
			}
			
			writer.write(feature.getFeatureType() + "\t");
			writer.write(primaryID + "\t");

			writer.write(feature.getSymbol() + "\t");
			writer.write(feature.getName() + "\t");
			writer.write(feature.getChromosome() + "\t");

			writer.write(location.getStartCoord() + "\t");
			writer.write(location.getEndCoord() + "\t");
			writer.write(location.getBuild() + "\t");

			writer.write(feature.getStrand() + "\t");
			writer.write(feature.getBestMatchType() + "\t");
			writer.write(feature.getBestMatchText() + "\t");
                        writer.write(feature.getStars().length() + "\r\n");
		}
	}
}
