package org.jax.mgi.fewi.searchUtil.entities;

/*
 * wrapper on a string object to appease hunter Generics
 */
public class SolrString implements SolrHdpEntity
{
	private String string;
	public SolrString(String string)
	{
		this.string=string;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
}
