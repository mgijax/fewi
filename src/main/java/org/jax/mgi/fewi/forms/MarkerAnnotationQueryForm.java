package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;

/*----------------------------------------------------------------------------------*/
/* class: represents the query parameters that can be used with a marker annotation */
/*----------------------------------------------------------------------------------*/

public class MarkerAnnotationQueryForm {

	private String referenceKey;
    private String mrkKey;
    private String vocab;
    private String restriction;
    private String header;
    private String goID;
	private List<String> aspectFilter = new ArrayList<String>();
	private List<String> evidenceFilter = new ArrayList<String>();
	private List<String> inferredFilter = new ArrayList<String>();
	private List<String> referenceFilter = new ArrayList<String>();
	private List<String> categoryFilter = new ArrayList<String>();

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
    public String getGoID() {
        return goID;
    }
    public void setGoID(String goID) {
        this.goID = goID;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getRestriction() {
        return restriction;
    }
    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }
    public List<String> getAspectFilter() {
		return aspectFilter;
    }
    public void setAspectFilter(List<String> aspectFilter) {
		this.aspectFilter = aspectFilter;
    }
    public List<String> getEvidenceFilter() {
	/* need to convert any left parentheses back into commas (they
	 * were tweaked in JS to avoid appearing like mulitiple values)
	 */
	List<String> tweaked = new ArrayList<String>();

	for (String s : this.evidenceFilter) {
	    tweaked.add(s.replace("(", ","));
	}

	return tweaked;
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
	public List<String> getCategoryFilter() {
		return categoryFilter;
	}
	public void setCategoryFilter(List<String> categoryFilter) {
		this.categoryFilter = categoryFilter;
	}

    @Override
    public String toString() {
        return "MarkerAnnotationQueryForm ["
            + (mrkKey != null ? "mrkKey=" + mrkKey + ", " : "")
            + (vocab != null ? "vocab=" + vocab + ", " : "")
            + "]";
    }

}
