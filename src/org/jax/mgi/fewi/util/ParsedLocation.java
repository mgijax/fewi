package org.jax.mgi.fewi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.config.ContextLoader;

// Is: a helper for taking a string location from the Quick Search, breaking it into its component pieces,
//	and then making those parts accessible through getter methods.  Returns empty strings for missing
//	pieces.  Returns any non coords (e.g.- cytobands or syntenic) as first coordinate.
public class ParsedLocation {
	private static Pattern coords = Pattern.compile("([0-9]+)-([0-9]+)");	// start-end
	private static Pattern other = Pattern.compile("([^(]+)");				// syntenic or cytoband locations

	private String startCoord = "";
	private String endCoord = "";
	private String build = ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION");
	
	public ParsedLocation(String s) {
		if (s != null) {
			Matcher coordMatcher = coords.matcher(s.trim());
			if (coordMatcher.find()) {
				startCoord = coordMatcher.group(1);
				endCoord = coordMatcher.group(2);
				
			} else {
				Matcher otherMatcher = other.matcher(s.trim());
				if (otherMatcher.find()) {
					startCoord = otherMatcher.group(1);
				}
			}
		}
	}

	public String getStartCoord() {
		return startCoord;
	}
	public String getEndCoord() {
		return endCoord;
	}
	public String getBuild() {
		return build;
	}
}
