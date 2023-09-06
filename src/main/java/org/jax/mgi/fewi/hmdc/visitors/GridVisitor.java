package org.jax.mgi.fewi.hmdc.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.jax.mgi.fe.datamodel.sort.SmartAlphaComparator;
import org.jax.mgi.fewi.hmdc.models.GridCluster;
import org.jax.mgi.fewi.hmdc.models.GridResult;
import org.jax.mgi.fewi.hmdc.models.GridRow;
import org.jax.mgi.fewi.hmdc.models.GridTermHeaderAnnotation;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.searchUtil.PrinterUtil;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.jsonmodel.GridMarker;

public class GridVisitor extends PrinterUtil implements GridVisitorInterface {

	private GridResult result = null;
	//private List<String> gridMPHeaders = null;
	
	private List<String> gridHighLights = null;
	private List<String> gridMPHeaders = null;
	private List<String> gridDiseaseHeaders = null;

	private SolrHdpGridEntry currentGridEntry;
	
	private final HashMap<Integer, ArrayList<SolrHdpGridAnnotationEntry>> annotationMap = new HashMap<>();

	private final SearchResults<SolrHdpGridEntry> gridResults;
	
	public GridVisitor(SearchResults<SolrHdpGridEntry> gridResults, SearchResults<SolrHdpGridAnnotationEntry> annotationResults, List<String> highLights) {
		this.gridResults = gridResults;
		
		gridHighLights = highLights;
		gridMPHeaders = new ArrayList<String>();
		gridDiseaseHeaders = new ArrayList<String>();

		for(SolrHdpGridAnnotationEntry ar: annotationResults.getResultObjects()) {
			
			if(!gridMPHeaders.contains(ar.getTermHeader()) && ("Mammalian Phenotype".equals(ar.getTermType()) || "Human Phenotype Ontology".equals(ar.getTermType()))) {
				gridMPHeaders.add(ar.getTermHeader());
			}
			if(!gridDiseaseHeaders.contains(ar.getTermHeader()) && "Disease Ontology".equals(ar.getTermType())) {
				gridDiseaseHeaders.add(ar.getTermHeader());
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
		TreeMap<String, GridRow> sortedMap = new TreeMap<String, GridRow>(new SmartAlphaComparator<String>());
		
		for(SolrHdpGridEntry res: gridResults.getResultObjects()) {
			currentGridEntry = res;
			Integer gridClusterKey = res.getGridClusterKey();
			
			if(!rowMap.containsKey(gridClusterKey)) {
				rowMap.put(gridClusterKey, new GridRow());
			}
			rowMap.get(gridClusterKey).Accept(this);
			
			String index = "";
			for (GridMarker gm: rowMap.get(gridClusterKey).getGridCluster().getHumanSymbols()) {
			    index += gm.getSymbol();
			}
			for (GridMarker gm: rowMap.get(gridClusterKey).getGridCluster().getMouseSymbols()) {
			    index += gm.getSymbol();
			}
			
			index += gridClusterKey;

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
			
			if("Disease Ontology".equals(shgae.getTermType())) {
				if(!diseaseCells.containsKey(term)) {
					diseaseCells.put(term, new GridTermHeaderAnnotation());
				}
				annotation = diseaseCells.get(term);
				
				if(currentGridEntry.getGenoClusterKey() != null && !currentGridEntry.getGenoClusterKey().equals("")) {
					annotation.incAnnotCount();
					if("normal".equals(shgae.getQualifier())) {
						annotation.incNormalCount();
					}
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
					if("normal".equals(shgae.getQualifier())) {
						annotation.incNormalCount();
					}
				} else {
					annotation.incHumanAnnotCount();
				}
			} else if("Human Phenotype Ontology".equals(shgae.getTermType())) {
				if(!mpHeaderCells.containsKey(term)) {
					mpHeaderCells.put(term, new GridTermHeaderAnnotation());
				}
				annotation = mpHeaderCells.get(term);
				annotation.incHumanAnnotCount();
			}
			
			if(annotation != null) {
				annotation.setHeader(shgae.getTermHeader());

				if(shgae.getTermType().equals("Disease Ontology") && !annotation.getTerms().contains(shgae.getTerm())) {
					//System.out.println("Term: " + shgae.getTerm());
					//System.out.println("Type: " + shgae.getTermType());
					annotation.getTerms().add(shgae.getTerm());
				}
				annotation.setGridClusterKey(currentGridEntry.getGridClusterKey());
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
			result.setGridDiseaseHeaders(gridDiseaseHeaders);
			result.setFilterQuery(gridResults.getFilterQuery());
			result.setGridRows(new ArrayList<GridRow>());
			result.Accept(this);
		}
		return result;
	}
}
