package org.jax.mgi.fewi.summary;

/**
 * Hold minimal data for a RNA-Seq heat map sample
 */
public class GxdRnaSeqHeatMapSample implements Cloneable {
	public String structure;
	public String age;
	public Integer stage;
	public String alleles;
	public String strain;
	public String sex;
	public String expID;
	public String bioreplicateSetID;
	public Integer index;
	public Integer bioreplicateCount;
		
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

	public Integer getBioreplicateCount() {
		return bioreplicateCount;
	}
	public void setBioreplicateCount(Integer bioreplicateCount) {
		this.bioreplicateCount = bioreplicateCount;
	}

	public Integer getStage() {
		return stage;
	}
	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public String getAlleles() {
		return alleles;
	}
	public void setAlleles(String alleles) {
		this.alleles = alleles;
	}

	public String getStrain() {
		return strain;
	}
	public void setStrain(String strain) {
		this.strain = strain;
	}

	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getExpID() {
		return expID;
	}
	public void setExpID(String expID) {
		this.expID = expID;
	}

	public String getBioreplicateSetID() {
		return bioreplicateSetID;
	}
	public void setBioreplicateSetID(String bioreplicateSetID) {
		this.bioreplicateSetID = bioreplicateSetID;
	}

	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getLabel() {
		StringBuffer sb = new StringBuffer();
		sb.append(structure);
		sb.append("_");
		sb.append(expID);
		sb.append("_");
		sb.append(bioreplicateSetID);
		if (!"wild-type".equals(this.alleles)) {
			// add a black star to the end to flag those with mutant alleles
			sb.append("\u2605");
		}
		return sb.toString();
	}

	public GxdRnaSeqHeatMapSample clone() throws CloneNotSupportedException {
		return (GxdRnaSeqHeatMapSample) super.clone();
	}
}
