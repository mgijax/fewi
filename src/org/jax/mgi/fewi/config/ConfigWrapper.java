package org.jax.mgi.fewi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigWrapper {
	
	private @Value("${SOLR_MARKER_URL}") String solr;
	private @Value("${foo.url}") String foo;
	
	public String getSolr() {
		return solr;
	}
	public String getFoo() {
		return foo;
	}

}
