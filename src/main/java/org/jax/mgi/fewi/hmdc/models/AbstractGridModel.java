package org.jax.mgi.fewi.hmdc.models;

import org.jax.mgi.fewi.hmdc.visitors.GridVisitorInterface;

public abstract class AbstractGridModel {
	public abstract void Accept(GridVisitorInterface pi);
}