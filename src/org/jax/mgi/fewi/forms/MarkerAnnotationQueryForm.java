package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Reference;

/*----------------------------------------------------------------------------------*/
/* class: represents the query parameters that can be used with a marker annotation */
/*----------------------------------------------------------------------------------*/

public class MarkerAnnotationQueryForm {

	private String referenceKey;
    private String mrkKey;
    private String vocab;
    private String restriction;
	private List<String> categoryFilter = new ArrayList<String>();
	private List<String> evidenceFilter = new ArrayList<String>();
	private List<String> inferredFilter = new ArrayList<String>();
	private List<String> referenceFilter = new ArrayList<String>();

    public String getReferenceKey() {
        return referenceKey;
    }
    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }
    public String getMrkKey() {
        return mrkKey;
    }
    public void setMrkKey(String mrkKey) {
        this.mrkKey = mrkKey;
    }
    public String getVocab() {
        return vocab;
    }
    public void setVocab(String vocab) {
        this.vocab = vocab;
    }
    public String getRestriction() {
        return restriction;
    }
    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }
    public List<String> getCategoryFilter() {
		return categoryFilter;
	}
	public void setCategoryFilter(List<String> categoryFilter) {
		this.categoryFilter = categoryFilter;
	}
	public List<String> getEvidenceFilter() {
		return evidenceFilter;
	}
	public void setEvidenceFilter(List<String> evidenceFilter) {
		this.evidenceFilter = evidenceFilter;
	}
	public List<String> getInferredFilter() {
		return inferredFilter;
	}
	public void setInferredFilter(List<String> inferredFilter) {
		this.inferredFilter = inferredFilter;
	}
	public List<String> getReferenceFilter() {
		return referenceFilter;
	}
	public void setReferenceFilter(List<String> referenceFilter) {
		this.referenceFilter = referenceFilter;
	}

    @Override
    public String toString() {
        return "MarkerAnnotationQueryForm ["
            + (mrkKey != null ? "mrkKey=" + mrkKey + ", " : "")
            + (vocab != null ? "vocab=" + vocab + ", " : "")
            + "]";
    }

}
