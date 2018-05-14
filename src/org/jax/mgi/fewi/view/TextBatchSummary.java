package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.DBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextBatchSummary extends AbstractTextView {
	
	private final Logger logger = LoggerFactory.getLogger(TextBatchSummary.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildTextDocument(Map<String, Object> model, BufferedWriter writer,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String filename = "MGIBatchReport_"+getCurrentDate();
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+".txt\"");
		logger.debug(response.getCharacterEncoding());
		
		BatchQueryForm queryForm = (BatchQueryForm)model.get("queryForm");
		startTimeoutPeriod();
		
		// grab the session so we can control the hibernate cache by evicting objects that we are done using.
		SessionFactory sessionFactory = (SessionFactory)model.get("sessionFactory");
		Session session = sessionFactory.getCurrentSession();
		
		writer.write(this.genHeader(queryForm));
		writer.write("\r\n");
		
		int rownum=1;
		for (BatchMarkerId id : (List<BatchMarkerId>) model.get("results")) {
			checkProgress(rownum);
			StringBuilder markerInfo = new StringBuilder();
			List<List<String>> associations = new ArrayList<List<String>>();
			Marker m = id.getMarker();
			List<MarkerID> ids;
			
			markerInfo.append(id.getTerm() + "\t");
			markerInfo.append(id.getTermType() + "\t");
			
			if (m != null){
				ids = m.getIds();
				markerInfo.append(m.getPrimaryID()); 
		 
				if(queryForm.getNomenclature()){
					markerInfo.append("\t");
					markerInfo.append(m.getSymbol() + "\t");
					markerInfo.append(m.getName() + "\t");
					markerInfo.append(m.getMarkerSubtype());
				}
				if(queryForm.getLocation()){
					markerInfo.append("\t");
					MarkerLocation loc = m.getPreferredCoordinates();
					if (loc != null) {
						markerInfo.append(loc.getChromosome() + "\t");
						markerInfo.append(loc.getStrand() + "\t");
						markerInfo.append(String.format("%.0f", loc.getStartCoordinate()) + "\t");
						markerInfo.append(String.format("%.0f", loc.getEndCoordinate()));
						evictCollection(session,m.getLocations());
					} else {
						markerInfo.append("UN\t\t");
					}
				}
				
				// build associations matrix
				if(queryForm.getEnsembl()){
					//logger.debug("ensembl");
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
					//logger.debug("entrez");
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
				}
				
				if(queryForm.getGo()){
					//logger.debug("go");
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
					evictCollection(session,m.getAnnotations());
				} else if(queryForm.getMp()){
					//logger.debug("mp");
					List<String> mpIds = new ArrayList<String>();
					StringBuffer mp;
	    			for (Annotation mpAnnot : m.getMPAnnotations()) {
	    				mp = new StringBuffer();
						mp.append(mpAnnot.getTermID() + "\t");
						mp.append(mpAnnot.getTerm());
						mpIds.add(mp.toString());
					}
		    		associations.add(mpIds);
					evictCollection(session,m.getAnnotations());
				} else if(queryForm.getDo()){
					List<String> doIds = new ArrayList<String>();
					StringBuffer dobuf;
	    			for (Annotation doAnnot : m.getDOAnnotations()) {
	    				dobuf = new StringBuffer();
	    				dobuf.append(doAnnot.getTermID() + "\t");
	    				dobuf.append(doAnnot.getTerm());
	    				doIds.add(dobuf.toString());
					}
		    		associations.add(doIds);
		    		evictCollection(session,m.getAnnotations());
				} else if(queryForm.getAllele()){
					//logger.debug("allele");
					List<String> alleles = new ArrayList<String>();
					StringBuffer allele;
	    			for (BatchMarkerAllele bma : m.getBatchMarkerAlleles()) {
	    				allele = new StringBuffer();
	    				allele.append(bma.getAlleleID() + "\t");
	    				allele.append(bma.getAlleleSymbol());
	    				alleles.add(allele.toString());
	    				session.evict(bma);
					}
		    		associations.add(alleles);
				} else if(queryForm.getExp()){
					//logger.debug("exp");
					List<String> expression = new ArrayList<String>();
					StringBuffer exp;
	    			for (MarkerTissueCount tissue : m.getMarkerTissueCounts()) {
	    				exp = new StringBuffer();
	    				exp.append(tissue.getStructure() + "\t");
	    				exp.append(tissue.getAllResultCount() + "\t");
	    				exp.append(tissue.getDetectedResultCount() + "\t");
	    				exp.append(tissue.getNotDetectedResultCount());
						expression.add(exp.toString());
						session.evict(tissue);
					}
		    		associations.add(expression);
				} else if(queryForm.getRefsnp()){
					//logger.debug("refsnp");
					List<String> refSnpIds = new ArrayList<String>();					
        			for (String snp : m.getBatchMarkerSnps()) {
        				refSnpIds.add(snp);
    				}
		    		associations.add(refSnpIds);
				} else if(queryForm.getRefseq()){
					//logger.debug("refseq");
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
					//logger.debug("uniprot");
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
					for (String s: combineResults) {
						rownum++;
						writer.write(markerInfo.toString());
						//markerInfo.append("\t" + s);
						writer.write("\t" + s + "\r\n");
					}
				}
				else
				{
					rownum++;
				}

				markerInfo.append("\r\n");
				combineResults = null;
				// make sure that maker info is still printed out if there are no combined results
				if( associations.size() == 0) writer.write(markerInfo.toString());	
				
				if(ids!=null)
				{
					evictCollection(session,ids);
				}
			}
			else {
				rownum++;
				markerInfo.append("No associated gene");
				markerInfo.append("\r\n");
			}
			if(m!=null) {
				
				session.evict(m);
				id.setMarker(null);
			}
			session.evict(id);
		}
		
		logger.info("finished processing batch text report");
	}
	
	private void evictCollection(Session session, Collection<?> collection)
	{
		for(Object o : collection)
		{
			session.evict(o);
		}
	}
	
	private String genHeader(BatchQueryForm queryForm){
		StringBuffer header = new StringBuffer();

		header.append("Input");
		header.append("\tInput Type");
		header.append("\tMGI Gene/Marker ID");
		 
		if(queryForm.getNomenclature()){
			header.append("\tSymbol");
			header.append("\tName");
			header.append("\tFeature Type");
		}
		if(queryForm.getLocation()){
			header.append("\tChr");
			header.append("\tStrand");
			header.append("\tStart");
			header.append("\tEnd");
		}
		if(queryForm.getEnsembl()){
			header.append("\tEnsembl ID");
		}
		if(queryForm.getEntrez()){
			header.append("\tEntrez Gene ID");
		}
		if(queryForm.getGo()){
			header.append("\tGO ID");
			header.append("\tTerm");
			header.append("\tCode");
		} else if(queryForm.getMp()){
			header.append("\tMP ID");
			header.append("\tTerm");
		} else if(queryForm.getDo()){
			header.append("\tDO ID");
			header.append("\tTerm");
		} else if(queryForm.getAllele()){
			header.append("\tAllele ID");
			header.append("\tSymbol");
		} else if(queryForm.getExp()){
			header.append("\tAnatomical Structure");
			header.append("\tAssay Results");
			header.append("\tDetected");
			header.append("\tNot Detected");
		} else if(queryForm.getRefsnp()){
			header.append("\tRefSNP ID");
		} else if(queryForm.getRefseq()){
			header.append("\tGenBank/RefSeq ID");
		} else if(queryForm.getUniprot()){
			header.append("\tUniprot ID");
		}
		return header.toString();
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
