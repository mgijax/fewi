package org.jax.mgi.fewi.test.mock;


/**
 * Represents a JSON summary object that was returned from a controller's response. 
 * Only defined the fields we need to test
 * 
 * @author kstone
 *
 */
public class MockRecombinaseSummary
{
	private Integer totalCount;
	private String alleleSymbol;
	
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public String getAlleleSymbol() {
		return alleleSymbol;
	}
	public void setAlleleSymbol(String alleleSymbol) {
		this.alleleSymbol = alleleSymbol;
	}
	
	
}