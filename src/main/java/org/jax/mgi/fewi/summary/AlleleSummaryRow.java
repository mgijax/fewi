package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.AlleleRelatedMarker;
import org.jax.mgi.fe.datamodel.AlleleSynonym;
import org.jax.mgi.fe.datamodel.VocabTermID;
import org.jax.mgi.fe.datamodel.phenotype.AlleleSummaryDisease;
import org.jax.mgi.fe.datamodel.phenotype.AlleleSummarySystem;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.html.DivCreator;
import org.jax.mgi.fewi.util.html.LinkCreator;
import org.jax.mgi.fewi.util.html.SpanCreator;
import org.jax.mgi.fewi.util.link.IDLinker;

public class AlleleSummaryRow {

	private static String NORMAL_PHENOTYPE="normal phenotype";
	private static String PHENOTYPE_NOT_ANALYZED="phenotype not analyzed";
	private static String CELL_LINE="Cell Line";
	private static String CHIMERIC="Chimeric";

	// encapsulated row object
	private final Allele allele;
	private IDLinker idLinker;

	String fewiUrl   = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	String webshareUrl = ContextLoader.getConfigBean().getProperty("WEBSHARE_URL");

	public AlleleSummaryRow (Allele allele, IDLinker idLinker) {
		this.allele = allele;
		this.idLinker = idLinker;
	}


	public String getNomen() {
		StringBuilder sb = new StringBuilder("");
		// allele symbol link
		sb.append("<a style=\"line-height: 1.8; \" href=\"").append(fewiUrl).append("allele/").append(allele.getPrimaryID()).append("\">");
		sb.append(FormatHelper.superscript(allele.getSymbol())).append("</a>");
		// gene + allele name
		sb.append("<br>").append(FormatHelper.superscript(allele.getGeneName())).append("; ").append(FormatHelper.superscript(allele.getName()));
		if(allele.hasPrimaryImage()) {
			sb.append("<br><a href=\"").append(fewiUrl).append("image/phenoSummary/allele/");
			sb.append(allele.getPrimaryID()).append("\">");
			sb.append("<img height=\"11\" width=\"15\" src=\"").append(webshareUrl).append("images/mgi_camera.gif\" />");
			sb.append("</a>");
		}
		return sb.toString();
	}
	
	public String getChr() {
		return allele.getChromosome();
	}
	
	public String getSynonyms() {
		List<String> synonyms = new ArrayList<String>();
		for(AlleleSynonym synonym : allele.getSynonyms()) {
			synonyms.add(FormatHelper.superscript(synonym.getSynonym()));
		}
		return "<span style=\"line-height: 1.4; \">"+StringUtils.join(synonyms,", ")+"</span>";
	}

	public String getCategory() {
		StringBuffer category = new StringBuffer();

		if (!"Not Applicable".equals(allele.getAlleleType())) {
			category.append("<span style=\"white-space: nowrap;\">");
			category.append(allele.getAlleleType());
			category.append("</span>");
		}

		if(allele.getAlleleSubType()!=null && !allele.getAlleleSubType().equals("")) {
			category.append("<br>(");
			category.append(allele.getAlleleSubType());
			category.append(")");
		}
		
		if(CELL_LINE.equalsIgnoreCase(allele.getTransmissionType())) {
			category.append("<br><b>(Cell Line)</b>");
		} else if(CHIMERIC.equalsIgnoreCase(allele.getTransmissionType())) {
			category.append("<br><b>(Chimeric)</b>");
		}

		if (allele.getCountOfMutationInvolvesMarkers() > 0) {

			List<AlleleRelatedMarker> mutationInvolves =
					allele.getMutationInvolvesMarkers();

			if (mutationInvolves.size() > 0) {
				category.append("<br>Involves ");
				category.append(
						allele.getCountOfMutationInvolvesMarkers());
				category.append(" genes (");

				boolean first = true;

				for (AlleleRelatedMarker mrk : mutationInvolves) {
					if (first) {
						first = false;
					} else {
						category.append (", ");
					}
					category.append ("<a href='");
					category.append (fewiUrl);
					category.append ("marker/");
					category.append (mrk.getRelatedMarkerID());
					category.append ("'>");
					category.append (mrk.getRelatedMarkerSymbol());
					category.append ("</a>");
				}
				
				if (allele.getCountOfMutationInvolvesMarkers() > 3) {
					category.append ("...");
				}
				
				category.append(") <a class='markerNoteButton'");
				category.append(" href='");
				category.append(fewiUrl);
				category.append("allele/mutationInvolves/");
				category.append(allele.getPrimaryID());
				category.append("'");

				category.append(" style='display:inline;");
				category.append(" line-height: 200%");
				category.append("'");

				category.append(" onClick='javascript:popupWide(\"");
				category.append(fewiUrl);
				category.append("allele/mutationInvolves/");
				category.append(allele.getPrimaryID());
				category.append("\", ");
				category.append(allele.getAlleleKey());
				category.append("); return false;'");

				category.append(">View&nbsp;all</a>");
			}
		}

		return category.toString();
	}

	public String getSystems(){
		List<String> systems = new ArrayList<String>();

		for(AlleleSummarySystem system : allele.getSummarySystems()) {
			if(!NORMAL_PHENOTYPE.equals(system.getSystem()) && !PHENOTYPE_NOT_ANALYZED.equals(system.getSystem()))  {
				systems.add(system.getSystem());
			}
		}
		return StringUtils.join(systems,", ");
	}
	
	public String getDiseases() {
		List<String> diseases = new ArrayList<String>();

		for(AlleleSummaryDisease disease : allele.getSummaryDiseases()) {
			//disease.getVocabTerm().
			LinkCreator makeUrl = new LinkCreator("", fewiUrl + "disease/" + disease.getDoID());
			makeUrl.setElementClass("MP");
			makeUrl.setTargetBlank(true);
			makeUrl.setText(disease.getDisease());
			
			String dialogId = disease.getDoID().replaceAll(":", "_") + "_" + allele.getAlleleKey();
			
			SpanCreator span = new SpanCreator("show_dialog(" + dialogId + ")");
			span.addStyle("color", "#000099");
			span.addStyle("cursor", "pointer");
			span.addStyle("text-decoration", "none");
			span.addStyle("font-size", "smaller");
			span.setElementClass("link");
			span.setOnMouseOver("setup_panel('" + dialogId + "');");
			span.setText("(IDs)");
			
			DivCreator div = new DivCreator("dialog(" + dialogId + ")");
			div.setElementClass("facetFilter; bottomBorder");
			div.addStyle("display", "none");
			
			DivCreator div_hd = new DivCreator();
			div_hd.setElementClass("hd");
			div_hd.setText(disease.getDisease() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			
			DivCreator div_bd = new DivCreator("");
			div_bd.setElementClass("bd");
			div_bd.addStyle("overflow", "auto");
			div_bd.addStyle("max-height", "150px");
			div_bd.addStyle("max-width", "750px");

			SpanCreator s = new SpanCreator();
			String spans = "";
			for(VocabTermID id: disease.getVocabTerm().getSecondaryIds()) {
				if(disease.getVocabTerm().getPrimaryId().equals(id.getAccID()) || (!id.getAccID().startsWith("DOID:") && !id.getAccID().startsWith("HP:"))) {
					s.setText(idLinker.getLinkWithClass(id, id.getAccID(), "MP"));
				} else {
					s.setText(id.getAccID());
				}
				spans += s.toString() + "<br>";
			}
			div_bd.setText(spans);
			div.setText(div_hd.toString() + div_bd.toString());
			
			diseases.add(makeUrl.toString() + "&nbsp;" + span.toString() + div.toString());
		}
		return StringUtils.join(diseases,"<br>");
	}
}
