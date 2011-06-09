package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerId;
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
    
    
    private static String urlPattern = "<a href='%s'>%s</a>";
    
    private Annotation goAnnot = null;
    private Annotation mpAnnot = null;
    private Annotation omimAnnot = null;
    private BatchMarkerAllele markerAllele = null;
    private MarkerTissueCount expCount = null;

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
    	if (!query.getGo() || goAnnot == null){
    		return "";
    	} else {
    		return goAnnot.getTermID();
    	}
    } 
    
    public String getGoTerms(){
    	if (!query.getGo() || goAnnot == null){
    		return "";
    	} else {
    		return goAnnot.getTerm();
    	}
    } 
    
    public String getGoCodes(){
    	if (!query.getGo() || goAnnot == null){
    		return "";
    	} else {
    		return goAnnot.getEvidenceCode();
    	}
    } 
    
    public String getMpIds(){
    	if (!query.getMp() || mpAnnot == null){
    		return "";
    	} else {
    		return mpAnnot.getTermID();
    	}
    }
    
    public String getMpTerms(){
    	if (!query.getMp() || mpAnnot == null){
    		return "";
    	} else {
			String text = mpAnnot.getTerm() + " (%s)"; 
			String url = fewiUrl + "mp/" + mpAnnot.getTermID();
			return String.format(text, String.format(urlPattern, url, "details"));
    	}
    }
    
    public String getOmimIds(){
    	if (!query.getOmim() || omimAnnot == null){
    		return "";
    	} else {
    		return omimAnnot.getTermID();
    	}
    }
    
    public String getOmimTerms(){
    	if (!query.getOmim() || omimAnnot == null){
    		return "";
    	} else {
			String url = fewiUrl + "omim/" + omimAnnot.getTermID();
			return String.format(urlPattern, url, omimAnnot.getTerm());
    	}
    }
    
    public String getAlleleIds(){
    	if (!query.getAllele() || markerAllele == null){
    		return "";
    	} else {
			return markerAllele.getAlleleID();
    	}
    }
    
    public String getAlleleSymbols(){
    	if (!query.getAllele() || markerAllele == null){
    		return "";
    	} else {
			String url = fewiUrl + "allele/" + markerAllele.getAlleleID();
			return String.format(urlPattern, url,
					FormatHelper.superscript(markerAllele.getAlleleSymbol()));
    	}
    }
    
    public String getExpressionStructure(){
    	if (!query.getExp() || expCount == null){
    		return "";
    	} else {
    		return expCount.getStructure();
    	}
    }
    
    public String getExpressionResultCount(){
    	if (!query.getExp() || expCount == null){
    		return "";
    	} else {
    		return String.valueOf(expCount.getAllResultCount());
    	}
    }
    
    public String getExpressionDetectedCount(){
    	if (!query.getExp() || expCount == null){
    		return "";
    	} else {
    		return String.valueOf(expCount.getDetectedResultCount());
    	}
    }
    
    public String getExpressionNotDetectedCount(){
    	if (!query.getExp() || expCount == null){
    		return "";
    	} else {
    		return String.valueOf(expCount.getNotDetectedResultCount());
    	}
    }
    
    public String getRefsnpIds(){
    	return "";
    }    
    
    public String getRefseqIds(){
    	List<String> l = new ArrayList<String>();
    	l.add(DBConstants.PROVIDER_REFSEQ);
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
    					if (id.getLogicalDB().equals(ldb)){
    						idList.add(id.getAccID());
    					}	
					}					
				}
    		}
    	}
    	return idList;
    }


	public void setGoAnnot(Annotation goAnnot) {
		this.goAnnot = goAnnot;
	}


	public void setMpAnnot(Annotation mpAnnot) {
		this.mpAnnot = mpAnnot;
	}


	public void setOmimAnnot(Annotation omimAnnot) {
		this.omimAnnot = omimAnnot;
	}


	public void setMarkerAllele(BatchMarkerAllele markerAllele) {
		this.markerAllele = markerAllele;
	}


	public void setExpCount(MarkerTissueCount expCount) {
		this.expCount = expCount;
	}

}
