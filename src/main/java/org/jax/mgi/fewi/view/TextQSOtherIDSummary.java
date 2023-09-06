package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.summary.QSOtherResultWrapper;
import org.jax.mgi.fewi.util.ParsedLocation;

public class TextQSOtherIDSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"MGI_OtherIDs.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<QSOtherResultWrapper> otherIDs = (List<QSOtherResultWrapper>) model.get("otherIDs");
		
		writer.write("Type\tSubtype\tPrimary ID\tName/Description\tBest Match Type\tBest Match\tMatch Score\r\n");
		
		Pattern nonZero = Pattern.compile("[1-9]");
		for (QSOtherResultWrapper otherID : otherIDs) {
			String primaryID = "";
			if (otherID.getPrimaryID() != null) {
				primaryID= otherID.getPrimaryID();
			}
			
			writer.write(otherID.getObjectType() + "\t");
			if (otherID.getObjectSubtype() != null) {
				writer.write(otherID.getObjectSubtype() + "\t");
			} else {
				writer.write("\t");
			}
			writer.write(primaryID + "\t");

			writer.write(otherID.getName() + "\t");
			writer.write(otherID.getBestMatchType() + "\t");
			writer.write(otherID.getBestMatchText() + "\t");
			writer.write(otherID.getStars().length() + "\r\n");
		}
	}
}
