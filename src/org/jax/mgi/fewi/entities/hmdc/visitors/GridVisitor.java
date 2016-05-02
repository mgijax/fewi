package org.jax.mgi.fewi.entities.hmdc.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jax.mgi.fewi.entities.hmdc.models.GridCluster;
import org.jax.mgi.fewi.entities.hmdc.models.GridResult;
import org.jax.mgi.fewi.entities.hmdc.models.GridRow;
import org.jax.mgi.fewi.entities.hmdc.models.GridTermHeaderAnnotation;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.searchUtil.PrinterUtil;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.org.mgi.shr.fe.util.GridMarker;

public class GridVisitor extends PrinterUtil implements GridVisitorInterface {

	private GridResult result = null;
	//private List<String> gridMPHeaders = null;
	
	private List<String> gridHighLights = null;
	private List<String> gridMPHeaders = null;
	private List<String> gridOMIMHeaders = null;

	private SolrHdpGridEntry currentGridEntry;
	
	private HashMap<Integer, ArrayList<SolrHdpGridAnnotationEntry>> annotationMap = new HashMap<>();

	private SearchResults<SolrHdpGridEntry> gridResults;
	
	public GridVisitor(SearchResults<SolrHdpGridEntry> gridResults, SearchResults<SolrHdpGridAnnotationEntry> annotationResults) {
		this.gridResults = gridResults;
		
		gridHighLights = new ArrayList<String>();
		gridMPHeaders = new ArrayList<String>();
		gridOMIMHeaders = new ArrayList<String>();
		
		Map<String, List<String>> highLights = gridResults.getResultSetMeta().getSetHighlights();

		for(SolrHdpGridAnnotationEntry ar: annotationResults.getResultObjects()) {
			
			if(highLights.containsKey(ar.getGridKey().toString())) {
				String term = highLights.get(ar.getGridKey().toString()).get(0);

				if(ar.getTerm().equals(term) && !gridHighLights.contains(ar.getTermHeader())) {
					gridHighLights.add(ar.getTermHeader());
				}
			}
			
			if(!gridMPHeaders.contains(ar.getTermHeader()) && "Mammalian Phenotype".equals(ar.getTermType())) {
				gridMPHeaders.add(ar.getTermHeader());
			}
			if(!gridOMIMHeaders.contains(ar.getTermHeader()) && "OMIM".equals(ar.getTermType())) {
				gridOMIMHeaders.add(ar.getTermHeader());
			}
			
			if(!annotationMap.containsKey(ar.getGridKey())) {
				annotationMap.put(ar.getGridKey(), new ArrayList<SolrHdpGridAnnotationEntry>());
			}
			annotationMap.get(ar.getGridKey()).add(ar);
		}
	}

	@Override
	public void Visit(GridResult gridResult) {

		HashMap<Integer, GridRow> rowMap = new HashMap<Integer, GridRow>();
		TreeMap<String, GridRow> sortedMap = new TreeMap<String, GridRow>();
		
		for(SolrHdpGridEntry res: gridResults.getResultObjects()) {
			currentGridEntry = res;
			Integer gridClusterKey = res.getGridClusterKey();
			
			if(!rowMap.containsKey(gridClusterKey)) {
				rowMap.put(gridClusterKey, new GridRow());
			}
			rowMap.get(gridClusterKey).Accept(this);
			
			String index = "";
			for (GridMarker gm: rowMap.get(gridClusterKey).getGridCluster().getHumanSymbols()) {
			    index += gm.getSymbol().toLowerCase();
			}
			for (GridMarker gm: rowMap.get(gridClusterKey).getGridCluster().getMouseSymbols()) {
			    index += gm.getSymbol().toLowerCase();
			}
			sortedMap.put(index, rowMap.get(gridClusterKey));
		}
		
		for(GridRow gr: sortedMap.values()) {
			gridResult.getGridRows().add(gr);
		}
		gridResult.setGridHighLights(gridHighLights);
	}

	@Override
	public void Visit(GridRow gridRow) {
		if(gridRow.getGridCluster() == null) {
			GridCluster gc = new GridCluster();
			gridRow.setGridCluster(gc);
			gc.Accept(this);
		}
		
		if(gridRow.getDiseaseCells() == null) {
			gridRow.setDiseaseCells(new HashMap<String, GridTermHeaderAnnotation>());
		}
		HashMap<String, GridTermHeaderAnnotation> diseaseCells = gridRow.getDiseaseCells();
		
		if(gridRow.getMpHeaderCells() == null) {
			gridRow.setMpHeaderCells(new HashMap<String, GridTermHeaderAnnotation>());
		}
		HashMap<String, GridTermHeaderAnnotation> mpHeaderCells = gridRow.getMpHeaderCells();

		for(SolrHdpGridAnnotationEntry shgae: annotationMap.get(currentGridEntry.getGridKey())) {
			GridTermHeaderAnnotation annotation = null;
			String term = shgae.getTermHeader();
			
			if("OMIM".equals(shgae.getTermType())) {
				if(!diseaseCells.containsKey(term)) {
					diseaseCells.put(term, new GridTermHeaderAnnotation());
				}
				annotation = diseaseCells.get(term);
				
				if(currentGridEntry.getGenoClusterKey() != null && !currentGridEntry.getGenoClusterKey().equals("")) {
					annotation.incAnnotCount();
				} else {
					annotation.incHumanAnnotCount();
				}
			} else if("Mammalian Phenotype".equals(shgae.getTermType())) {
				if(!mpHeaderCells.containsKey(term)) {
					mpHeaderCells.put(term, new GridTermHeaderAnnotation());
				}
				annotation = mpHeaderCells.get(term);
				
				if(currentGridEntry.getGenoClusterKey() != null && !currentGridEntry.getGenoClusterKey().equals("")) {
					annotation.incAnnotCount();
				} else {
					annotation.incHumanAnnotCount();
				}
			}
			
			if(annotation != null) {
				annotation.incNormal("normal".equals(shgae.getQualifier()));
			}
			
		}

	}

	@Override
	public void Visit(GridTermHeaderAnnotation gridTermHeaderAnnotation) {

	}
	
	@Override
	public void Visit(GridCluster gridCluster) {
		gridCluster.setMouseSymbols(currentGridEntry.getGridMouseSymbols());
		gridCluster.setHumanSymbols(currentGridEntry.getGridHumanSymbols());
		gridCluster.setHomologyClusterKey(currentGridEntry.getHomologyClusterKey());
	}

	public GridResult getGridResult() {
		if(result == null) {
			result = new GridResult();
			result.setGridHighLights(gridHighLights);
			result.setGridMPHeaders(gridMPHeaders);
			result.setGridOMIMHeaders(gridOMIMHeaders);
			result.setFilterQuery(gridResults.getFilterQuery());
			result.setGridRows(new ArrayList<GridRow>());
			result.Accept(this);
		}
		return result;
	}
}
