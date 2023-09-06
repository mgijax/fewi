package org.jax.mgi.fewi.matrix;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * shared converter that identifies whether a String value should be
 * considered to be "detected" or "not detected"
 */
public class DetectionConverter {
	protected static final Set<String> detectedValues = new HashSet<>(Arrays.asList("yes", "Yes",
		"low", "Low", "medium", "Medium", "high", "High"));
	protected static final Set<String> notDetectedValues = new HashSet<>(Arrays.asList("no", "No",
		"below cutoff", "Below cutoff", "Below Cutoff"));

	public static boolean isDetected(String value) {
		return detectedValues.contains(value); 
	}

	public static boolean isNotDetected(String value) {
		return notDetectedValues.contains(value); 
	}
}
