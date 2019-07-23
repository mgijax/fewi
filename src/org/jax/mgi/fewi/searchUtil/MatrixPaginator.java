package org.jax.mgi.fewi.searchUtil;

public class MatrixPaginator {

	private Integer startIndexMatrix = 0;
	private Integer resultsMatrix;
	private Integer resultsDefault = 25;

	
	public MatrixPaginator() {}
	public MatrixPaginator(Integer resultsMatrix)
	{
		this.resultsMatrix=resultsMatrix;
	}
	
    // startIndex
	public Integer getStartIndexMatrix() {
        if (startIndexMatrix < 0){return 0;}
		return startIndexMatrix;
	}
	public void setStartIndexMatrix(Integer startIndexMatrix) {
		this.startIndexMatrix = startIndexMatrix;
	}

    // results
	public Integer getResultsMatrix() {
        if (resultsMatrix==null){return resultsDefault;}
		return resultsMatrix;
	}
	public void setResultsMatrix(Integer resultsMatrix) {
		this.resultsMatrix = resultsMatrix;
	}


    // toString for debugging purposes
	@Override
	public String toString() {
		return "MatrixPaginator [resultsMatrix=" + resultsMatrix + ", startIndexMatrix=" + startIndexMatrix
				+ "]";
	}
}
