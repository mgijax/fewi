package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.BatchMarkerSnp;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a Marker;  represents on row in summary
 */
public class BatchSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(BatchSummaryRow.class);

	// encapsulated row object
	private BatchMarkerId batchMarkerId;
	
	private BatchQueryForm query = null;

	// config values
    private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    private String javawiUrl = ContextLoader.getConfigBean().getProperty("JAVAWI_URL");
    private String wiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
    
    private static String urlPattern = "<a href='%s' target='_blank'>%s</a>";
    
    private List<Annotation> goAnnots = null;
    private List<Annotation> mpAnnots = null;
    private List<Annotation> omimAnnots = null;
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
    		return "";
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
    		return batchMarkerId.getMarker().getMarkerType();
    	} else {
    		return "";
    	}
    }
    public String getChromosome() {
    	if (batchMarkerId.getMarker() != null && query.getLocation()){
    		MarkerLocation loc = batchMarkerId.getMarker().getPreferredCoordinates();
    		if(loc != null){
    			return loc.getChromosome();
    		} else {
    			return "UN";
    		}
    	}
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
    	List<String> l = new ArrayList<String>();
    	if (query.getEnsembl()){
    		l.add(DBConstants.PROVIDER_ENSEMBL);
    	}
    	return StringUtils.join(this.getId(l), "<br/>");
    }
    
    public String getEntrezIds() {
    	List<String> l = new ArrayList<String>();
    	if (query.getEntrez()){
    		l.add(DBConstants.PROVIDER_ENTREZGENE);
    	}
    	return StringUtils.join(this.getId(l), "<br/>");
    }
    
    public String getVegaIds() {
    	List<String> l = new ArrayList<String>();
    	if (query.getVega()){
    		l.add(DBConstants.PROVIDER_VEGA);
    	}
    	return StringUtils.join(this.getId(l), "<br/>");
    }
    
    public String getGoIds(){
    	if (!query.getGo()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> go = new ArrayList<String>();
    	if (marker != null && goAnnots == null){
    		goAnnots = batchMarkerId.getMarker().getGoAnnotations();
    	}
    	if (goAnnots != null){
    		for (Annotation annotation : goAnnots) {
    			go.add(annotation.getTermID());
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
    		goAnnots = batchMarkerId.getMarker().getGoAnnotations();
    	}
    	if (goAnnots != null){
    		for (Annotation annotation : goAnnots) {
    			go.add(annotation.getTerm());
			}
    	}
    	return StringUtils.join(go, "<br/>");
    } 
    
    public String getGoCodes(){
    	if (!query.getGo()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> go = new ArrayList<String>();
    	if (marker != null && goAnnots == null){
    		goAnnots = batchMarkerId.getMarker().getGoAnnotations();
    	}
    	if (goAnnots != null){
    		for (Annotation annotation : goAnnots) {
    			go.add(annotation.getEvidenceCode());
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
    		mpAnnots = batchMarkerId.getMarker().getMPAnnotations();
    	}
    	if (mpAnnots != null){
    		for (Annotation annotation : mpAnnots) {
    			mp.add(annotation.getTermID());
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
    		mpAnnots = batchMarkerId.getMarker().getMPAnnotations();
    	}
    	String text, url;
    	if (mpAnnots != null){
    		for (Annotation annotation : mpAnnots) {
    			text = annotation.getTerm() + " (%s)"; 
    			url = javawiUrl + String.format("WIFetch?page=mpAnnotSummary&markerKey=%d&id=%s", 
    					marker.getMarkerKey(), annotation.getTermID());
    			mp.add(String.format(text, String.format(urlPattern, url, "details")));
			}
    	}
    	return StringUtils.join(mp, "<br/>");
    }
    
    public String getOmimIds(){
    	if (!query.getOmim()){
    		return "";
    	}
    	Marker marker = batchMarkerId.getMarker();
    	List<String> omim = new ArrayList<String>();
    	if (marker != null  && omimAnnots == null){
    		logger.debug("get omim");
    		omimAnnots = batchMarkerId.getMarker().getOMIMAnnotations();
    	}
    	if (omimAnnots != null){
    		for (Annotation annotation : omimAnnots) {
    			logger.debug(annotation.getTermID());
    			omim.add(annotation.getTermID());
			}
    	}
    	return StringUtils.join(omim, "<br/>");
    }
    
    public String getOmimTerms(){
    	Marker marker = batchMarkerId.getMarker();
    	List<String> omim = new ArrayList<String>();
    	if (marker != null  && omimAnnots == null){
    		omimAnnots = batchMarkerId.getMarker().getOMIMAnnotations();
    	}
    	if (omimAnnots != null){
    		String url;
    		for (Annotation annotation : omimAnnots) {
    			url = javawiUrl + "WIFetch?page=humanDisease&id=" + annotation.getTermID();
    			omim.add(String.format(urlPattern, url, annotation.getTerm()));
			}
    	}
    	return StringUtils.join(omim, "<br/>");
    }
    
    public String getAlleleIds(){
    	List<String> alleleOutput = new ArrayList<String>();
    	Marker marker = batchMarkerId.getMarker();

    	if (marker != null){
    		if (markerAlleles == null){
    			markerAlleles = marker.getBatchMarkerAlleles();
    		}
    		for (BatchMarkerAllele allele : markerAlleles) {
				alleleOutput.add(allele.getAlleleID());
			}
    	}
    	return StringUtils.join(alleleOutput, "<br/>");
    }
    
    public String getAlleleSymbols(){
    	List<String> alleleOutput = new ArrayList<String>();
    	Marker marker = batchMarkerId.getMarker();

    	if (marker != null){
    		if (markerAlleles == null){
    			markerAlleles = marker.getBatchMarkerAlleles();
    		}
    		String url;
    		for (BatchMarkerAllele allele : markerAlleles) {
    			url = fewiUrl + "allele/" + allele.getAlleleID();
				alleleOutput.add(String.format(urlPattern, url,
						FormatHelper.superscript(allele.getAlleleSymbol())));
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
	    		structures.add(tissue.getStructure());
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
    		String url;
	    	for (MarkerTissueCount tissue : expCounts) {
	    		logger.debug(tissue.getStructure() + ": " + String.valueOf(tissue.getAllResultCount()));
	    		
    			url = wiUrl + String.format("searches/expression_report.cgi?Anatomical structure&returnType=assay results&_Marker_key=%d&_Structure_key=%d", 
    					marker.getMarkerKey(), tissue.getStructureKey());
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
    	String url = javawiUrl + "WIFetch?page=snpDetail&id=%s";
    	if (batchMarkerId.getMarker() != null){
			for (BatchMarkerSnp snp : batchMarkerId.getMarker().getBatchMarkerSnps()) {
				url = String.format(url, snp.getSnpID());
				String.format(urlPattern, url, snp.getSnpID());
				refSnpIds.add(String.format(urlPattern, url, snp.getSnpID()));
			}
    	}
    	return StringUtils.join(refSnpIds, "<br/>");
    }    
    
    public String getRefseqIds(){
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_REFSEQ);
    	l.add("Sequence DB");   	
    	return StringUtils.join(this.getId(l), "<br/>");
    }
    
    public String getUniprotIds(){
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
    					logger.debug(ldb + ": " + id.getAccID() + ": " + id.getLogicalDB());
    					if (id.getLogicalDB().equals(ldb)){
    						logger.debug("add");
    						idList.add(id.getAccID());
    					}	
					}					
				}
    		}
    	}
    	logger.debug("ids: " + idList.size());
    	return idList;
    }

}
