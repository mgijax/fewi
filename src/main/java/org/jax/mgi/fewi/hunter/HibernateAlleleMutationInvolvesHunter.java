package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fe.datamodel.AlleleRelatedMarker;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Intended as an alternative to using Solr.  This is patterned after the
 * HibernateSequenceHunter, hoping to avoid creating another Solr index for a
 * fairly simple lookup of AlleleRelatedMarker data.
 */
@Repository
public class HibernateAlleleMutationInvolvesHunter implements Hunter<AlleleRelatedMarker>
{    
	@Autowired
	private SessionFactory sessionFactory;
	
	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(
		HibernateAlleleMutationInvolvesHunter.class);
    
	/*
	 *  enum for the database field names we can use, based on hibernate
	 *  prop names.	(just makes it easier to use)
	 */
	public enum ArmField 
	{
		ARM_KEY("armKey"), 
		REFERENCE_KEY("referenceKey"),
		ALLELE_KEY("alleleKey"),
		SEQUENCE_NUM("sequenceNum");
 
		private String field;
 
		private ArmField(String field) {
			this.field = field;
		}
 
		public String getField() {
			return field;
		}
	}

	
	// map a SearchConstant to the table DB field (or association) you are sorting on
	protected Map<String,ArmField> propertyMap = new HashMap<String,ArmField>();
	
	// map a SortConstant to the table DB field you are sorting on
	protected Map<String,ArmField> sortMap = new HashMap<String,ArmField>();
	
	/*
	 * init any properties and mappings
	 */
	public HibernateAlleleMutationInvolvesHunter()
	{
        // Map the search constants
        propertyMap.put(SearchConstants.ALLELE_KEY, ArmField.ALLELE_KEY);
        
	// Map the sort constants
	sortMap.put(SortConstants.SEQUENCE_NUM, ArmField.SEQUENCE_NUM);
	}
	
	
	public void huntWithoutCount(SearchParams searchParams, SearchResults<AlleleRelatedMarker> searchResults)
	{
		this.hunt(searchParams, searchResults,false);
	}

	public void hunt(SearchParams searchParams, SearchResults<AlleleRelatedMarker> searchResults)
	{
		this.hunt(searchParams,searchResults,true);
	}

	@SuppressWarnings("unchecked")
	public void hunt(SearchParams searchParams,
	    SearchResults<AlleleRelatedMarker> searchResults,
	    boolean doCount)
	{
		Session s = sessionFactory.getCurrentSession();
		
		/*
		 *  gather up all the selected filters 
		 *  (we assume they are all '=' operators
		 *  	and that they are all ANDed together)
		 *  
		 *  This assumption holds true in the AlleleController as of
		 *  June 2014.
		 */
		Map<ArmField,String> selectedFilters = new HashMap<ArmField,String>();
		gatherSelectedFilters(searchParams.getFilter(),selectedFilters);
		
		if(selectedFilters.keySet().size() < 1)
		{
			// No filters selected, just bail. We don't want to
			// bring back the whole DB.
			logger.debug("No filters selected in "
				+ "HibernateAlleleMutationInvolvesHunter");
			return;
		}
	
    	Criteria query = s.createCriteria(AlleleRelatedMarker.class);

    	// apply any filters that apply to the allele_related_marker table
    	if(selectedFilters.containsKey(ArmField.ALLELE_KEY)) {
	    int alleleKey = Integer.parseInt(selectedFilters.get(
		ArmField.ALLELE_KEY));
	    logger.debug("alleleKey: " + alleleKey);
	    query.add(Restrictions.eq("alleleKey", alleleKey));
    	}
    	
    	// get the total count
    	if(doCount)
    	{
		logger.debug("calling getTotalCount");
    		int totalCount = getTotalCount(query);
		logger.debug("returned from getTotalCount: " + totalCount);
    		searchResults.setTotalCount(totalCount);
    	}
    	
    	// add any sorts
    	applySortsToCriteria(query,searchParams);
    	
    	//add the pagination
    	applyPaginationToCriteria(query,searchParams);
    	
    	// now run the query to get the results
    	List<AlleleRelatedMarker> arms = query.list();
    	
    	List<String> resultKeys = new ArrayList<String>();
    	
    	// iterate to collect the keys
    	for(AlleleRelatedMarker arm : arms)
    	{
    		resultKeys.add(""+arm.getAlleleKey());
    	}
    	
    	// add the result objects
    	searchResults.setResultKeys(resultKeys);
    	searchResults.setResultObjects(arms);
	}
	
	protected int getTotalCount(Criteria query)
	{
		int totalCount = ((Number) query.setProjection(Projections.rowCount()).uniqueResult()).intValue();
		
		// need to reset the criteria to before we did the count
		query.setProjection(null);
		query.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		
		return totalCount;
	}
	
	/*
	 * Add the necessary order by clauses
	 */
	protected Criteria applySortsToCriteria(Criteria query, SearchParams searchParams)
	{
		Order order = Order.asc(SortConstants.SEQUENCE_NUM);
		query.addOrder(order);
		return query;
/*
		List<Sort> sorts = searchParams.getSorts();
		if(sorts==null) return query;
		
		// join sequenceNum so we have access to it
		query.createAlias("sequenceNum","sequenceNum");
		
		for(Sort sort : sorts)
		{
			String sortConstant = sort.getSort();
			if(this.sortMap.containsKey(sortConstant))
			{
				String sortField = sortMap.get(sortConstant).getField();

				Order order = Order.asc(sortField);
				NullPrecedence nullPrecedence = NullPrecedence.FIRST;
				if(sort.isDesc())
				{
					order = Order.desc(sortField);
					nullPrecedence = NullPrecedence.LAST;
				}
				
				query.addOrder(order);
			}
			
		}
		return query;
*/
	}

	private Criteria applyPaginationToCriteria(Criteria query,SearchParams searchParams)
	{
    	query.setFirstResult(searchParams.getStartIndex());
    	query.setMaxResults(searchParams.getPageSize());
    	return query;
	}
	
	
	/*
	 * This is a rather dumb method of mapping filter selections. 
	 * It simply looks at all filters, and assumes all operators are '=',
	 * that every value is a single value,
	 * and that they will be ANDed together
	 */
	protected void gatherSelectedFilters(Filter filter,Map<ArmField,String> selectedFilters)
	{
		String searchConstant = filter.getProperty();
		String value = filter.getValue();
		if(searchConstant!=null && !searchConstant.equals("")
				&& value!=null && !value.equals(""))
		{
			if(this.propertyMap.containsKey(searchConstant))
			{
				selectedFilters.put(propertyMap.get(searchConstant),value);
			}
		}
		if(filter.getNestedFilters()!=null)
		{
			for(Filter nestedFilter : filter.getNestedFilters())
			{
				gatherSelectedFilters(nestedFilter,selectedFilters);
			}
		}
	}
}
