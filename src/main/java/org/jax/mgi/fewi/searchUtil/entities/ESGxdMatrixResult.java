package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;


public class ESGxdMatrixResult extends ESEntity implements SolrGxdEntity {
	protected String printname;
	protected String structureId;
	protected Integer theilerStage;
	protected String detectionLevel;
	protected String geneSymbol;

	protected Integer count;
	
	public static final List<String> RETURN_FIELDS = List.of(GxdResultFields.STRUCTURE_PRINTNAME,
			GxdResultFields.STRUCTURE_EXACT, GxdResultFields.THEILER_STAGE, GxdResultFields.DETECTION_LEVEL,
			GxdResultFields.MARKER_SYMBOL);

	public String getPrintname() {
		return printname;
	}

	public void setPrintname(String printname) {
		this.printname = printname;
	}

	public Integer getTheilerStage() {
		return theilerStage;
	}

	public void setTheilerStage(Integer theilerStage) {
		this.theilerStage = theilerStage;
	}

	public String getDetectionLevel() {
		return detectionLevel;
	}

	public void setDetectionLevel(String detectionLevel) {
		this.detectionLevel = detectionLevel;
	}

	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "SolrGxdMatrixResult [printname=" + printname + ", structureId="
				+ structureId + ", theilerStage=" + theilerStage
				+ ", detectionLevel=" + detectionLevel + ", geneSymbol="
				+ geneSymbol + ", count=" + count + "]";
	}
}
