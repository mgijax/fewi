package org.jax.mgi.fewi.hmdc.models;

import java.util.List;

import org.jax.mgi.fewi.hmdc.visitors.GridVisitorInterface;

public class GridResult extends AbstractGridModel {

	private List<String> gridMPHeaders;
	private List<String> gridDiseaseHeaders;
	private List<String> gridHighLights;
	private List<GridRow> gridRows;
	private String filterQuery;
	private String queryToken;

	public List<String> getGridMPHeaders() {
		return gridMPHeaders;
	}
	public void setGridMPHeaders(List<String> gridMPHeaders) {
		this.gridMPHeaders = gridMPHeaders;
	}
	public List<String> getGridDiseaseHeaders() {
		return gridDiseaseHeaders;
	}
	public void setGridDiseaseHeaders(List<String> gridDiseaseHeaders) {
		this.gridDiseaseHeaders = gridDiseaseHeaders;
	}
	public List<String> getGridHighLights() {
		return gridHighLights;
	}
	public void setGridHighLights(List<String> gridHighLights) {
		this.gridHighLights = gridHighLights;
	}
	public List<GridRow> getGridRows() {
		return gridRows;
	}
	public void setGridRows(List<GridRow> gridRows) {
		this.gridRows = gridRows;
	}	
	public String getFilterQuery() {
		return filterQuery;
	}
	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}
	public String getQueryToken() {
		return queryToken;
	}
	public void setQueryToken(String queryToken) {
		this.queryToken = queryToken;
	}
	
	@Override
	public void Accept(GridVisitorInterface pi) {
		pi.Visit(this);
	}
}
