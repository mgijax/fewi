package org.jax.mgi.fewi.searchUtil.entities;

import java.io.Serializable;

import org.jax.mgi.snpdatamodel.document.ESEntity;

public class ESAggStringCount extends ESEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String key;
	private long count;

	public ESAggStringCount(String key, long count) {
		this.key = key;
		this.count = count;
	}

	@Override
	public String toString() {
		return "ESAggStringCount: " +  key + ":" + count;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
