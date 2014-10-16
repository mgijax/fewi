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
    public SolrJoinMapper(String toIndexUrl,String toKey,String fromIndex,String fromKey) {
        this.toIndexUrl = toIndexUrl;
        this.toKey = toKey;
        this.fromIndex = fromIndex;
        this.fromKey = fromKey;
    }
    
    // Include Optional filter string for the joinTo index
    public SolrJoinMapper(String toIndexUrl,String toKey,String fromIndex,String fromKey,String childFilter) {
        this.toIndexUrl = toIndexUrl;
        this.toKey = toKey;
        this.fromIndex = fromIndex;
        this.fromKey = fromKey;
        this.toFilter = childFilter;
    }
    
    public String getToIndexUrl()
    {
    	return this.toIndexUrl;
    }
    
    /**
     * Get the join clause to append to the solr query
     */

    public String getJoinClause(String queryString) 
    {
    	return getJoinClause(queryString,null);
    }
    public String getJoinClause(String queryString,String extraJoinClause) 
    {
    	boolean toFilterEmpty = this.toFilter==null || this.toFilter.trim().equals("");
    	boolean extraJoinClauseEmpty = extraJoinClause==null || extraJoinClause.trim().equals("");
    	
    	if(toFilterEmpty && extraJoinClauseEmpty)
    	{
    		return "{!join fromIndex="+fromIndex+
    			" from="+fromKey+
    			" to="+toKey+"} "+queryString;
    	}
    	String finalToFilter = "";
    	if(toFilterEmpty) finalToFilter = extraJoinClause;
    	else if(extraJoinClauseEmpty) finalToFilter = this.toFilter;
    	else finalToFilter = this.toFilter + " AND "+extraJoinClause;
    	
    	// else do the nested query format to include a filter on the toIndex
    	return "_query_:\"{!join fromIndex="+fromIndex+
    			" from="+fromKey+
    			" to="+toKey+
    			" v='"+queryString.replace("\"","\\\"")+"'}\" AND "+finalToFilter;
    }
}
