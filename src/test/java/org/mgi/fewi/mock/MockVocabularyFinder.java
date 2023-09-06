package org.mgi.fewi.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.finder.VocabularyFinder;


/**
 * Mock a vocabulary finder using a map for term lookups
 */
public class MockVocabularyFinder extends VocabularyFinder {
	Map<String,VocabTerm> lookup = new HashMap<String, VocabTerm>();
	public MockVocabularyFinder (Map<String,VocabTerm> termLookup)
	{
		super();
		lookup=termLookup;
	}
	
	// GXDhandler looks up terms by ID to generate DAGs for the Tissue Matrices
	@Override
	public List<VocabTerm> getTermByID(String termID)
	{
		List<VocabTerm> terms = new ArrayList<VocabTerm>();
		if(lookup.containsKey(termID))
		{
			terms.add(lookup.get(termID));
		}
		return terms;
	}
	
	@Override
	public List<VocabTerm> getTermsByID(List<String> termIDs)
	{
		List<VocabTerm> terms = new ArrayList<VocabTerm>();
		for(String termID : termIDs)
		{
			List<VocabTerm> term = this.getTermByID(termID);
			if(term != null)
			{
				terms.addAll(term);
			}
		}
		return terms;
	}
	
	@Override
	public VocabTerm getTermByKey(String key)
	{
		if(lookup.containsKey(key))
		{
			return lookup.get(key);
		}
		return null;
	}
}
