package org.jax.mgi.fewi.util;

/* Is: a helper for computing various comparisons with search strings containing wildcards.
 * Assumes: All comparisons will be case-insensitive.
 */
public class WildcardHelper {
	// Determine whether the 'source' String begins with the given 'target' String,
	// considering properly any wildcards (*) in 'target'.
	public static boolean startsWith(String source, String target) {
		return indexOf(source, target) == 0;
	}
	
	// Determine whether the 'source' String contains (anywhere in it) the given
	// 'target' String, considering properly any wildcards (*) in 'target'.
	public static boolean contains(String source, String target) {
		return indexOf(source, target) >= 0;
	}
	
	// Determine at what position in 'source' the 'target' String appears, considering
	// properly any wildcards (*) in 'target'.  Returns -1 if it does not appear.
	public static int indexOf(String source, String target) {
		Pair pair = firstMatch(source, target);
		if (pair != null) {
			return pair.startPos;
		}
		return -1;
	}

	// Determine whether the 'source' String is entirely matched by the given
	// 'target' String, considering properly any wildcards (*) in 'target'.
	public static boolean matches(String source, String target) {
		Pair pair = firstMatch(source, target);
		if ((pair != null) && (pair.startPos == 0)) {
			return source.length() == (pair.endPos + 1);
		}
		return false;
	}
	
	private static Pair firstMatch(String source, String target) {
		// Six cases to consider...
		// 1. no wildcard in target (fall back on regular string operations)
		// 2. leading wildcard (*ab)
		// 3. trailing wildcard (ab*)
		// 4. inner wildcard (a*b)
		// 5. multiple inner wildcards (a*b*c)
		// 6. combination of 2-5 (*a*b*c*)

		String s = source.toLowerCase();
		String t = target.toLowerCase();
		
		// case 1 - no wildcard, so use String operations
		if (t.indexOf('*') == -1) {
			int startPos = s.indexOf(t);
			if (startPos < 0) {
				return null;
			} else {
				return new Pair(startPos, startPos + t.length() - 1);
			}
		}
		
		// To handle cases 2-6, we...
		// 1. Break our target string up on wildcards.
		// 2. Check that each of those pieces appears--in order--in the source string.
		String[] pieces = t.split("\\*");
		int overallStart = -1;
		int lastEnd = -1;
		int thisStart = -1;

		for (String piece : pieces) {
			// Ignore any empty pieces due to leading or trailing wildcards.
			if ((piece == null) || (piece.length() == 0)) { continue; }

			thisStart = s.indexOf(piece, Math.max(0, lastEnd));
			if (thisStart <= lastEnd) {
				return null;				// no instance of piece after the last one
			} else if (overallStart < 0) {
				overallStart = thisStart;
			}

			lastEnd = thisStart + piece.length() - 1;
		}
		
		// Need to adjust the start and end values for cases of leading or trailing wildcards.
		if (t.startsWith("*")) { overallStart = 0; }
		if (t.endsWith("*")) { lastEnd = s.length() - 1; }

		return new Pair(overallStart, lastEnd);
	}

	// Is: a (start position, end position) pair describing a match
	private static class Pair {
		int startPos;
		int endPos;
		
		public Pair(int startPos, int endPos) {
			this.startPos = startPos;
			this.endPos = endPos;
		}
	}
}
