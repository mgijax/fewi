package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTextView extends AbstractReportView {

	// logger for the class
	private Logger logger = LoggerFactory.getLogger(AbstractTextView.class);
	private BufferedWriter writer = null;
	private static final String CONTENT_TYPE = "text/plain";

	AbstractTextView(){
		super();
		setContentType(CONTENT_TYPE);
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("renderMergedOutputModel");

		writer = new BufferedWriter(response.getWriter());

		// Build Text document.
		buildTextDocument(model, writer, request, response);

		// Flush to HTTP response.
		writer.flush(); 
		writer.close();
	}

	protected void writeIfNotNull(String in) throws IOException {
		if(in != null) {
			writer.write(in + "\t");
		} else {
			writer.write("\t");
		}
	}

	protected void writeIfNotNull(Integer i) throws IOException {
		writeIfNotNull(i.toString());
	}

	protected void endLine() throws IOException {
		writer.write("\r\n");
	}
	
	public void testRenderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception { }
	protected abstract void buildTextDocument(Map<String, Object> model, BufferedWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception;

}
