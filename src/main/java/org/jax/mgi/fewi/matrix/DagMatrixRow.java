package org.jax.mgi.fewi.matrix;

import org.jax.mgi.fe.datamodel.VocabTerm;


/**
 * This is the implementation of the AbstractMatrixDagRow base class. 
 * It is the version you instantiate, but please don't edit this class.
 */
public class DagMatrixRow extends AbstractDagMatrixRow<DagMatrixRow>{
	
	public DagMatrixRow makeMatrixRow(VocabTerm term)
	{
		DagMatrixRow mr = new DagMatrixRow();
		mr.setRid(term.getPrimaryId());
		mr.setTerm(term.getTerm());
		return mr;
	}
}
