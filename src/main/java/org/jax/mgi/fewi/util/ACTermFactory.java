package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fe.datamodel.VocabTermSynonym;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;

/* Does: takes a VocabTerm object and converts it to its respective ACTerm objects for use by an ACHelper.
 */
public class ACTermFactory {
	private Set<String> emapaIDCache = new HashSet<String>();
	
	public List<ACTerm> getACTerms(VocabTerm term) {
		List<ACTerm> terms = new ArrayList<ACTerm>();
		
		// add an entry for the term itself
		if (term.getTerm() != null) {
			terms.add(new ACTerm(term, true, term.getTerm()));
		}
		
		// add an entry for each of its synonyms
		if (term.getSynonyms() != null) {
			for (VocabTermSynonym synonym : term.getSynonyms()) {
				terms.add(new ACTerm(term, false, synonym.getSynonym()));
			}
		}
		
		return terms;
	}

	public List<ACTerm> getACTerms(EmapaACResult emapaTerm) {
		List<ACTerm> terms = new ArrayList<ACTerm>();
		
		// add an entry for the term itself (but only once, as EmapaACResults are already denormalized)
		String accID = emapaTerm.getAccID();
		
		// If this is an entry for the term itself, then we index it without any synonyms first.
		if (!emapaTerm.getIsStrictSynonym()) {
			if (emapaTerm.getStructure() != null) {
				terms.add(new ACTerm(emapaTerm, true, emapaTerm.getStructure()));
				emapaIDCache.add(accID);
			}
		}
		
		// add an entry for each of its synonyms
		if (emapaTerm.getIsStrictSynonym() && (emapaTerm.getSynonym() != null)) {
			terms.add(new ACTerm(emapaTerm, false, emapaTerm.getSynonym()));
		}
		
		return terms;
	}
}
