package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.AnnotationProperty;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.Reference;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.link.IDLinker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * wrapper around an Annotation;  represents on row in summary
 */
public class GOSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private Annotation annot;

	private final IDLinker  linker = IDLinker.getInstance();
	private final NotesTagConverter ntc = new NotesTagConverter();
	private final Logger logger = LoggerFactory.getLogger(GOSummaryRow.class);

	// config values
	String wiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	public GOSummaryRow (Annotation annot) {
		this.annot = annot;
		return;
	}

	public GOSummaryRow (Annotation annot, Marker marker) {
		this.annot = annot;
		return;
	}

	public GOSummaryRow (Annotation annot, Reference reference) {
		this.annot = annot;
		return;
	}

	//------------------------------------------------------------------------
	// public instance methods;  JSON serializer will call all public methods
	//------------------------------------------------------------------------

	public String getMarker() {
		List<Marker> markers = this.annot.getMarkers();
		if ((markers == null) || (markers.size() == 0)) {
			return "";
		}

		// assume only one marker, and that the marker is for mouse
		Marker m = markers.get(0);

		String t = "<a href='" + fewiUrl + "marker/" + m.getPrimaryID()
				+ "'>" + m.getSymbol() + "</a>, " + m.getName();
		return t;
	}

	public String getChromosome() {
		List<Marker> markers = this.annot.getMarkers();
		if ((markers == null) || (markers.size() == 0)) {
			return "";
		}

		// assume only one marker, and that the marker is for mouse
		Marker m = markers.get(0);

		// prefer a genetic location if one is available; if not, fall back on
		// whatever is available
		MarkerLocation ml = m.getPreferredCentimorgans();
		if (ml == null) {
			ml = m.getPreferredLocation();
		}
		if (ml.getChromosome() == null) {
			return "";
		}
		return ml.getChromosome();
	}

	public String getCategory() {
		return annot.getDagName();
	}

	public String getTerm() {
		String termText = "<a href='"+ fewiUrl +"vocab/gene_ontology/" + annot.getTermID() + "'> " + annot.getTerm() + "</a>";
		if (annot.getQualifier() != null) {
			return "<b>" + annot.getQualifier() + "</b> " + termText;
		}
		return termText;
	}

	/** return the String that should be sent to the browser for the
	 * evidence code column in the GO table.  This now includes a tooltip.
	 */
	public String getEvidence() {
		String ec = annot.getEvidenceCode();
		String et = annot.getEvidenceTerm();

		// if we don't have the abbreviation mapped, then skip the tooltip
		if (et == null) {
			return ec;
		}

		String tooltipTemplate = "<span onMouseOver='return overlib(\"<TERM>\", LEFT, WIDTH, <WIDTH>);' onMouseOut='nd();'><CODE></span>";

		int width = Math.min(200, 7 * et.length());

		return tooltipTemplate
				.replace("<CODE>", ec)
				.replace("<TERM>", et)
				.replace("<WIDTH>", "" + width);
	}

	public String getInferred() {
		List<AnnotationInferredFromID> inferred = annot.getInferredFromList();
		if (inferred.size() >= 1)
		{
			String tooltipTemplate = "<span onMouseOver='return overlib(\"<TOOLTIP>\", LEFT, WIDTH, <WIDTH>);' onMouseOut='nd();'><LINK></span>";

			String inferredString = "";
			Boolean first = Boolean.TRUE;

			for (AnnotationInferredFromID aifi: inferred) {
			    // use a comma for a separator if we're after the
			    // 1st ID for a comma-delimimed set
			    boolean useComma = false;

			    for (String myID : aifi.getAccID().split(",")) {
				String link = linker.getLink(aifi.getLogicalDB(), myID);
				String tooltip = aifi.getTooltip();
				if (tooltip != null) {
					int length = 1;
					for (String s : tooltip.split("\n")) {
						if (s.length() > length) {
							length = s.length();
						}
					}
					int pixels = 10 * length;
					if (length > 10) {
						pixels = (int) Math.round(6.5 * length);
					}
					tooltip = tooltip.replace("'", "&apos;"); // properly display apostrophe
					link = tooltipTemplate
							.replace("<LINK>", link)
							.replace("<WIDTH>", "" + pixels)
							.replace("<TOOLTIP>", tooltip
									.replace("<", "&&&&&")
									.replace(">", "</sup>")
									.replace("&&&&&", "<sup>")
									.replace("\n", "<br/>"));
				}
 
				if (first) {
					first = Boolean.FALSE;
					inferredString = link;
				}
				else if (!useComma) {
					inferredString = inferredString + " | " + link;
				} else {
					inferredString = inferredString + "," + link;
				}
				useComma = true;
			   } // walk through IDs, even comma-separated
			}

			return inferredString;
		}

		return "";
	}

	public String getReferences() {
		List<Reference> references = annot.getReferences();

		if (references == null || references.size() == 0) {
			return "";
		}

		String refString = "";
		Boolean first = Boolean.TRUE;

		// fill in pubmed ID with line break, citation, link to reference
		String tooltipTemplate = "<span onMouseOver='return overlib(\"<CITATION>\", LEFT, WIDTH, 200, CLOSECLICK, CLOSETEXT, \"Close X\");' onMouseOut='nd();'><LINK></span>";

		String jnumLink = "<a href='" + fewiUrl
				+ "reference/<JNUM>'><JNUM></a>";

		for (Reference ref: references) {
			String citation = ref.getMiniCitation();
			String jnum = jnumLink.replace("<CITATION>", citation)
					.replace("<JNUM>", ref.getJnumID());
			String mgiLink = tooltipTemplate.replace("<CITATION>", citation)
					.replace("<LINK>", jnum);

			if (first) {
				refString = refString + mgiLink;
				first = Boolean.FALSE;
			}
			else {
				refString = refString + ", " + mgiLink;
			}

			if (ref.getPubMedID() != null) {
				String pubmedLink = tooltipTemplate
						.replace("<CITATION>", citation)
						.replace("<LINK>",
								linker.getLink("MEDLINE", ref.getPubMedID(),
										"PMID:" + ref.getPubMedID()));

				refString = refString + "&nbsp;[" + pubmedLink + "]";
			}
		}
		return refString;
	}

	/*
	 * Get the annotation extensions
	 */
	public String getAnnotationExtensions() {
		List<AnnotationProperty> extensions = annot.getAnnotationExtensions();
		
		StringBuilder sb = new StringBuilder();
		if (extensions.size() > 0) {
			
			sb.append("<div class=\"goProperties\"><div class=\"stanza\">");

			Integer currentStanza = null;
			for (AnnotationProperty property: extensions) {
				
				if (currentStanza == null) {
					currentStanza = property.getStanza();
				}
				// Check if we have a new stanza
				else if (currentStanza != property.getStanza()) {
					
					// stanza separator
					sb.append("</div><hr><div class=\"stanza\">");
					currentStanza = property.getStanza();
				}
				
				// convert note tags in property value
				String propertyValue = ntc.convertNotes(property.getValue(), '|');
				
				sb.append("<span class=\"propertyWrap\">");
				
				sb.append("<span class=\"term\">")
					.append(property.getProperty())
					.append("</span> <span class=\"value\">")
					.append(propertyValue)
					.append("</span>");
				
				sb.append("</span>");
			}
	
			sb.append("</div></div>");
		}
		return sb.toString();
	}

	public String getIsoforms() {
		List<AnnotationProperty> isoforms = annot.getIsoforms();

		List<String> displayItems = new ArrayList<String>();
		for (AnnotationProperty property: isoforms) {
			String displayItem = property.getValue();
			displayItem = ntc.convertNotes(displayItem, '|');
			displayItems.add(displayItem);
		}

		return StringUtils.join(displayItems, "<br/>");
	}
	

	/* get the GO slimgrid headers that this annotation would roll up to,
	 * as a comma-separated string
	 */
	public String getHeaders() {
		return annot.getHeaderAbbreviations();
	}
}
