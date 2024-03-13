package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;

/*
 * wrapper on a string object to appease hunter Generics
 */
public class SolrString implements SolrHdpEntityInterface,SolrGxdEntity
{
	private final String string;
	public SolrString(String string)
	{
		this.string=string;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
}
