package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.BatchMarkerAllele;
import org.jax.mgi.fe.datamodel.BatchMarkerGoAnnotation;
import org.jax.mgi.fe.datamodel.BatchMarkerId;
import org.jax.mgi.fe.datamodel.BatchMarkerMpAnnotation;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.BatchMarkerIdWrapper;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FormatHelper;


/**
 * wrapper around a Marker;  represents on row in summary
 */
public class BatchSummaryRow {
	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private BatchMarkerIdWrapper bmi;

	private BatchQueryForm query = null;

	// config values
    private final String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    private static String urlPattern = "<a href='%s' target='_blank'>%s</a>";
    private static String noWrap = "<span style='white-space:nowrap;'>%s</span>";

	//-------------
	// constructors
	//-------------

    public BatchSummaryRow (BatchMarkerId batchMarkerId, BatchQueryForm query) {
    	this.bmi = new BatchMarkerIdWrapper(batchMarkerId);
    	this.query = query;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    // get the matching term
    public String getTerm() {
    	return bmi.getTerm();
    }
    
    // get the type of the matching term
    public String getType() {
    	return bmi.getTermType();
    }
    
    // get the value of the marker ID column.  This will be linked, if we're not showing the nomen columns.
    public String getMarkerId() {

    	// if we're showing the nomen fields, then this one is just simply an ID
    	if ((query != null) && query.getNomenclature()) {
    		if ((bmi.getMarkerID() != null) && (bmi.getMarkerID().length() > 0)) {
    			return bmi.getMarkerID();
    		}
    	}

    	// This was an ID search and we have a match to a marker, so link the ID.
    	if (bmi.getMarkerID() != null) {
       		return String.format(urlPattern, fewiUrl + "marker/" + bmi.getMarkerID(), bmi.getMarkerID());
    	}
    	
    	return "No associated gene";
    }

    // Get the value for the symbol column. Symbol is linked appropriately if the query included nomen display.
    public String getSymbol() {
    	if ((bmi.getMarkerID() != null) && (bmi.getSymbol() != null)) {
    		return String.format(urlPattern, fewiUrl + "marker/" + bmi.getMarkerID(), bmi.getSymbol());
    	}
    	return "";
    }

    // Get the value for the name column.
    public String getName() {
    	return bmi.getName();
    }

    // Get the value for the feature type column.
    public String getFeature() {
    	return bmi.getFeatureType();
    }

    // Get the value for the chromosome column.
    public String getChromosome() {
    	return bmi.getChromosome();
    }

    // Get the value for the strand column.
    public String getStrand() {
    	return bmi.getStrand();
    }

    // Get the value for the start coordinate column.
    public Double getStart() {
    	return bmi.getStart();
    }

    // Get the value for the end coordinate column.
    public Double getEnd() {
    	return bmi.getEnd();
    }

    // Get a string containing the matching marker's Ensembl IDs (if requested), separated by HTML line breaks.
    public String getEnsemblIds() {
    	if (!query.getEnsembl()) return "";
    	return StringUtils.join(bmi.getIDs(DBConstants.PROVIDER_ENSEMBL), "<br/>");
    }

    // Get a string containing the matching marker's Entrez Gene IDs (if requested), separated by HTML line breaks.
    public String getEntrezIds() {
    	if (!query.getEntrez()) return "";
    	return StringUtils.join(bmi.getIDs(DBConstants.PROVIDER_ENTREZGENE), "<br/>");
    }

    // Get a string containing the matching marker's associated GO IDs, terms, or evidence codes (if requested), 
    // separated by HTML line breaks.
    private String getGoField(String field){
    	if (!query.getGo()){ return ""; }
    	List<String> go = new ArrayList<String>();
    	for (BatchMarkerGoAnnotation annotation : bmi.getGOAnnotations()) {
    		if ("ID".equals(field)) {
    			go.add(String.format(noWrap, annotation.getGoId()));
    		} else if ("term".equals(field)) {
    			go.add(String.format(noWrap, annotation.getGoTerm()));
    		} else if ("code".equals(field)) {
    			go.add(String.format(noWrap, annotation.getEvidenceCode()));
    		}
		}
    	return StringUtils.join(go, "<br/>");
    }

    // Get a string containing the matching marker's associated GO IDs (if requested),
    // separated by HTML line breaks.
    public String getGoIds(){
    	return getGoField("ID");
    }

    // Get a string containing the matching marker's associated GO terms (if requested),
    // separated by HTML line breaks.
    public String getGoTerms(){
    	return getGoField("term");
    }

    // Get a string containing the matching marker's associated GO evidence codes (if requested),
    // separated by HTML line breaks.
    public String getGoCodes(){
    	return getGoField("code");
    }

    // Get a string containing the matching marker's associated MP IDs or terms (if requested), 
    // separated by HTML line breaks.
    private String getMpField(String field){
    	if (!query.getMp()){ return ""; }
    	List<String> mp = new ArrayList<String>();
    	String text, url;
    	for (BatchMarkerMpAnnotation annotation : bmi.getMPAnnotations()) {
    		if ("ID".equals(field)) {
    			mp.add(String.format(noWrap, annotation.getMpId()));
    		} else if ("term".equals(field)) {
    			text = annotation.getMpTerm() + " (%s)";
    			url = fewiUrl + String.format("mp/annotations/%s?markerID=%s", annotation.getMpId(), bmi.getMarkerID() );
    			mp.add(String.format(noWrap, String.format(text, String.format(urlPattern, url, "details"))));
    		}
		}
    	return StringUtils.join(mp, "<br/>");
    }

    // Get a string containing the matching marker's associated MP IDs (if requested), separated by HTML line breaks.
    public String getMpIds(){
    	return getMpField("ID");
    }

    // Get a string containing the matching marker's associated MP terms (if requested), separated by HTML line breaks.
    public String getMpTerms(){
    	return getMpField("term");
    }

    // Get a string containing the matching marker's associated DO IDs or terms (if requested), 
    // separated by HTML line breaks.
    private String getDoField(String field){
    	if (!query.getDo()){ return ""; }
    	List<String> out = new ArrayList<String>();
    	String url;
    	for (Annotation annotation : bmi.getDOAnnotations()) {
    		// only want to keep positive annotations (no NOTs)
    		if (annotation.getQualifier() == null) {
    			if ("ID".equals(field)) {
    				out.add(String.format(noWrap, annotation.getTermID()));
    			} else if ("term".equals(field)) {
   					url = fewiUrl + "disease/" + annotation.getTermID();
   					out.add(String.format(noWrap, String.format(urlPattern, url, annotation.getTerm())));
    			}
    		}
		}
    	return StringUtils.join(out, "<br/>");
    }

    // Get a string containing the matching marker's associated DO IDs (if requested), separated by HTML line breaks.
    public String getDoIds(){
    	return getDoField("ID");
    }

    // Get a string containing the matching marker's associated DO terms (if requested), separated by HTML line breaks.
    public String getDoTerms(){
    	return getDoField("term");
    }

    // Get a string containing the matching marker's allele IDs or linked symbols (if requested), 
    // separated by HTML line breaks.
    private String getAlleleField(String field){
    	if (!query.getAllele()){ return ""; }
    	List<String> out = new ArrayList<String>();
    	String url;
    	for (BatchMarkerAllele allele : bmi.getAlleles()) {
    		if ("ID".equals(field)) {
    			out.add(String.format(noWrap, allele.getAlleleID()));
    		} else if ("symbol".equals(field)) {
    			url = fewiUrl + "allele/" + allele.getAlleleID();
    			out.add(String.format(noWrap, String.format(urlPattern, url,
						FormatHelper.superscript(allele.getAlleleSymbol()))));
    		}
		}
    	return StringUtils.join(out, "<br/>");
    }

    // Get a string containing the matching marker's allele IDs (if requested), separated by HTML line breaks.
    public String getAlleleIds(){
    	return getAlleleField("ID");
    }

    // Get a string containing the matching marker's (linked) allele symbols (if requested), separated by HTML line breaks.
    public String getAlleleSymbols(){
    	return getAlleleField("symbol");
    }

    // Get a string containing the matching marker's expression counts by tissue (if requested), 
    // separated by HTML line breaks.
    private String getExpressionTissueCountsField(String field){
    	if (!query.getExp()){ return ""; }
    	List<String> out = new ArrayList<String>();
    	String url;
    	for (MarkerTissueCount tissue : bmi.getExpressionCountsByTissue()) {
    		if ("structure".equals(field)) {
    			out.add(String.format(noWrap, tissue.getStructure()));
    		} else if ("all results".equals(field)) {
    			url = fewiUrl + String.format("gxd/summary?markerMgiId=%s&annotatedStructureKey=%d",
    					bmi.getMarkerID(), tissue.getStructureKey());
				out.add(String.format(urlPattern, url, tissue.getAllResultCount()));
    		} else if ("detected".equals(field)) {
    			out.add(String.format(noWrap, tissue.getDetectedResultCount()));
    		} else if ("not detected".equals(field)) {
    			out.add(String.format(noWrap, tissue.getNotDetectedResultCount()));
    		}
		}
    	return StringUtils.join(out, "<br/>");
    }

    // Get a string containing the matching marker's tissue names which have GXD data (if requested), 
    // separated by HTML line breaks.
    public String getExpressionStructure(){
    	return getExpressionTissueCountsField("structure");
    }

    // Get a string containing the matching marker's counts of expression results (if requested), 
    // separated by HTML line breaks.
    public String getExpressionResultCount(){
    	return getExpressionTissueCountsField("all results");
    }

    // Get a string containing the matching marker's counts of "detected" expression results (if requested), 
    // separated by HTML line breaks.
    public String getExpressionDetectedCount(){
    	return getExpressionTissueCountsField("detected");
    }

    // Get a string containing the matching marker's counts of "not detected" expression results (if requested), 
    // separated by HTML line breaks.
    public String getExpressionNotDetectedCount(){
    	return getExpressionTissueCountsField("not detected");
    }

    // Get a string containing the matching marker's associated RefSNPs (if requested), 
    // separated by HTML line breaks.
    public String getRefsnpIds(){
    	if (!query.getRefsnp()) { return ""; }
    	List<String> refSnpIds = new ArrayList<String>();
    	String url = fewiUrl + "snp/%s";
		for (String snp : bmi.getRefSNPs()) {
			String snpUrl = String.format(url, snp);
			refSnpIds.add(String.format(urlPattern, snpUrl, snp));
		}
    	return StringUtils.join(refSnpIds, "<br/>");
    }

    // Get a string containing the matching marker's RefSeq IDs (if requested), separated by HTML line breaks.
    public String getRefseqIds(){
    	if(!query.getRefseq()) return "";
    	return StringUtils.join(bmi.getIDs(DBConstants.PROVIDER_REFSEQ, "Sequence DB"), "<br/>");
    }

    // Get a string containing the matching marker's Swiss-Prot and TrEMBL IDs (if requested), separated by HTML line breaks.
    public String getUniprotIds(){
    	if(!query.getUniprot()) return "";
    	return StringUtils.join(bmi.getIDs(DBConstants.PROVIDER_SWISSPROT, DBConstants.PROVIDER_TREMBL), "<br/>");
    }
}