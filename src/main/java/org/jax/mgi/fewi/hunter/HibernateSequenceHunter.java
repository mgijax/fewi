package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.jax.mgi.fe.datamodel.MarkerSequenceAssociation;
import org.jax.mgi.fe.datamodel.ReferenceSequenceAssociation;
import org.jax.mgi.fe.datamodel.Sequence;
import org.jax.mgi.fe.datamodel.SequenceID;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Intended as an alternative to using Solr.
 * The reasoning behind this, is that it takes a good chunk of time to load our Sequence
 * 	Solr index (20+ minutes), yet the queries are incredibly simple, by sequence ID, marker key, or reference key.
 * 
 * This class should provide the same functionality with comparable performance, and no need for a Solr index.
 * 
 * @kstone
 */
@Repository
public class HibernateSequenceHunter implements Hunter<Sequence>
{    
	@Autowired
	private SessionFactory sessionFactory;
	
	/*
	 *  enum for the database field names we can use, based on hibernate prop names.
	 *  	(just makes it easier to use)
	 */
	public enum SeqField 
	{
		SEQ_ID("sequenceId"), 
		REF_KEY("referenceKey"),
		MRK_KEY("markerKey"),
		SEQ_KEY("sequenceKey"),
		PROVIDER("provider"),
		SEQ_TYPE("sequenceType"),
		LENGTH("length"),
		BY_SEQ_TYPE("sequenceNum.bySequenceType"),
		BY_PROVIDER("sequenceNum.byProvider");
 
		private String field;
 
		private SeqField(String field) {
			this.field = field;
		}
 
		public String getField() {
			return field;
		}
	}

	
	// map a SearchConstant to the sequence table DB field (or association) you are sorting on
	protected Map<String,SeqField> propertyMap = new HashMap<String,SeqField>();
	
	// map a SortConstant to the sequence table DB field you are sorting on
	protected Map<String,SeqField> sortMap = new HashMap<String,SeqField>();
	
	/*
	 * init any properties and mappings
	 */
	public HibernateSequenceHunter()
	{
        // Map the search constants
        propertyMap.put(SearchConstants.SEQ_ID, SeqField.SEQ_ID);
        propertyMap.put(SearchConstants.MRK_KEY, SeqField.MRK_KEY);
        propertyMap.put(SearchConstants.REF_KEY, SeqField.REF_KEY);
        propertyMap.put(SearchConstants.SEQ_KEY, SeqField.SEQ_KEY);
        propertyMap.put(SearchConstants.SEQ_PROVIDER, SeqField.PROVIDER);
        
		// Map the sort constants
	    sortMap.put(SortConstants.SEQUENCE_TYPE, SeqField.BY_SEQ_TYPE);
	    sortMap.put(SortConstants.SEQUENCE_PROVIDER, SeqField.BY_PROVIDER);
	    sortMap.put(SortConstants.SEQUENCE_LENGTH, SeqField.LENGTH);
	}
	
	
	public void huntWithoutCount(SearchParams searchParams, SearchResults<Sequence> searchResults)
	{
		this.hunt(searchParams, searchResults,false);
	}
	public void hunt(SearchParams searchParams, SearchResults<Sequence> searchResults)
	{
		this.hunt(searchParams,searchResults,true);
	}
	@SuppressWarnings("unchecked")
	public void hunt(SearchParams searchParams, SearchResults<Sequence> searchResults,boolean doCount)
	{
		Session s = sessionFactory.getCurrentSession();
		
		/*
		 *  gather up all the selected filters 
		 *  (we assume they are all '=' operators
		 *  	and that they are all ANDed together)
		 *  
		 *  This assumption holds true in the SequenceController as of 2013/10/17
		 *  -kstone
		 */
		Map<SeqField,String> selectedFilters = new HashMap<SeqField,String>();
		gatherSelectedFilters(searchParams.getFilter(),selectedFilters);
		
		if(selectedFilters.keySet().size() < 1)
		{
			// No filters selected, just bail. We don't want to bring back the whole DB.
			return;
		}
	
    	Criteria query = s.createCriteria(Sequence.class);
    	// apply any filters that apply to the sequence table
    	if(selectedFilters.containsKey(SeqField.SEQ_KEY))
    	{
    		int seqKey = Integer.parseInt(selectedFilters.get(SeqField.SEQ_KEY));
    		query.add(Restrictions.eq("sequenceKey",seqKey));
    	}
    	if(selectedFilters.containsKey(SeqField.PROVIDER))
    	{
    		String provider = selectedFilters.get(SeqField.PROVIDER);
    		if("uniprot".equalsIgnoreCase(provider))
    		{
    			// we need to map TreMBL and SWISS-PROT to UniProt
    			query.add(Restrictions.in("provider",Arrays.asList("TrEMBL","SWISS-PROT","UniProt")));
    		}
    		else
    		{
    			query.add(Restrictions.eq("provider",provider));
    		}
    	}
    	
    	// now apply any filters related to associations
    	if(selectedFilters.containsKey(SeqField.MRK_KEY))
    	{
    		String markerKey = selectedFilters.get(SeqField.MRK_KEY);
    		addMarkerKeyCriteria(query,markerKey);
    	}
    	if(selectedFilters.containsKey(SeqField.REF_KEY))
    	{
    		String referenceKey = selectedFilters.get(SeqField.REF_KEY);
    		addReferenceKeyCriteria(query,referenceKey);
    	}
    	if(selectedFilters.containsKey(SeqField.SEQ_ID))
    	{
    		String sequenceId = selectedFilters.get(SeqField.SEQ_ID);
    		addSequenceIdCriteria(query,sequenceId);
    	}
    	
    	// get the total count
    	if(doCount)
    	{
    		int totalCount = getTotalCount(query);
    		searchResults.setTotalCount(totalCount);
    	}
    	
    	// add any sorts
    	applySortsToCriteria(query,searchParams);
    	
    	//add the pagination
    	applyPaginationToCriteria(query,searchParams);
    	
    	// now run the query to get the results
    	List<Sequence> sequences = query.list();
    	
    	List<String> resultKeys = new ArrayList<String>();
    	
    	// iterate to collect the keys
    	for(Sequence sequence : sequences)
    	{
    		resultKeys.add(""+sequence.getSequenceKey());
    	}
    	
    	// add the result objects
    	searchResults.setResultKeys(resultKeys);
    	searchResults.setResultObjects(sequences);
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
	 * Gets sequences for a marker directly off the MarkerSequenceAssociation (i.e. marker_to_sequence table)
	 */
	protected Criteria addMarkerKeyCriteria(Criteria query, String markerKey)
	{
    	/***Initialize an instance of MarkerSequenceAssociation Class with the properties that you want to match***/
    	MarkerSequenceAssociation msi = new MarkerSequenceAssociation();
    	msi.setMarkerKey(Integer.parseInt(markerKey));
    	Example mse = Example.create(msi);
    	
    	// now apply the marker specific filter
    	query.createCriteria("markerAssociations",JoinType.INNER_JOIN)
        	    .add(mse);
    	return query;
	}
	
	/*
	 * Gets sequences for a reference directly off the ReferenceSequenceAssociation (i.e. reference_to_sequence table)
	 */
	protected Criteria addReferenceKeyCriteria(Criteria query, String referenceKey)
	{
    	/***Initialize an instance of ReferenceSequenceAssociation Class with the properties that you want to match***/
		ReferenceSequenceAssociation rsi = new ReferenceSequenceAssociation();
    	rsi.setReferenceKey(Integer.parseInt(referenceKey));
    	Example rse = Example.create(rsi);
    	
    	// now apply the reference specific filter
    	query.createCriteria("referenceAssociations",JoinType.INNER_JOIN)
        	    .add(rse);
    	return query;
	}
	
	/*
	 * Gets sequences for an ID directly off the SequenceId (i.e. sequence_id table)
	 */
	protected Criteria addSequenceIdCriteria(Criteria query, String sequenceId)
	{
    	/***Initialize an instance of SequenceId Class with the properties that you want to match***/
    	SequenceID sii = new SequenceID();
    	sii.setAccID(sequenceId);
    	//sii.setPreferred(null);
    	Example sie = Example.create(sii);
    	
    	// now apply the sequence ID specific filter
    	query.createCriteria("ids",JoinType.INNER_JOIN)
        	    .add(sie);
    	return query;
	}
	
	/*
	 * Add the necessary order by clauses
	 */
	protected Criteria applySortsToCriteria(Criteria query, SearchParams searchParams)
	{
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
				
				// the "length" field contains nulls, and we want them to sort lowest
				if(sortField.equalsIgnoreCase("length")) order = order.nulls(nullPrecedence);
				
				query.addOrder(order);
			}
			
		}
		return query;
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
	protected void gatherSelectedFilters(Filter filter,Map<SeqField,String> selectedFilters)
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
