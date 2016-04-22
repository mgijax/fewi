package org.jax.mgi.fewi.entities.hmdc.visitors;

import org.jax.mgi.fewi.entities.hmdc.models.GridCluster;
import org.jax.mgi.fewi.entities.hmdc.models.GridResult;
import org.jax.mgi.fewi.entities.hmdc.models.GridRow;
import org.jax.mgi.fewi.entities.hmdc.models.GridTermHeaderAnnotation;

public interface GridVisitorInterface {

	void Visit(GridCluster gridCluster);
	void Visit(GridResult gridResult);
	void Visit(GridRow gridRow);
	void Visit(GridTermHeaderAnnotation gridTermHeaderAnnotation);
	
}
