package org.jax.mgi.fewi.view;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.BatchMarkerAllele;
import org.jax.mgi.fe.datamodel.BatchMarkerGoAnnotation;
import org.jax.mgi.fe.datamodel.BatchMarkerId;
import org.jax.mgi.fe.datamodel.BatchMarkerMpAnnotation;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerID;
import org.jax.mgi.fe.datamodel.MarkerLocation;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.util.BatchMarkerIdWrapper;
import org.jax.mgi.fewi.util.DBConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextBatchSummary extends AbstractTextView {
	
	private final Logger logger = LoggerFactory.getLogger(TextBatchSummary.class);
	
	// Prepend a tab character to string 's' if non-null.  If null, just return the tab character (for
	// an empty cell).
	private String tab(String s) {
		if (s != null) {
			return "\t" + s;
		}
		return "\t";
	}
	
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
			BatchMarkerIdWrapper bmi = new BatchMarkerIdWrapper(id);
			
			checkProgress(rownum);
			
			// the row we're building
			StringBuilder markerInfo = new StringBuilder();
			
			// list of 1-n relationships for the current match -- These will be combined into a cross-product
			// toward the end of processing for the current match.
			List<List<String>> associations = new ArrayList<List<String>>();

			// is this match to a marker?
			boolean hasMarker = (id.getMarker() != null);

			// three required fields:
			// search term that matched, the type of the matching term, and the ID of the matching object
			markerInfo.append(bmi.getTerm());
			markerInfo.append(tab(bmi.getTermType()));
			if (bmi.getMarkerID().length() != 0) {
				markerInfo.append(tab(bmi.getMarkerID())); 
			} else {
				markerInfo.append(tab("No associated gene"));
			}
			
			// user requested nomenclature fields
			if (queryForm.getNomenclature()){
				markerInfo.append(tab(bmi.getSymbol()));
				markerInfo.append(tab(bmi.getName()));
				markerInfo.append(tab(bmi.getFeatureType()));
			}

			// user requested location fields
			if (queryForm.getLocation()){
				if ((bmi.getChromosome() != null) && (bmi.getChromosome().length() > 0)) {
					markerInfo.append(tab(bmi.getChromosome()));
				} else {
					markerInfo.append(tab(null));
				}
				markerInfo.append(tab(bmi.getStrand()));

				if (bmi.getStart() != null) {
					markerInfo.append(tab(String.format("%.0f", bmi.getStart()))); 
				} else {
					markerInfo.append(tab(null));
				}
				
				if (bmi.getEnd() != null) {
					markerInfo.append(tab(String.format("%.0f", bmi.getEnd()))); 
				} else {
					markerInfo.append(tab(null));
				}
				
				if ((id.getMarker() != null) && (id.getMarker().getLocations() != null)) {
					evictCollection(session, id.getMarker().getLocations());
				}
			}

			// Now we begin building the lists of associations (1-n relationships for the match).  These
			// will be combined into a cross-product later on.  After the first two, only one option can
			// be chosen from the other association types.
			
			// user requested Ensembl IDs
			if(queryForm.getEnsembl()){
	        	associations.add(bmi.getIDs(DBConstants.PROVIDER_ENSEMBL));
			}
			
			// user requested Entrez Gene IDs
			if(queryForm.getEntrez()){
	        	associations.add(bmi.getIDs(DBConstants.PROVIDER_ENTREZGENE));
			}
			
			// user requested GO associations
			if (queryForm.getGo()) {
				List<String> goIds = new ArrayList<String>();
				StringBuffer go;
	    		for (BatchMarkerGoAnnotation goAnnot : bmi.getGOAnnotations()) {
	    			go = new StringBuffer();
					go.append(goAnnot.getGoId() + "\t");
					go.append(goAnnot.getGoTerm() + "\t");
					go.append(goAnnot.getEvidenceCode());
					goIds.add(go.toString());
				}
		    	associations.add(goIds);
		    	if (hasMarker && goIds.size() > 0) {
		    		evictCollection(session, id.getMarker().getBatchMarkerGoAnnotations());
		    	}
			}
			
			// user requested MP annotations
			else if (queryForm.getMp()) {
				List<String> mpIds = new ArrayList<String>();
				StringBuffer mp;
    			for (BatchMarkerMpAnnotation mpAnnot : bmi.getMPAnnotations()) {
    				mp = new StringBuffer();
					mp.append(mpAnnot.getMpId() + "\t");
					mp.append(mpAnnot.getMpTerm());
					mpIds.add(mp.toString());
				}
	    		associations.add(mpIds);
	    		if (hasMarker && mpIds.size() > 0) {
	    			evictCollection(session, id.getMarker().getBatchMarkerMpAnnotations());
	    		}
			}
			
			// user requested DO annotations
			else if(queryForm.getDo()){
				List<String> doIds = new ArrayList<String>();
				StringBuffer dobuf;
	    		for (Annotation doAnnot : bmi.getDOAnnotations()) {
	    			dobuf = new StringBuffer();
	    			dobuf.append(doAnnot.getTermID() + "\t");
	    			dobuf.append(doAnnot.getTerm());
	    			doIds.add(dobuf.toString());
				}
		    	associations.add(doIds);
		    	if (hasMarker && doIds.size() > 0) {
		    		evictCollection(session, id.getMarker().getAnnotations());
		    	}
			}
			
			// user requested alleles
			else if(queryForm.getAllele()){
				List<String> alleles = new ArrayList<String>();
				StringBuffer allele;
	    		for (BatchMarkerAllele bma : bmi.getAlleles()) {
	    			allele = new StringBuffer();
	    			allele.append(bma.getAlleleID() + "\t");
	    			allele.append(bma.getAlleleSymbol());
	    			alleles.add(allele.toString());
	    			session.evict(bma);
				}
		    	associations.add(alleles);
			}
			
			// user requested counts of expression results by tissue
			else if(queryForm.getExp()){
				List<String> expression = new ArrayList<String>();
				StringBuffer exp;
	    		for (MarkerTissueCount tissue : bmi.getExpressionCountsByTissue()) {
	    			exp = new StringBuffer();
	    			exp.append(tissue.getStructure() + "\t");
	    			exp.append(tissue.getAllResultCount() + "\t");
	    			exp.append(tissue.getDetectedResultCount() + "\t");
	    			exp.append(tissue.getNotDetectedResultCount());
					expression.add(exp.toString());
					session.evict(tissue);
				}
		    	associations.add(expression);
			}
			
			// user requested RefSNP IDs
			else if(queryForm.getRefsnp()){
				associations.add(bmi.getRefSNPs());
			}
			
			// user requested RefSeq IDs
			else if(queryForm.getRefseq()){
		    	associations.add(bmi.getIDs(DBConstants.PROVIDER_REFSEQ, "Sequence DB"));
			}
			
			// user requested UniProt IDs
			else if(queryForm.getUniprot()){
		    	associations.add(bmi.getIDs(DBConstants.PROVIDER_SWISSPROT, DBConstants.PROVIDER_TREMBL));
			}
			
			
			// if we have any association sets that are empty lists, add an empty value
			for (List<String> assoc: associations) {
				if (assoc.size() == 0) {
					assoc.add("");
				}
			}
				
			// compute the cross-product of the various lists of associations, so lists:
			// 		[ A, B, C ] and [ D, E, F ]
			// will produce rows:
			//		[ A, D ], [ A, E ], [ A, F ], [ B, D ], [ B, E ], [ B, F ], [ C, D ], [ C, E ], [ C, F ]

			List<String> combineResults = new ArrayList<String>();
			if (associations.size() > 0) {					
				combineResults = associations.get(0);
				
				if (associations.size() > 1) {				
					for (int i = 1; i < associations.size(); i++) {
						combineResults = combineSets(combineResults, associations.get(i));
					}				
				}

				// Now for each of those cross-product rows we generated:
				//	1. write out the left portion of the line (data before the associations)
				//	2. write out the association data for this row.
				for (String s: combineResults) {
					rownum++;
					writer.write(markerInfo.toString());
					writer.write("\t" + s + "\r\n");
				}
			}
			else {
				// no associations requested, so just write basic info
				markerInfo.append("\r\n");
				writer.write(markerInfo.toString());	
				rownum++;
			}

			combineResults = null;
				
			if (hasMarker) {
				session.evict(id.getMarker());
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
