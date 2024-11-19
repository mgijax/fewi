package org.jax.mgi.fewi.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.matrix.DagMatrixRowOpener;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.jax.mgi.fewi.matrix.OpenCloseState;
import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Functions for generating the Gxd Matrices
 */
@Repository
public class GxdMatrixHandler {

	
	private final Logger logger = LoggerFactory.getLogger(GxdMatrixHandler.class);
	
	@Autowired
	private VocabularyFinder vocabFinder;
	
	@Autowired
	private DagMatrixRowOpener rowOpener;
	
	// sort the list of VocabTerms alphanumerically
	private List<VocabTerm> sortTerms(List<VocabTerm> terms) {
		if ((terms != null) && (terms.size() > 0)) {
			Collections.sort(terms, terms.get(0).getEmapsComparator());
		}
		return terms;
	}

	public GxdMatrixRow makeGxdMatrixRow(VocabTerm term)
	{
		return (new GxdMatrixRow()).makeMatrixRow(term);
	}

	private static List<VocabTerm> highLevelEmapaTerms = null;
	private static volatile Object highLevelTermsLock = new Object();
	public List<GxdMatrixRow> getHighLevelTerms()
	{
		String mouseId = "EMAPA:25765"; // it all starts here...
		String organSystemId = "EMAPA:16103";
		if(highLevelEmapaTerms==null)
		{
			synchronized (GxdMatrixHandler.highLevelTermsLock) {
				if(highLevelEmapaTerms==null)
				{
					logger.debug("initializing high level emapa terms");
					List<VocabTerm> terms = vocabFinder.getTermByID(mouseId);
		
					logger.debug("found "+terms.size()+" terms for \"mouse\" ID: "+mouseId);
					// go one level deep
					highLevelEmapaTerms = new ArrayList<VocabTerm>();
					for(VocabTerm topTerm : sortTerms(terms))
					{
						highLevelEmapaTerms.add(topTerm);
						for(VocabTerm secondLevelTerm : sortTerms(topTerm.getChildren()))
						{
							if(secondLevelTerm.getPrimaryId().equals(organSystemId))
							{
								secondLevelTerm.getChildren().size();
							}
						}
					}
					logger.debug("done initializing high level emapa terms");
				}
			}
		}

		// Now build on the fly, the matrix rows for the high level terms
		List<GxdMatrixRow> highLevelRows = new ArrayList<GxdMatrixRow>();
		for(VocabTerm term : highLevelEmapaTerms)
		{
			GxdMatrixRow topTerm = makeGxdMatrixRow(term);
			topTerm.setOc(OpenCloseState.OPEN);
			highLevelRows.add(topTerm);
			for(VocabTerm child : term.getChildren())
			{
				// look for organ system
				GxdMatrixRow secondLevelTerm = makeGxdMatrixRow(child);
				topTerm.addChild(secondLevelTerm);
				if(child.getPrimaryId().equals(organSystemId))
				{
					secondLevelTerm.setOc(OpenCloseState.OPEN);
					List<VocabTerm> organSystemTerms = child.getChildren();
					for(VocabTerm organSystemTerm : sortTerms(organSystemTerms))
					{
						GxdMatrixRow thirdLevelTerm = makeGxdMatrixRow(organSystemTerm);
						secondLevelTerm.addChild(thirdLevelTerm);
					}
				}
			}
		}
		return highLevelRows;
	}

	/*
	 * Based on query submitted we display either the structure view,
	 * 	or the high level emapa terms view
	 */
	public List<GxdMatrixRow> getParentTermsToDisplay(GxdQueryForm query,String childrenOf,List<String> pathsToOpen)
	{
		boolean mapChildren = childrenOf!=null && !childrenOf.equals("");
		boolean mapDifferentialStructures = isTissueMatrixDifferentialView(query);
		boolean mapProfileStructures = isMatrixProfileView(query);
		boolean mapStructureFilters = isTissueMatrixFilterView(query);
		boolean mapStructureOnly = isTissueMatrixStructureView(query) && !mapDifferentialStructures && !mapStructureFilters;
		boolean mapHighLevelTerms = !mapChildren && !mapStructureOnly && !mapDifferentialStructures && !mapStructureFilters && !mapProfileStructures;

		List<GxdMatrixRow> parentTerms = new ArrayList<GxdMatrixRow>();

		// added for the 'and nowhere else' differential query
		if ( ((childrenOf == null) || (childrenOf.trim().length() == 0))
				&& (query.getAnywhereElse() != null) && (query.getAnywhereElse().trim().length() > 0) ) {

			// If the user asks for 'structure A and not anywhere else', then just show a single matrix row
			// for structure A.  Unless this search has a Detected filter, in which case show the set of 
			// (default) high level terms.
			if ((query.getStructureID() != null) && (query.getStructureID().trim().length() > 0)) {
				List<VocabTerm> terms = vocabFinder.getTermByID(query.getStructureID());
				if ((terms != null) && (terms.size() > 0) && ((query.getDetectedFilter() == null) || (query.getDetectedFilter().size() == 0))) {
					parentTerms.add(makeGxdMatrixRow(terms.get(0)));
				} else {
					// can't find term for structure A -- should not happen, but fall back on the set of
					// high level terms just in case
					mapHighLevelTerms = true;
				}
			} else {
				// no structure specified; show the default set of high level terms in the matrix display
				mapHighLevelTerms = true;
			}
		}
		else if(mapChildren)
		{
			// need to map query to VocabTerm object
			List<VocabTerm> terms = vocabFinder.getTermByID(childrenOf.toUpperCase());
			if(terms.size()==0)
			{
				logger.warn("Could not find database object for structure ID: "+childrenOf);
			}
			else
			{
				VocabTerm term = terms.get(0);
				if(term.getChildren().size()==0)
				{
					logger.warn("Could not find children for structure ID: "+childrenOf);
				}

				for(VocabTerm child : sortTerms(term.getChildren()))
				{
					parentTerms.add(makeGxdMatrixRow(child));
				}
			}
		}
		else if(mapStructureFilters)
		{

			List<VocabTerm> terms = vocabFinder.getTermsByID(query.getStructureIDFilter());
			if(terms.size()==0)
			{
				mapHighLevelTerms=true; // revert to normal high level emapa terms
				logger.warn("Could not find database object for structure ID filters: "+query.getStructureIDFilter());
			}
			else
			{
				for(VocabTerm term : sortTerms(terms))
				{
					parentTerms.add(makeGxdMatrixRow(term));
				}
			}
		}
		else if (mapProfileStructures) 
		{
			for (String termID : query.getProfileStructureID()) 
			{
				if (termID == null || termID.equals("")) continue;

				List<VocabTerm> pterms = vocabFinder.getTermByID(termID.toUpperCase());
				for(VocabTerm pt : sortTerms(pterms))
				{
					parentTerms.add(makeGxdMatrixRow(pt));
				}
			}
		}
		else if(mapStructureOnly || mapDifferentialStructures)
		{
			// need to map query to VocabTerm object
			List<VocabTerm> terms = new ArrayList<VocabTerm>();
			if (query.getStructureID() != null && !query.getStructureID().equals(""))
			{
				terms = vocabFinder.getTermByID(query.getStructureID().toUpperCase());
			}
			else
			{
				logger.debug("getParentTermsToDisplay-> resolving term by annotated structure key");
				VocabTerm term = vocabFinder.getTermByKey(query.getAnnotatedStructureKey());
				if (term == null)
				{
					logger.warn("Could not find database object for annotatedStructureKey: "+query.getAnnotatedStructureKey());
				}
				else
				{
					terms.add(term);
				}
			}
			
			if(terms.size()==0)
			{
				mapHighLevelTerms=true; // revert to normal high level emapa terms
				logger.warn("Could not find database object for structure ID: "+query.getStructureID());
			}
			else
			{
				for(VocabTerm term : sortTerms(terms))
				{
					if("EMAPS".equalsIgnoreCase(term.getVocabName()))
					{
						// we need to convert any EMAPS IDs to EMAPA ids or else all hell will break loose.
						term = term.getEmapInfo().getEmapaTerm();
					}
					GxdMatrixRow parentRow = makeGxdMatrixRow(term);
					parentTerms.add(parentRow);
					if(mapStructureOnly)
					{
						parentRow.setOc(OpenCloseState.OPEN);
						// for structure only queries we expand the children one level
						for(VocabTerm child : sortTerms(term.getChildren()))
						{
							parentRow.addChild(makeGxdMatrixRow(child));
						}
					}
				}
				if(mapDifferentialStructures
						&& !query.getDifStructureID().equals(query.getStructureID()))
				{
					// need to map query to VocabTerm object for the differential structure
					List<VocabTerm> difTerms = vocabFinder.getTermByID(query.getDifStructureID().toUpperCase());
					if(difTerms.size()==0)
					{
						mapHighLevelTerms=true; // revert to normal high level emapa terms
						logger.warn("Could not find database object for differential structure ID: "+query.getDifStructureID());
					}
					else
					{
						for(VocabTerm difTerm : sortTerms(difTerms))
						{
							parentTerms.add(makeGxdMatrixRow(difTerm));
						}
					}
				}
			}
		}


		if(mapHighLevelTerms)
		{
			parentTerms = getHighLevelTerms();
		}
		
		// open any other default rows
		rowOpener.openRows(parentTerms,pathsToOpen);
		
		return parentTerms;
	}

	public boolean isTissueMatrixStructureView(GxdQueryForm query)
	{
		return (query.getStructureID()!=null && !query.getStructureID().equals(""))
				|| (query.getAnnotatedStructureKey()!=null && !query.getAnnotatedStructureKey().equals(""));
	}
	public boolean isTissueMatrixDifferentialView(GxdQueryForm query)
	{
		return query.getStructureID()!=null && !query.getStructureID().equals("")
				&& query.getDifStructureID()!=null && !query.getDifStructureID().equals("");
	}
	public boolean isMatrixProfileView(GxdQueryForm query)
	{
		return query.getProfileFormMode() != null && !query.getProfileFormMode().equals("");
	}
	public boolean isTissueMatrixFilterView(GxdQueryForm query)
	{
		return query.getStructureIDFilter()!= null && query.getStructureIDFilter().size() > 0;
	}

	public List<GxdMatrixRow> assignOpenCloseState(List<GxdMatrixRow> matrixRows, GxdQueryForm query, List<SolrDagEdge> edges)
	{
		matrixRows = this.getFlatTermList(matrixRows,null);
		
		// need to set open/close state
		logger.debug("assignOpenCloseState-> querying for DAG edges");
		//List<SolrDagEdge> edges = getDAGDirectRelationships(query,matrixRows).getResultObjects();
		Set<String> rowsWithChildren = new HashSet<String>();
		for(SolrDagEdge edge : edges)
		{
			//logger.debug("row with children="+edge);
			rowsWithChildren.add(edge.getParentId());
		}
		logger.debug("assignOpenCloseState-> querying for list of exact matching EMAPA IDs");
		//Set<String> rowsWithData = getExactStructureIds(query);


		List<GxdMatrixRow> finalParentTerms = new ArrayList<GxdMatrixRow>();
		for(GxdMatrixRow parentTerm : matrixRows)
		{
			// skip if term is already set to open
			if(parentTerm.getOc().equals(OpenCloseState.OPEN.getState()))
			{
				finalParentTerms.add(parentTerm);
				continue;
			}

			if(rowsWithChildren.contains(parentTerm.getRid()))
			{
				parentTerm.setEx(true);
			}
			else
			{
				continue;
			}
			finalParentTerms.add(parentTerm);
		}
		logger.debug("assignOpenCloseState-> done assigning open/close states");
		return finalParentTerms;
	}

	public void addMapChildrenOfFilterForMatrix(GxdQueryForm query, String mapChildrenOf)
	{
		// get child IDs
		List<VocabTerm> terms = vocabFinder.getTermByID(mapChildrenOf);
		if(terms.size() == 0)
		{
			logger.warn("could not find vocab term (for mapChildrenOf) ID = "+mapChildrenOf);
		}
		else
		{
			VocabTerm term = terms.get(0);
			List<VocabTerm> children = term.getChildren();
			if(children.size() == 0)
			{
				logger.warn("could not find children rows to map for ID = "+mapChildrenOf);
			}
			for(VocabTerm child : children)
			{
				query.getMatrixStructureId().add(child.getPrimaryID());
			}
		}
	}
	
	public List<GxdMatrixRow> getFlatTermList(List<GxdMatrixRow> rows)
	{
		return getFlatTermList(rows,new HashSet<String>());
	}
	
	public List<GxdMatrixRow> getFlatTermList(List<GxdMatrixRow> rows, Set<String> uniqueRows)
	{
		List<GxdMatrixRow> flatRows = new ArrayList<GxdMatrixRow>();
		for(GxdMatrixRow row : rows)
		{
			boolean addRow = true;
			if(uniqueRows!=null)
			{
				if(uniqueRows.contains(row.getRid()))
				{
					addRow = false;
				}
				uniqueRows.add(row.getRid());
			}
			if (addRow)
			{
				flatRows.add(row);
			}
			flatRows.addAll(getFlatTermList(row.getChildren(),uniqueRows));
		}

		return flatRows;
	}

	public List<GxdMatrixRow> pruneEmptyRows(List<GxdMatrixRow> rows, Set<String> idsWithData)
	{
		List<GxdMatrixRow> finalRows = new ArrayList<GxdMatrixRow>();
		for(GxdMatrixRow row : rows)
		{
			// skip rows with no data
			if(!idsWithData.contains(row.getRid())) continue;

			finalRows.add(row);
			pruneEmptyChildrenRecurse(row,idsWithData);
		}
		return finalRows;
	}
	public void pruneEmptyChildrenRecurse(GxdMatrixRow row, Set<String> idsWithData)
	{
		List<GxdMatrixRow> children = row.getChildren();
		List<Integer> indicesToRemove = new ArrayList<Integer>();
		for(int i=0;i<children.size();i++)
		{
			GxdMatrixRow child = children.get(i);
			if(!idsWithData.contains(child.getRid()))
			{
				// remove child somehow
				indicesToRemove.add(i);
				continue;
			}
			pruneEmptyChildrenRecurse(child,idsWithData);
		}
		// now remove the child
		int counter=0;
		for(Integer index : indicesToRemove)
		{
			children.remove(index - counter);
			counter += 1;
		}
	}
	
    

    /*
     * Exposed methods for testing purposes
     */
    public void setVocabFinder(VocabularyFinder  vocabFinder)
    {
    	this.vocabFinder = vocabFinder;
    }
    
    public void setRowOpener(DagMatrixRowOpener rowOpener)
    {
    	this.rowOpener = rowOpener;
    }
}
