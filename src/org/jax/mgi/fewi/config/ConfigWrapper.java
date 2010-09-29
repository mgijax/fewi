package org.jax.mgi.fewi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigWrapper {
	
	private @Value("${solr.reference.url}") String solr;
	
	public String getSolr() {
		return solr;
	}

	public String formatStr(String s){
		return "your arg: " + s; 
	}
}
