package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.StrainMarker;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HibernateBatchSummaryHunter
{
    // logger for the class
    private final Logger logger = LoggerFactory.getLogger(HibernateBatchSummaryHunter.class);
    
    private static SmartAlphaComparator smartAlphaComparator = new SmartAlphaComparator();

	@Autowired
	private SessionFactory sessionFactory;
	
	private final Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
	
    public HibernateBatchSummaryHunter() {
				
		List<String> uniprotItems = new ArrayList<String>();
		uniprotItems.add("SWISS-PROT");
		uniprotItems.add("TrEMBL");
		typeMap.put("UniProt", uniprotItems);
		
		List<String> ensemblItems = new ArrayList<String>();
		ensemblItems.add("Ensembl Gene Model");
		ensemblItems.add("Ensembl Protein");
		ensemblItems.add("Ensembl Transcript");
		typeMap.put("Ensembl", ensemblItems);
		
		List<String> nomenItems = new ArrayList<String>();
		nomenItems.add("old symbol");
		nomenItems.add("%synonym");
		nomenItems.add("% symbol");	// removed 'ortholog'
		nomenItems.add("current name");
		nomenItems.add("current symbol");
		typeMap.put("nomen", nomenItems);
		
		List<String> genbankItems = new ArrayList<String>();
		genbankItems.add("GenBank");
		genbankItems.add("RefSeq");
		typeMap.put("GenBank", genbankItems);
	}

	@SuppressWarnings("unchecked")
	public void hunt(SearchParams searchParams, SearchResults<BatchMarkerId> searchResults) 
	{
        logger.debug("-> hunt");        
        
        // the queried id set
    	List<String> idSet = new ArrayList<String>();
    	// actual ids sent to query are lowercased
    	List<String> idSetLower = new ArrayList<String>();  	
    	
    	// begin generating the HQSL clauses
    	StringBuffer hql = new StringBuffer("FROM BatchMarkerId WHERE ");
    	List<String> clauses = new ArrayList<String>();
    	 		
		for (Filter f : searchParams.getFilter().getNestedFilters()) {
			String prop = f.getProperty();
			if (SearchConstants.BATCH_TERM.equals(prop)){
				idSet = f.getValues();
				for (String id: idSet) {
					idSetLower.add(id.toLowerCase());
				}
				clauses.add("lower(term) IN (:ids) ");
			} 
			else if (SearchConstants.BATCH_TYPE.equals(prop)) {
		    	List<String> termTypes; 
				if (typeMap.containsKey(f.getValue())){
					termTypes = typeMap.get(f.getValue());
				} 
				else {
					termTypes = Arrays.asList(f.getValue());
				}
				List<String> tClauses = new ArrayList<String>();
				for (String type : termTypes) {
					tClauses.add("term_type LIKE '" + type + "'");
				}
				clauses.add("(" + StringUtils.join(tClauses, " OR ") + ")");
			}
		}
    	hql.append(StringUtils.join(clauses, " AND "));

        logger.debug("-> filter parsed" );   
        
    	// perform HSQL query for BathMarkerIds
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        
        List<BatchMarkerId> qr = new ArrayList<BatchMarkerId>();
        
		int start = 0, end = 0, batchSize = 2000;
		while (start < idSetLower.size()){
			end = start + batchSize;
			if (end > idSetLower.size()) end = idSetLower.size();
			logger.debug(String.format("batch %d-%d", start, end));
			query.setParameterList("ids", idSetLower.subList(start, end));				
			qr.addAll(query.list());
			start = end;
		}
		idSetLower = null;

        logger.debug("-> query complete" );

        // organize the results grouped by query term
        Map<String, List<BatchMarkerId>> qResults = new HashMap<String, List<BatchMarkerId>>();
        Set<String> markerKey = new HashSet<String>();
        BatchMarkerIdComparator comparator = new BatchMarkerIdComparator();
        
        for (BatchMarkerId item: qr){
        	BatchMarkerId bmi = item;
        	
        	// tracking a unique set of marker keys
        	markerKey.add(bmi.getUniqueKey());
        	
        	// get the batch marker term and lowercase it 
        	// to maintain a map of BatchMarkerId records for that lowercase term
        	String bTerm = bmi.getTerm().toLowerCase();
        	List <BatchMarkerId> bResults;
        	if (qResults.containsKey(bTerm)) {
        		bResults = qResults.get(bTerm);
        	} else {
        		bResults = new ArrayList<BatchMarkerId>();
        		qResults.put(bTerm, bResults);
        	}
        	bResults.add(bmi);
        	if (bResults.size() > 1) {
				Collections.sort(bResults, comparator);
        	}
        }
        qr = null;
        logger.debug("-> results parsed" );
        
        // build the final result set by stepping through the original list of IDs (in order)
        // and mapping them to the retrieved BatchMarkerId records
        List<BatchMarkerId> returnList = new ArrayList<BatchMarkerId>();
        for (String id: idSet) {
        	if (qResults.containsKey(id.toLowerCase())) {
        		for (BatchMarkerId b : qResults.get(id.toLowerCase())) {
        			b.setTerm(id);
					returnList.add(b);
				}
        	} else {
        		BatchMarkerId blankId = new BatchMarkerId();
    			blankId.setTerm(id);
    			blankId.setTermType("");
    			returnList.add(blankId);
        	}
        }
        
        logger.debug("-> results sorted" );
        
        searchResults.setTotalCount(returnList.size());
        logger.debug("markers: " + markerKey.size());
        searchResults.getResultSetMeta().addCount("marker", markerKey.size());
        
        int endIndex = searchParams.getStartIndex() + searchParams.getPageSize();
        if (endIndex > returnList.size()){
        	endIndex = returnList.size();
        }
        searchResults.setResultObjects(returnList.subList(searchParams.getStartIndex(), 
        		endIndex));
    }

	private class BatchMarkerIdComparator implements Comparator<BatchMarkerId> {
		@Override
		public int compare(BatchMarkerId a, BatchMarkerId b) {
			// sort so canonical marker matches come before strain marker matches, and so 
			// C57BL/6J strain marker matches come before other strains
			
			Marker am = a.getMarker();
			Marker bm = b.getMarker();
			
			if (am != null) {
				if (bm != null) {
					// both are canonical marker matches, so smart alpha sort
					return smartAlphaComparator.compare(am.getSymbol(), bm.getSymbol());
				} else {
					// a comes first
					return -1;
				}
			} else if (bm != null) {
				// b comes first
				return 1;
			}
			
			// If we got here, both are strain marker matches.  Sort by strain, preferring C57BL/6J.
			
			StrainMarker asm = a.getStrainMarker();
			StrainMarker bsm = b.getStrainMarker();
			
			if (asm != null) {
				if (bsm != null) {
					// This should be the only branch we actually hit in this section.
					
					if ("C57BL/6J".equals(asm.getStrainName())) {
						return -1;
					} else if ("C57BL/6J".equals(bsm.getStrainName())) {
						return 1;
					}
					return smartAlphaComparator.compare(asm.getStrainName(), bsm.getStrainName());
					
				} else {
					// should not happen (a is defined, b is not)
					return -1;
				}
			} else {
				// should not happen (a is not defined, b is)
				return 1;
			}
		}
	}
	
	/* skip the complexity of matching results to the IDs that matched each
	 * and just consolidate the results into a list of matching marker IDs.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getMarkerIDs(SearchParams searchParams) {
 	       logger.debug("-> getMarkerIDs()");
        
        	// the queried id set
    		List<String> idSet = new ArrayList<String>();
    		// actual ids sent to query are lowercased
    		List<String> idSetLower = new ArrayList<String>();  	
    	
 	   	// begin generating the HQSL clauses
 	   	StringBuffer hql = new StringBuffer("FROM BatchMarkerId WHERE ");
 	   	List<String> clauses = new ArrayList<String>();
    	 		
		for (Filter f : searchParams.getFilter().getNestedFilters()) {
			String prop = f.getProperty();
			if (SearchConstants.BATCH_TERM.equals(prop)){
				idSet = f.getValues();
				for (String id: idSet) {
					idSetLower.add(id.toLowerCase());
				}
				clauses.add("lower(term) IN (:ids) ");
			} 
			else if (SearchConstants.BATCH_TYPE.equals(prop)) {
			    	List<String> termTypes; 
				if (typeMap.containsKey(f.getValue())){
					termTypes = typeMap.get(f.getValue());
				} 
				else {
					termTypes = Arrays.asList(f.getValue());
				}
				List<String> tClauses = new ArrayList<String>();
				for (String type : termTypes) {
					tClauses.add("term_type LIKE '" + type + "'");
				}
				clauses.add("(" + StringUtils.join(tClauses, " OR ") + ")");
			}
		}
 	   	hql.append(StringUtils.join(clauses, " AND "));

		logger.debug("-> filter parsed" );   
        
    		// perform HSQL query for BatchMarkerIds
		Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        
		HashSet<String> markerIDs = new HashSet<String>();
        
		int start = 0, end = 0, batchSize = 2000;
		while (start < idSetLower.size()){
			end = start + batchSize;
			if (end > idSetLower.size()) end = idSetLower.size();
			logger.debug(String.format("batch %d-%d", start, end));
			query.setParameterList("ids", idSetLower.subList(start, end));				
			for (BatchMarkerId bmi : (List<BatchMarkerId>) query.list()) {
				markerIDs.add(bmi.getMarker().getPrimaryID());
			}
			start = end;
		}
		idSetLower = null;

	        logger.debug("-> query complete" );

		List<String> markerIDList = new ArrayList<String>();
		for (String s : markerIDs.toArray(new String[0])) {
			markerIDList.add(s);
		}

		logger.debug("-> found " + markerIDList.size() + " marker IDs");
		return markerIDList;
	}
}
