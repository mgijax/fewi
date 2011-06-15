package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.BatchMarkerSnp;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.DBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextBatchSummary extends AbstractTextView {
	
	private Logger logger = LoggerFactory.getLogger(TextBatchSummary.class);

	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Content-Disposition","attachment; filename=\"batchReport.txt\"");
		System.out.println(response.getCharacterEncoding());
		
		BatchQueryForm queryForm = (BatchQueryForm)model.get("queryForm");
		List<BatchMarkerId> results = (List<BatchMarkerId>) model.get("results");
		
		Marker m;
		List<MarkerID> ids;
		StringBuffer markerInfo;
		List<List<String>> associations;
		
		for (BatchMarkerId id : results) {
			markerInfo = new StringBuffer();
			associations = new ArrayList<List<String>>();
			m = id.getMarker();
			
			

			
			markerInfo.append(id.getTerm() + "\t");
			markerInfo.append(id.getTermType() + "\t");
			
			if (m != null){
				ids = m.getIds();
				markerInfo.append(m.getPrimaryID()); 
		 
				if(queryForm.getNomenclature()){
					markerInfo.append("\t");
					markerInfo.append(m.getSymbol() + "\t");
					markerInfo.append(m.getName() + "\t");
					markerInfo.append(m.getMarkerType());
				}
				if(queryForm.getLocation()){
					markerInfo.append("\t");
					MarkerLocation loc = m.getPreferredCoordinates();
					if (loc != null) {
						markerInfo.append(loc.getChromosome() + "\t");
						markerInfo.append(loc.getStrand() + "\t");
						markerInfo.append(String.format("%.0f", loc.getStartCoordinate()) + "\t");
						markerInfo.append(String.format("%.0f", loc.getEndCoordinate()));
					} else {
						markerInfo.append("UN\t\t");
					}
				}
				
				// build associations matrix
				if(queryForm.getEnsembl()){
					logger.debug("ensembl");
					List<String> eIds = new ArrayList<String>();
	        		if(ids != null){
	        			for (MarkerID mId : ids) {
	    					if (mId.getLogicalDB().equals(DBConstants.PROVIDER_ENSEMBL)){
	    						eIds.add(mId.getAccID());
	    					}						
	    				}
	        		}
	        		associations.add(eIds);
				}
				if(queryForm.getEntrez()){
					logger.debug("entrez");
					List<String> eIds = new ArrayList<String>();
		    		if(ids != null){
		    			for (MarkerID mId : ids) {
							if (mId.getLogicalDB().equals(DBConstants.PROVIDER_ENTREZGENE)){
								eIds.add(mId.getAccID());
							}						
						}
		    		}
		    		associations.add(eIds);
				}
				if(queryForm.getVega()){
					logger.debug("vega");
					List<String> vIds = new ArrayList<String>();
		    		if(ids != null){
		    			for (MarkerID mId : ids) {
							if (mId.getLogicalDB().equals(DBConstants.PROVIDER_VEGA)){
								vIds.add(mId.getAccID());
							}						
						}
		    		}
		    		associations.add(vIds);
				}
				
				if(queryForm.getGo()){
					logger.debug("go");
					List<String> goIds = new ArrayList<String>();
					StringBuffer go;
	    			for (Annotation goAnnot : m.getGoAnnotations()) {
	    				go = new StringBuffer();
						go.append(goAnnot.getTermID() + "\t");
						go.append(goAnnot.getTerm() + "\t");
						go.append(goAnnot.getEvidenceCode());
						goIds.add(go.toString());
					}
		    		associations.add(goIds);
				} else if(queryForm.getMp()){
					logger.debug("mp");
					List<String> mpIds = new ArrayList<String>();
					StringBuffer mp;
	    			for (Annotation mpAnnot : m.getMPAnnotations()) {
	    				mp = new StringBuffer();
						mp.append(mpAnnot.getTermID() + "\t");
						mp.append(mpAnnot.getTerm() + "\t");
						mp.append(mpAnnot.getEvidenceCode());
						mpIds.add(mp.toString());
					}
		    		associations.add(mpIds);
				} else if(queryForm.getOmim()){
					logger.debug("omim");
					List<String> mpIds = new ArrayList<String>();
					StringBuffer mp;
	    			for (Annotation mpAnnot : m.getMPAnnotations()) {
	    				mp = new StringBuffer();
						mp.append(mpAnnot.getTermID() + "\t");
						mp.append(mpAnnot.getTerm());
	
						mpIds.add(mp.toString());
					}
		    		associations.add(mpIds);
				} else if(queryForm.getAllele()){
					logger.debug("allele");
					List<String> alleles = new ArrayList<String>();
					StringBuffer allele;
	    			for (BatchMarkerAllele bma : m.getBatchMarkerAlleles()) {
	    				allele = new StringBuffer();
	    				allele.append(bma.getAlleleID() + "\t");
	    				allele.append(bma.getAlleleSymbol());
	    				alleles.add(allele.toString());
					}
		    		associations.add(alleles);
				} else if(queryForm.getExp()){
					logger.debug("exp");
					List<String> expression = new ArrayList<String>();
					StringBuffer exp;
	    			for (MarkerTissueCount tissue : m.getMarkerTissueCounts()) {
	    				exp = new StringBuffer();
	    				exp.append(tissue.getStructure() + "\t");
	    				exp.append(tissue.getAllResultCount() + "\t");
	    				exp.append(tissue.getDetectedResultCount() + "\t");
	    				exp.append(tissue.getNotDetectedResultCount());
						expression.add(exp.toString());
					}
		    		associations.add(expression);
				} else if(queryForm.getRefsnp()){
					logger.debug("refsnp");
					List<String> refSnpIds = new ArrayList<String>();					
        			for (BatchMarkerSnp snp : m.getBatchMarkerSnps()) {
        				refSnpIds.add(snp.getSnpID());
    				}
		    		associations.add(refSnpIds);
				} else if(queryForm.getRefseq()){
					logger.debug("refseq");
					List<String> refSeqIds = new ArrayList<String>();
	        		if(ids != null){
	        			for (MarkerID mId : ids) {
	    					if (mId.getLogicalDB().equals(DBConstants.PROVIDER_REFSEQ) ||
	    							mId.getLogicalDB().equals("Sequence DB")){
	    						refSeqIds.add(mId.getAccID());
	    					}						
	    				}
	        		}
		    		associations.add(refSeqIds);
				} else if(queryForm.getUniprot()){
					logger.debug("uniprot");
					List<String> uniProtIds = new ArrayList<String>();
	        		if(ids != null){
	        			for (MarkerID mId : ids) {
	    					if (mId.getLogicalDB().equals(DBConstants.PROVIDER_SWISSPROT) ||
	    							mId.getLogicalDB().equals(DBConstants.PROVIDER_TREMBL)){
	    						uniProtIds.add(mId.getAccID());
	    					}						
	    				}
	        		}
		    		associations.add(uniProtIds);
				}
				
				for (List<String> assoc: associations) {
					if (assoc.size() == 0) {
						assoc.add("");
					}
				}
				
				List<String> combineResults = new ArrayList<String>();
				if (associations.size() > 0) {
					combineResults = associations.get(0);
					
					if (associations.size() > 1) {				
						for (int i = 1; i < associations.size(); i++) {
							combineResults = combineSets(combineResults, associations.get(i));
						}				
					}
				}
				if (combineResults.size() > 0){
					for (String s: combineResults) {
						logger.debug(s + ".");
						writer.write(markerInfo.toString());
						writer.write("\t" + s);		
						writer.write("\r\n");
					}
				} else {
					writer.write(markerInfo.toString());		
					writer.write("\r\n");
				}
			}
		}
	}
	
	private List<String> combineSets (List<String> listA, List<String> listB){
		List<String> results = new ArrayList<String>();
		for (String a: listA ){
			for (String b: listB ) {
				results.add(a + "\t" + b);
			}
		}
		return results;
	}

}
