package org.jax.mgi.fewi.searchUtil.entities;

/*
 * Result objects need to implement this for certain optional methods to work.
 */
public interface UniqueableObject
{
	public Object getUniqueKey();
}