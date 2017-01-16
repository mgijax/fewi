package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerGoAnnotation;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.BatchMarkerMpAnnotation;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.BatchQueryForm;
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
	private BatchMarkerId batchMarkerId;

	private BatchQueryForm query = null;

	// config values
    private final String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    private static String urlPattern = "<a href='%s' target='_blank'>%s</a>";
    private static String noWrap = "<span style='white-space:nowrap;'>%s</span>";

    private List<BatchMarkerGoAnnotation> goAnnots = null;
    private List<BatchMarkerMpAnnotation> mpAnnots = null;
    private List<Annotation> doAnnots = null;
    private List<BatchMarkerAllele> markerAlleles = null;
    private List<MarkerTissueCount> expCounts = null;

	//-------------
	// constructors
	//-------------

    public BatchSummaryRow (BatchMarkerId batchMarkerId, BatchQueryForm query) {
    	this.batchMarkerId = batchMarkerId;
    	this.query = query;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getTerm() {
    	return batchMarkerId.getTerm();
    }
    public String getType() {
    	return batchMarkerId.getTermType();
    }
    public String getMarkerId() {
    	if (batchMarkerId.getMarker() != null){
    		if (query != null && !query.getNomenclature()){
        		return String.format(urlPattern,
        				fewiUrl + "marker/" + batchMarkerId.getMarker().getPrimaryID(),
        				batchMarkerId.getMarker().getPrimaryID());
    		} else {
    			return batchMarkerId.getMarker().getPrimaryID();
    		}
    	} else {
    		return "No associated gene";
    	}
    }
    public String getSymbol() {
    	if (batchMarkerId.getMarker() != null && query.getNomenclature()){
    		return String.format(urlPattern,
    				fewiUrl + "marker/" + batchMarkerId.getMarker().getPrimaryID(),
    				batchMarkerId.getMarker().getSymbol());
    	} else {
    		return "";
    	}
    }
    public String getName() {
    	if (batchMarkerId.getMarker() != null && query.getNomenclature()){
    		return batchMarkerId.getMarker().getName();
    	} else {
    		return "";
    	}
    }
    public String getFeature() {
    	if (batchMarkerId.getMarker() != null && query.getNomenclature()){
    		return batchMarkerId.getMarker().getMarkerSubtype();
    	} else {
    		return "";
    	}
    }
    public String getChromosome() {
    	if (query.getLocation() && batchMarkerId.getMarker() != null) 
    	{
		    // first attempt to get the chromosome from the coordinates
	
	    	MarkerLocation loc = batchMarkerId.getMarker().getPreferredCoordinates();
	    	if (loc != null){
				if (loc.getChromosome() != null) {
				    return loc.getChromosome();
				} // if coordinate's chromosome is non-null
	    	} // if coordinates exist
	
		    // fall back on chromosome from a cM location
	
		    loc = batchMarkerId.getMarker().getPreferredCentimorgans();
		    if (loc != null){
	    		if (loc.getChromosome() != null){
	    			return loc.getChromosome();
	    		} // if cM location's chromosome is non-null
		    } // if cM location exists
    	} // if batchMarkerId.getMarker() != null
    	return "";
    }
    public String getStrand() {
    	if (batchMarkerId.getMarker() != null && query.getLocation()){
    		MarkerLocation loc = batchMarkerId.getMarker().getPreferredCoordinates();
    		if(loc != null){
    			return loc.getStrand();
    		}
    	}
    	return "";
    }
    public Double getStart() {
    	if (batchMarkerId.getMarker() != null && query.getLocation()){
    		MarkerLocation loc = batchMarkerId.getMarker().getPreferredCoordinates();
    		if(loc != null){
    			return loc.getStartCoordinate();
    		}
    	}
    	return null;
    }
    public Double getEnd() {
    	if (batchMarkerId.getMarker() != null && query.getLocation()){
    		MarkerLocation loc = batchMarkerId.getMarker().getPreferredCoordinates();
    		if(loc != null){
    			return loc.getEndCoordinate();
    		}
    	}
    	return null;
    }

    public String getEnsemblIds() {

    	if (!query.getEnsembl()) return "";
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_ENSEMBL);
    	return StringUtils.join(this.getId(l), "<br/>");
    }

    public String getEntrezIds() {
    	if(!query.getEntrez()) return "";
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_ENTREZGENE);
    	return StringUtils.join(this.getId(l), "<br/>");
    }

    public String getVegaIds() {
    	if (!query.getVega()) return "";
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_VEGA);
    	return StringUtils.join(this.getId(l), "<br/>");
    }

    public String getGoIds(){
    	if (!query.getGo()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> go = new ArrayList<String>();
    	if (marker != null && goAnnots == null){
    		goAnnots = marker.getBatchMarkerGoAnnotations();
    	}
    	if (goAnnots!= null && goAnnots.size() > 0){
    		for (BatchMarkerGoAnnotation annotation : goAnnots) {
    			go.add(String.format(noWrap, annotation.getGoId()));
			}
    	}
    	return StringUtils.join(go, "<br/>");
    }

    public String getGoTerms(){
    	if (!query.getGo()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> go = new ArrayList<String>();
    	if (marker != null && goAnnots == null){
    		goAnnots = marker.getBatchMarkerGoAnnotations();
    	}
    	if (goAnnots!= null && goAnnots.size() > 0){
    		for (BatchMarkerGoAnnotation annotation : goAnnots) {
    			go.add(String.format(noWrap, annotation.getGoTerm()));
			}
    	}
    	return StringUtils.join(go, "<br/>");
    }

    public String getGoCodes(){
    	if (!query.getGo()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	if (marker != null && goAnnots == null){
    		goAnnots = marker.getBatchMarkerGoAnnotations();
    	}
    	List<String> go = new ArrayList<String>();
    	if (goAnnots!= null && goAnnots.size() > 0){
    		for (BatchMarkerGoAnnotation annotation : goAnnots) {
    			go.add(String.format(noWrap, annotation.getEvidenceCode()));
			}
    	}
    	return StringUtils.join(go, "<br/>");
    }

    public String getMpIds(){
    	if (!query.getMp()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> mp = new ArrayList<String>();
    	if (marker != null && mpAnnots == null){
    		mpAnnots = marker.getBatchMarkerMpAnnotations();
    	}
    	if (mpAnnots != null && mpAnnots.size() > 0){
    		for (BatchMarkerMpAnnotation annotation : mpAnnots) {
    			mp.add(String.format(noWrap, annotation.getMpId()));
			}
    	}
    	return StringUtils.join(mp, "<br/>");
    }

    public String getMpTerms(){
    	if (!query.getMp()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> mp = new ArrayList<String>();
    	if (marker != null && mpAnnots == null){
    		mpAnnots = marker.getBatchMarkerMpAnnotations();
    	}
    	String text, url;
    	if (mpAnnots != null && mpAnnots.size() > 0){
    		for (BatchMarkerMpAnnotation annotation : mpAnnots) {
    			text = annotation.getMpTerm() + " (%s)";
			url = fewiUrl + String.format("mp/annotations/%s?markerID=%s", annotation.getMpId(), marker.getPrimaryID() );
    			mp.add(String.format(noWrap, String.format(text, String.format(urlPattern, url, "details"))));
			}
    	}
    	return StringUtils.join(mp, "<br/>");
    }

    public String getDoIds(){
    	if (!query.getDo()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> doa = new ArrayList<String>();
    	if (marker != null  && doAnnots == null){
    		doAnnots = batchMarkerId.getMarker().getDOAnnotations();
    	}
    	if (doAnnots != null){
    		for (Annotation annotation :doAnnots) {
    			// only keep positive annotations (no NOTs)
    			if (annotation.getQualifier() == null) {
    				doa.add(String.format(noWrap, annotation.getTermID()));
    			}
			}
    	}
    	return StringUtils.join(doa, "<br/>");
    }

    public String getDoTerms(){
    	if(!query.getDo()) return "";
    	Marker marker = batchMarkerId.getMarker();
    	List<String> doa = new ArrayList<String>();
    	if (marker != null  && doAnnots == null){
    		doAnnots = batchMarkerId.getMarker().getDOAnnotations();
    	}
    	if (doAnnots != null){
    		for (Annotation annotation : doAnnots) {
    			// only keep positive annotations (no NOTs)
    			if (annotation.getQualifier() == null) {
    				String url = fewiUrl + "disease/" + annotation.getTermID();
    				doa.add(String.format(noWrap, String.format(urlPattern, url, annotation.getTerm())));
    			}
			}
    	}
    	return StringUtils.join(doa, "<br/>");
    }

    public String getAlleleIds(){
    	List<String> alleleOutput = new ArrayList<String>();
    	Marker marker = batchMarkerId.getMarker();

    	if (query.getAllele() && marker != null){
    		if (markerAlleles == null){
    			markerAlleles = marker.getBatchMarkerAlleles();
    		}
    		for (BatchMarkerAllele allele : markerAlleles) {
    			alleleOutput.add(String.format(noWrap, allele.getAlleleID()));
			}
    	}
    	return StringUtils.join(alleleOutput, "<br/>");
    }

    public String getAlleleSymbols(){
    	List<String> alleleOutput = new ArrayList<String>();
    	Marker marker = batchMarkerId.getMarker();

    	if ( query.getAllele() && marker != null){
    		if (markerAlleles == null){
    			markerAlleles = marker.getBatchMarkerAlleles();
    		}
    		String url;
    		for (BatchMarkerAllele allele : markerAlleles) {
    			url = fewiUrl + "allele/" + allele.getAlleleID();
    			alleleOutput.add(String.format(noWrap, String.format(urlPattern, url,
						FormatHelper.superscript(allele.getAlleleSymbol()))));
			}
    	}
    	return StringUtils.join(alleleOutput, "<br/>");
    }

    public String getExpressionStructure(){
    	if (!query.getExp()){
    		return "";
    	}
    	List<String> structures = new ArrayList<String>();
    	Marker marker = batchMarkerId.getMarker();

    	if (marker != null  && expCounts == null){
    		expCounts = batchMarkerId.getMarker().getMarkerTissueCounts();
    	}

    	if (expCounts != null){
	    	for (MarkerTissueCount tissue : expCounts) {
	    		structures.add(String.format(noWrap, tissue.getStructure()));
			}
    	}
    	return StringUtils.join(structures, "<br/>");
    }

    public String getExpressionResultCount(){
    	if (!query.getExp()){
    		return "";
    	}
    	List<String> structures = new ArrayList<String>();
    	Marker marker = batchMarkerId.getMarker();

    	if (marker != null  && expCounts == null){
    		expCounts = batchMarkerId.getMarker().getMarkerTissueCounts();
    	}

    	if (expCounts != null){
	    	for (MarkerTissueCount tissue : expCounts) {
    			String url = fewiUrl + String.format("gxd/summary?markerMgiId=%s&annotatedStructureKey=%d",
    					marker.getPrimaryID(), tissue.getStructureKey());
    			structures.add(String.format(urlPattern, url, tissue.getAllResultCount()));
			}
    	}
    	return StringUtils.join(structures, "<br/>");
    }

    public String getExpressionDetectedCount(){
    	if (!query.getExp()){
    		return "";
    	}
    	List<Integer> structures = new ArrayList<Integer>();
    	Marker marker = batchMarkerId.getMarker();

    	if (marker != null  && expCounts == null){
    		expCounts = batchMarkerId.getMarker().getMarkerTissueCounts();
    	}

    	if (expCounts != null){
	    	for (MarkerTissueCount tissue : expCounts) {
	    		structures.add(tissue.getDetectedResultCount());
			}
    	}
    	return StringUtils.join(structures, "<br/>");
    }

    public String getExpressionNotDetectedCount(){
    	if (!query.getExp()){
    		return "";
    	}
    	List<Integer> structures = new ArrayList<Integer>();
    	Marker marker = batchMarkerId.getMarker();

    	if (marker != null  && expCounts == null){
    		expCounts = batchMarkerId.getMarker().getMarkerTissueCounts();
    	}

    	if (expCounts != null){
	    	for (MarkerTissueCount tissue : expCounts) {
	    		structures.add(tissue.getNotDetectedResultCount());
			}
    	}
    	return StringUtils.join(structures, "<br/>");
    }

    public String getRefsnpIds(){
    	List<String> refSnpIds = new ArrayList<String>();
    	String url = fewiUrl + "snp/%s";
    	if (query.getRefsnp() && batchMarkerId.getMarker() != null){
			for (String snp : batchMarkerId.getMarker().getBatchMarkerSnps()) {
				String snpUrl = String.format(url, snp);
				refSnpIds.add(String.format(urlPattern, snpUrl, snp));
			}
    	}
    	return StringUtils.join(refSnpIds, "<br/>");
    }

    public String getRefseqIds(){
    	if(!query.getRefseq()) return "";
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_REFSEQ);
    	l.add("Sequence DB");
    	return StringUtils.join(this.getId(l), "<br/>");
    }

    public String getUniprotIds(){
    	if(!query.getUniprot()) return "";
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_SWISSPROT);
    	l.add(DBConstants.PROVIDER_TREMBL);
    	return StringUtils.join(this.getId(l), "<br/>");
    }

    private List<String> getId(List<String> logicalDb){
    	List<String> idList = new ArrayList<String>();
    	if (batchMarkerId.getMarker() != null){
    		List<MarkerID> ids = batchMarkerId.getMarker().getIds();
    		if(ids != null){
    			for (MarkerID id : ids) {
    				for (String ldb : logicalDb) {
    					if (id.getLogicalDB().equals(ldb)){
    						idList.add(id.getAccID());
    					}
					}
				}
    		}
    	}
    	return idList;
    }

}
