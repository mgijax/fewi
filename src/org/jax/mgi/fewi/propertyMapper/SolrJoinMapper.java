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
    
    public String getToIndexUrl()
    {
    	return this.toIndexUrl;
    }
    
    /**
     * Get the join clause to append to the solr query
     */

    public String getJoinClause() 
    {
    	return "{!join fromIndex="+fromIndex+
    			" from="+fromKey+
    			" to="+toKey+"} ";
    }
}
