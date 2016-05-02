package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.org.mgi.shr.fe.util.GridMarker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalGridHunter extends SolrHunter<SolrHdpEntityInterface> {

	private ObjectMapper mapper = new ObjectMapper();

	public SolrDiseasePortalGridHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		
        propertyMap.put(SearchConstants.ALL_PHENOTYPE, new SolrPropertyMapper(Arrays.asList(IndexConstants.ALL_PHENO_ID, IndexConstants.ALL_PHENO_TEXT),"OR"));
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		List<String> keys = new ArrayList<String>();
		
		for (SolrDocument doc : sdl) {
			
			SolrHdpGridEntry gridResult = new SolrHdpGridEntry();
			gridResult.setAllelePairs((String)doc.getFieldValue(DiseasePortalFields.ALLELE_PAIRS));
			gridResult.setGenoClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GENO_CLUSTER_KEY));
			gridResult.setGridClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GRID_CLUSTER_KEY));
			gridResult.setGridKey((Integer)doc.getFieldValue(DiseasePortalFields.GRID_KEY));
			gridResult.setHomologyClusterKey(((List<Integer>)doc.getFieldValue(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY)).get(0));
			
			TypeFactory typeFactory = TypeFactory.defaultInstance();
			
			List<GridMarker> gridHumanSymbols = new ArrayList<GridMarker>();
			String humanSymbols = (String)doc.getFieldValue(DiseasePortalFields.GRID_HUMAN_SYMBOLS);
			
			try {
				if(humanSymbols != null && !humanSymbols.equals("")) {
					gridHumanSymbols = mapper.readValue(humanSymbols, typeFactory.constructCollectionType(ArrayList.class, GridMarker.class));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			gridResult.setGridHumanSymbols(gridHumanSymbols);
						
			List<GridMarker> gridMouseSymbols = new ArrayList<GridMarker>();
			String mouseSymbols = (String)doc.getFieldValue(DiseasePortalFields.GRID_MOUSE_SYMBOLS);

			try {
				if(mouseSymbols != null && !mouseSymbols.equals("")) {
					gridMouseSymbols = mapper.readValue(mouseSymbols, typeFactory.constructCollectionType(ArrayList.class, GridMarker.class));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			gridResult.setGridMouseSymbols(gridMouseSymbols);

			sr.addResultObjects(gridResult);
			keys.add(gridResult.getGridKey().toString());
		}

		if (keys != null) {
			sr.setResultKeys(keys);
		}
	}

	@Value("${solr.dp.grid.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}