package org.jax.mgi.fewi.searchUtil.entities;

import java.io.Serializable;

import org.jax.mgi.snpdatamodel.document.ESEntity;

public class ESAggLongCount extends ESEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private long key;
	private long count;

	public ESAggLongCount(long key, long count) {
		this.key = key;
		this.count = count;
	}

	@Override
	public String toString() {
		return "ESAggLongCount: " + key + ":" + count;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
