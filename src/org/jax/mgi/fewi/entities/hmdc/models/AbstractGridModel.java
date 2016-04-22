package org.jax.mgi.fewi.entities.hmdc.models;

import org.jax.mgi.fewi.entities.hmdc.visitors.GridVisitorInterface;

public abstract class AbstractGridModel {
	public abstract void Accept(GridVisitorInterface pi);
}