package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.summary.ImageSummaryRow;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrAlleleImagesByAlleleHunter extends SolrHunter<ImageSummaryRow>
{
  /***
   * The constructor sets up this hunter so that it is specific to finding
   * a image key given an allele key
   */
  public SolrAlleleImagesByAlleleHunter() {

    /*
     * Setup the property map.  This maps from the properties of the incoming
     * filter list to the corresponding field names in the Solr implementation.
     */
    propertyMap.put(SearchConstants.ALL_KEY, new SolrPropertyMapper(IndexConstants.ALL_KEY));
    propertyMap.put(SearchConstants.IMG_IS_THUMB, new SolrPropertyMapper(IndexConstants.IS_THUMB));
    propertyMap.put(SearchConstants.IMG_CLASS, new SolrPropertyMapper(IndexConstants.IMAGE_CLASS));

    /*
     * The name of the field we want to iterate through the documents for
     * and place into the output.  In this case we want the standard list of
     * object keys returned.
     */
    keyString = IndexConstants.IMAGE_KEY;

  }


  /***
   * Overridding to add additional filters for this specific request.
   */
  protected SearchParams preProcessSearchParams(SearchParams searchParams) {

    // filter list to collect pre-existing and new filters
    List<Filter> filterList = new ArrayList<Filter> ();

    // add pre-existing filters
    filterList.add(searchParams.getFilter());

    // additional filter for image class
    Filter phenotype = new Filter(SearchConstants.IMG_CLASS, "Phenotypes", Filter.Operator.OP_EQUAL);
    filterList.add(phenotype);

    // additional filter thumbnails
    Filter isThumb = new Filter(SearchConstants.IMG_IS_THUMB, "0", Filter.Operator.OP_EQUAL);
    filterList.add(isThumb);

    // container filter -- 'AND' the old and new filters together
    Filter containerFilter = new Filter();
    containerFilter.setNestedFilters(filterList);
    containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);

    // reset the filters for this search with the new filer container
    searchParams.setFilter(containerFilter);

    return searchParams;

  }


  @Value("${solr.image.url}")
  public void setSolrUrl(String solrUrl) {
    super.solrUrl = solrUrl;
    }
}