package org.jax.mgi.fewi.hmdc.visitors;

import org.jax.mgi.fewi.hmdc.models.GridCluster;
import org.jax.mgi.fewi.hmdc.models.GridResult;
import org.jax.mgi.fewi.hmdc.models.GridRow;
import org.jax.mgi.fewi.hmdc.models.GridTermHeaderAnnotation;

public interface GridVisitorInterface {

	void Visit(GridCluster gridCluster);
	void Visit(GridResult gridResult);
	void Visit(GridRow gridRow);
	void Visit(GridTermHeaderAnnotation gridTermHeaderAnnotation);
	
}
