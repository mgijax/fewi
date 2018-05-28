package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.shr.jsonmodel.AccessionID;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;

public class TextStrainSummary extends AbstractTextView {
    
	// return the contents of 'items' as a single, pipe-delimited String
	private String pipeDelimited(List<String> items) {
		if ((items == null) || (items.size() == 0)) {
			return "";
		}
		return StringUtils.join(items, "|");
	}
	
	// return a list of just the ID strings for the IDs of the given 'strain'
	private List<String> getIDs (SimpleStrain strain) {
		List<String> ids = new ArrayList<String>();
		for (AccessionID id : strain.getAccessionIDs()) {
			String displayID = id.getAccID();
			try {
				Integer i = Integer.parseInt(displayID);
				if (id.getLogicalDB().equals("JAX Registry")) {
					ids.add("JAX:" + displayID);
				} else {
					ids.add(id.getLogicalDB() + ":" + displayID);
				}
			} catch (Exception e) {
				// ID has non-numeric characters, so trust it has a prefix
				ids.add(displayID);
			}
		}
		return ids;
	}
	
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"MGIstrains.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<SimpleStrain> strains = (List<SimpleStrain>) model.get("strains");
		
		writer.write("Official Strain Name\tMGI ID\tSynonyms\tAttributes\tIDs\tReferences\r\n");
		
		for (SimpleStrain strain : strains) {
			
			writer.write(strain.getName() + "\t");
			writer.write(strain.getPrimaryID() + "\t");
			writer.write(pipeDelimited(strain.getSynonyms()) + "\t");
			writer.write(pipeDelimited(strain.getAttributes()) + "\t");
			writer.write(pipeDelimited(getIDs(strain)) + "\t");
			writer.write(strain.getReferenceCount() + "\r\n");
		}
	}
}
