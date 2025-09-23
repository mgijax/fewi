package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


@Repository
public class ESGxdResultHunter extends ESGxdSummaryBaseHunter<BaseESDocument> {
	/***
	 * The constructor sets up this hunter so that it is specific to sequence
	 * summary pages. Each item in the constructor sets a value that it has
	 * inherited from its superclass, and then relies on the superclass to
	 * perform all of the needed work via the hunt() method.
	 */
	public ESGxdResultHunter() {
		super(BaseESDocument.class);
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to actually get a
		 * specific field, and return it rather than a list of keys.
		 */
		keyString = GxdResultFields.RESULT_KEY;
	}
	
	@Value("${es.gxdresult.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}	
}