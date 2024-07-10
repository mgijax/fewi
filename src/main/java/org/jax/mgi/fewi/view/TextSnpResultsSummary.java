package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.finder.SnpBatchFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.ConsensusSNPSummaryRow;
import org.jax.mgi.snpdatamodel.ConsensusAlleleSNP;
import org.jax.mgi.snpdatamodel.ConsensusCoordinateSNP;
import org.jax.mgi.snpdatamodel.ConsensusMarkerSNP;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextSnpResultsSummary extends AbstractTextView {
	private static String TAB = "\t"; // tab character
	private static String NL = "\r\n"; // newline character(s)

	private final Logger logger = LoggerFactory.getLogger(TextSnpResultsSummary.class);

	// format the input parameters into a single line of output, including
	// the trailing newline
	private String buildLine(String snpID, String chromosome, long coordinate, String markerID, String symbol, String category, String variationClass, String alleleSummary, List<String> strains, List<ConsensusAlleleSNP> alleles) {

		StringBuffer sb = new StringBuffer();

		sb.append(snpID);
		sb.append(TAB);
		sb.append(chromosome);
		sb.append(TAB);
		sb.append(coordinate);
		sb.append(TAB);
		sb.append(markerID);
		sb.append(TAB);
		sb.append(symbol);
		sb.append(TAB);
		sb.append(category);
		sb.append(TAB);
		sb.append(variationClass);
		sb.append(TAB);
		sb.append(alleleSummary);
		sb.append(TAB);

		for (String strain : strains) {
			String call = ""; // the call for this strain

			for (ConsensusAlleleSNP a : alleles) {
				if (strain.equals(a.getStrain())) {
					String allele = a.getAllele();

					if (a.isConflict()) {
						allele = allele + " <conflict>";
					}
					call = allele;
					break;
				}
			} // allele loop

			sb.append(call);
			sb.append(TAB);
		} // strain loop

		sb.append(NL);
		return sb.toString();
	}

	/*
	 * get the list of strains to display. If 'displayStrains' is provided then use
	 * that (should always be the case); otherwise use the 'finder' to look up the
	 * strains based on facets for the query.
	 */
	private List<String> getStrains(String displayStrains, SnpBatchFinder finder) {
		List<String> strains = null;

		if (displayStrains != null) {
			strains = Arrays.asList(displayStrains.split(","));
		}

		if ((strains == null) || (strains.size() == 0)) {
			finder.setBatchSize(0);
			SearchResults<ConsensusSNPSummaryRow> sr = finder.getNextResults();
			strains = sr.getResultFacets();
		}
		logger.debug("got " + strains.size() + " strains");
		return strains;
	}

	/*
	 * build and return the header line for the output file, as a String
	 */
	private String buildHeaderLine(List<String> strains, String buildNumber, String assemblyVersion, String refStrain) {
		StringBuffer sb = new StringBuffer(); // for output

		// if we have a reference strain, we need to flag it (assume
		// it is the first strain in 'strains')

		boolean flagReferenceStrain = false;
		if ((refStrain != null) && (refStrain.length() > 0)) {
			flagReferenceStrain = true;
		}

		// write the headers
		String[] initialHeaderTitles = { "SNP ID (" + buildNumber + ")", "Chromosome", "Coordinate (" + assemblyVersion + ")", "MGI Gene ID", "Gene Symbol", "Category", "Variation Type", "Allele Summary (all strains)" };

		for (String title : initialHeaderTitles) {
			sb.append(title);
			sb.append(TAB);
		}

		for (String strain : strains) {
			if (flagReferenceStrain) {
				sb.append(strain.replace(" ", "+"));
				sb.append(" (reference strain)");
				sb.append(TAB);
				flagReferenceStrain = false;
			} else {
				sb.append(strain.replace(" ", "+"));
				sb.append(TAB);
			}
		}
		sb.append(NL);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.debug("In TextSnpResultsSummary.buildTextDocument()");

		String filename = "MGI_SNP_Query_" + getCurrentDate();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".txt\"");
		System.out.println(response.getCharacterEncoding());

		// get our SNP data in batches, to avoid blowing out memory
		SnpBatchFinder finder = (SnpBatchFinder) model.get("resultFinder");

		// get list of strains to display, in order left to right
		List<String> strains = getStrains(request.getParameter("displayStrains"), finder);

		// set the batchSize to a reasonable amount
		finder.setBatchSize(1000);

		writer.write(buildHeaderLine(strains, (String) model.get("buildNumber"), (String) model.get("assemblyVersion"), request.getParameter("referenceStrain")));

		// limit to distance-based marker associations shown for
		// nomenclature search
		int withinRange = 2000;
		try {
			withinRange = Integer.parseInt((String) model.get("withinRange"));
		} catch (NumberFormatException e) {
		}
		;

		// is this file the result of a nomenclature search?
		boolean nomenSearch = false;
		List<String> matchedMarkerIds = (List<String>) model.get("matchedMarkerIds");
		if ((matchedMarkerIds != null) && (matchedMarkerIds.size() > 0)) {
			nomenSearch = true;
		}

		// convert the list of matched marker IDs to a HashSet for
		// better performance
		HashSet<String> markerMatches = new HashSet<String>();
		if (nomenSearch) {
			for (String markerID : matchedMarkerIds) {
				markerMatches.add(markerID);
			}
		}

		// main loop -- keep going until we find no more batches
		// of results
		while (finder.hasNextResults()) {

			// get the next batch of results
			SearchResults<ConsensusSNPSummaryRow> results = finder.getNextResults();

			// each SNP can have multiple locations, which would
			// give rise to multiple rows (hence, nested loop)

			for (ConsensusSNPSummaryRow r : results.getResultObjects()) {
				ConsensusSNP snp = r.getConsensusSNP();

				// and each coordinate can be associated with
				// multiple markers, which gives rise to yet
				// another multiplier of rows (and another
				// nested loop)

				for (ConsensusCoordinateSNP c : snp.getConsensusCoordinates()) {
					List<ConsensusMarkerSNP> markers = c.getMarkers();

					/*
					 * rules for display of marker associations: 1. do not show duplicate
					 * marker/function class pairs for a given SNP 2a. (if nomen search) only
					 * include distance- based associations for markers which match the nomenclature
					 * search and are within the region setting on the query form 2b. (if nomen
					 * search) also include any 'within coordinates' associations 2c. (if region
					 * search) include only "within coordinates" for distance-based associations 3.
					 * suppress display of marker associations for Contig_Reference function class
					 * 4. if the function class is "Locus-Region" then we need to append its
					 * distance-direction
					 */

					// did we write this SNP/coord out to the file yet?
					boolean wroteSNP = false;

					if ((markers != null) && (markers.size() > 0)) {

						// "marker ID, function class" pairs for this
						// SNP -- to prevent writing duplicates
						HashSet<String> pairs = new HashSet<String>();

						for (ConsensusMarkerSNP m : markers) {
							String fc = m.getFunctionClass();

							// rule 3, above
							if ("Contig-Reference".equals(fc)) {
								continue;
							}

							boolean isDistanceAssoc = false;

							if ("within distance of".equals(fc)) {
								isDistanceAssoc = true;
							}

							String id = m.getAccid(); // marker ID

							// rule 1, above
							String pair = id + ", " + fc;
							if (pairs.contains(pair)) {
								continue;
							}
							pairs.add(pair); // remember this one

							// rule 2a, above
							int snpDistance = m.getDistanceFrom();
							if (nomenSearch && isDistanceAssoc && ((snpDistance > withinRange) || !markerMatches.contains(id))) {
								continue;
							}
							// rule 2b just falls through appropriately

							// rule 2c, above
							if (!nomenSearch && isDistanceAssoc) {
								continue;
							}

							// adjust function class to show distances
							if (isDistanceAssoc) {
								fc = m.getDistanceFrom() + " bp " + m.getDistanceDirection();
							}

							// rule 4, above
							if ("Locus-Region".equals(fc)) {
								fc = fc + " " + m.getDistanceDirection();
							}

							writer.write(buildLine(snp.getAccid(), c.getChromosome(), c.getStartCoordinate(), id, m.getSymbol(), fc, c.getVariationClass(), c.getAlleleSummary(), strains, snp.getConsensusAlleles()));

							wroteSNP = true;
						} // marker loop
					}

					/*
					 * either: 1. SNP has no associated markers, or 2. SNP had only markers
					 * associated via the Contig_Reference function class, or 3. SNP had no markers
					 * matching the nomen search within the specified bp range. So, show the SNP
					 * without associated markers.
					 */
					if (!wroteSNP) {
						writer.write(buildLine(snp.getAccid(), c.getChromosome(), c.getStartCoordinate(), "", "", "", c.getVariationClass(), c.getAlleleSummary(), strains, snp.getConsensusAlleles()));
					}
				} // coordinate loop
			} // consensus SNP loop
		} // "while more results" loop

		logger.debug("wrote rows, finishing");
	}
}
