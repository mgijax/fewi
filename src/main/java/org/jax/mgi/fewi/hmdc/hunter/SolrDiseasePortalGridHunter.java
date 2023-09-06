package org.jax.mgi.fewi.hmdc.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.hunter.SolrHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.jsonmodel.GridGenocluster;
import org.jax.mgi.shr.jsonmodel.GridMarker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Repository
public class SolrDiseasePortalGridHunter extends SolrHunter<SolrHdpEntityInterface> {

	private ObjectMapper mapper = new ObjectMapper();

	public SolrDiseasePortalGridHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		
		
		// These are added to reduce the amount of data coming back from solr
		// over the wire and speeds up the query.
		// All the search fields are not returned
		returnedFields.add(DiseasePortalFields.ALLELE_PAIRS);
		returnedFields.add(DiseasePortalFields.GENO_CLUSTER_KEY);
		returnedFields.add(DiseasePortalFields.GRID_CLUSTER_KEY);
		returnedFields.add(DiseasePortalFields.GRID_KEY);
		returnedFields.add(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY);
		returnedFields.add(DiseasePortalFields.GRID_HUMAN_SYMBOLS);
		returnedFields.add(DiseasePortalFields.GRID_MOUSE_SYMBOLS);
		returnedFields.add(DiseasePortalFields.MARKER_SYMBOL);
		returnedFields.add(DiseasePortalFields.GENO_CLUSTER_KEY);
		returnedFields.add(DiseasePortalFields.BY_GENOCLUSTER);
		returnedFields.add(DiseasePortalFields.IS_CONDITIONAL);
		returnedFields.add(DiseasePortalFields.IMSR_ALLELES);
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
			gridResult.setHomologyClusterKey((Integer)doc.getFieldValue(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY));
			gridResult.setByGenoCluster((Integer) doc.getFieldValue(DiseasePortalFields.BY_GENOCLUSTER));
			gridResult.setConditional((Integer) doc.getFieldValue(DiseasePortalFields.IS_CONDITIONAL));
			try {
				String gridGenoclusterString = (String)doc.getFieldValue(DiseasePortalFields.IMSR_ALLELES);
				if(gridGenoclusterString != null) {
					gridResult.setGridGenocluster((GridGenocluster) mapper.readValue(gridGenoclusterString, GridGenocluster.class));
				}
			} catch (Exception e) {}

			@SuppressWarnings("unchecked")
			List<String> symbols = (List<String>) doc.getFieldValue(DiseasePortalFields.MARKER_SYMBOL);

			if ((symbols != null) && (symbols.size() > 0)) {
				gridResult.setMarkerSymbol(symbols.get(0));
			}
			
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