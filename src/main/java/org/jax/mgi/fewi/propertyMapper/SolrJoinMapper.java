package org.jax.mgi.fewi.propertyMapper;


/**
 * The SolrPropertyMapper class handles the mapping of operators being passed in from the 
 * hunter, and mapping them specifically to Solr. 
 * 
 * It handles the special case of joining one index to another.
 * We need to define the mapping
 * 
 * @author kstone
 *
 */

public class SolrJoinMapper {
	
	private String toIndexUrl="";
	private String toKey="";
	private String fromIndex="";
	private String fromKey="";
	private String toFilter ="";
   
	/**
	 * Define the join mapping
	 * 
	 *  - "to" is the index with documents we are returning
	 *  - "from" is the index we are querying
	 *  
	 * @param toIndexUrl
	 * @param toIndex
	 * @param toKey
	 * @param fromIndex
	 * @param fromKey
	 */
	public SolrJoinMapper(String toIndexUrl, String toKey, String fromIndex, String fromKey) {
		this.toIndexUrl = toIndexUrl;
		this.toKey = toKey;
		this.fromIndex = fromIndex;
		this.fromKey = fromKey;
	}
	
	// Include Optional filter string for the joinTo index
	public SolrJoinMapper(String toIndexUrl, String toKey, String fromIndex, String fromKey, String childFilter) {
		this.toIndexUrl = toIndexUrl;
		this.toKey = toKey;
		this.fromIndex = fromIndex;
		this.fromKey = fromKey;
		this.toFilter = childFilter;
	}
	
	public String getToIndexUrl() {
		return this.toIndexUrl;
	}
	
	/**
	 * Get the join clause to append to the solr query
	 */

	public String getJoinClause(String queryString, String extraJoinClause) {
		boolean toFilterEmpty = toFilter==null || toFilter.trim().equals("");
		boolean extraJoinClauseEmpty = extraJoinClause==null || extraJoinClause.trim().equals("");
		
		if(toFilterEmpty && extraJoinClauseEmpty) {
			return "{!join fromIndex=" + fromIndex + " from=" + fromKey + " to=" + toKey + "} " + queryString;
		}
		String finalToFilter = "";
		if(toFilterEmpty) finalToFilter = extraJoinClause;
		else if(extraJoinClauseEmpty) finalToFilter = toFilter;
		else finalToFilter = toFilter + " AND " + extraJoinClause;
		
		// else do the nested query format to include a filter on the toIndex
		queryString = queryString.replace("\"","\\\"");
		// This takes care of searching for
		// 5'-phosphate which crashed the server before
		queryString = queryString.replace("'","\\\\'");
		String ret = "_query_:\"{!join fromIndex=" + fromIndex + " from=" + fromKey + " to=" + toKey + " v='" + queryString + "'}\" AND " + finalToFilter;
		return ret;
	}
}
