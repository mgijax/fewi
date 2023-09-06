package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Statistic;

public class TextStatisticSummary extends AbstractTextView {
    
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	    
		response.setHeader("Content-Disposition","attachment; filename=\"statistics.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		@SuppressWarnings("unchecked")
		List<Statistic> statistics = (List<Statistic>) model.get("statistics");
		String date = (String) model.get("databaseDate");
		
		writer.write("MGI Data Statistics as of " + date + "\r\n");
		writer.write("Count\tDescription\tSection\r\n");
		
		for (Statistic stat : statistics) {
			
			writer.write(stat.getCommaDelimitedValue() + "\t");
			writer.write(stat.getName() + "\t");
			writer.write(stat.getGroupName().replace("Stats Page ", "") + "\r\n");
		}

	}
}
