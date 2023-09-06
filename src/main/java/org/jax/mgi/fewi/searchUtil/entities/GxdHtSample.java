package org.jax.mgi.fewi.searchUtil.entities;

/* Is: a sample for a high-throughput expression experiment
 * Notes: is populated using data from the gxdHtSample Solr index
 */
public class GxdHtSample
{
	private Integer sampleKey;
	private String structureTerm;
	private String structureID;
	private String celltypeTerm;
	private String celltypeID;
	private String age;
	private Integer theilerStage;
	private String sex;
	private Integer experimentKey;
	private String name;
	private String organism;
	private String mutantAlleles;
	private String geneticBackground;
	private String relevancy;
	private String note;
	private Integer byDefault;
	private boolean matchesSearch = false;

	public boolean getMatchesSearch() {
		return matchesSearch;
	}
	public void setMatchesSearch(boolean matchesSearch) {
		this.matchesSearch = matchesSearch;
	}
	public String getRelevancy() {
		return relevancy;
	}
	public void setRelevancy(String relevancy) {
		this.relevancy = relevancy;
	}
	public Integer getSampleKey() {
		return sampleKey;
	}
	public void setSampleKey(Integer sampleKey) {
		this.sampleKey = sampleKey;
	}
	public String getStructureTerm() {
		return structureTerm;
	}
	public void setStructureTerm(String structureTerm) {
		this.structureTerm = structureTerm;
	}
	public String getStructureID() {
		return structureID;
	}
	public void setStructureID(String structureID) {
		this.structureID = structureID;
	}
        //
	public String getCelltypeID() {
		return celltypeID;
	}
	public void setCelltypeID(String celltypeID) {
		this.celltypeID = celltypeID;
	}
        //
	public String getCelltypeTerm() {
		return celltypeTerm;
	}
	public void setCelltypeTerm(String celltypeTerm) {
		this.celltypeTerm = celltypeTerm;
	}
        //
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public Integer getTheilerStage() {
		return theilerStage;
	}
	public void setTheilerStage(Integer theilerStage) {
		this.theilerStage = theilerStage;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getExperimentKey() {
		return experimentKey;
	}
	public void setExperimentKey(Integer experimentKey) {
		this.experimentKey = experimentKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	public String getMutantAlleles() {
		return mutantAlleles;
	}
	public void setMutantAlleles(String mutantAlleles) {
		this.mutantAlleles = mutantAlleles;
	}
	public String getGeneticBackground() {
		return geneticBackground;
	}
	public void setGeneticBackground(String geneticBackground) {
		this.geneticBackground = geneticBackground;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Integer getByDefault() {
		return byDefault;
	}
	public void setByDefault(Integer byDefault) {
		this.byDefault = byDefault;
	}
	@Override
	public String toString() {
		return "GxdHtSample [sampleKey=" + sampleKey + ", structureTerm=" + structureTerm + ", structureID="
				+ structureID + ", age=" + age + ", theilerStage=" + theilerStage + ", sex=" + sex + ", experimentKey="
				+ experimentKey + ", name=" + name + ", organism=" + organism + ", mutantAlleles=" + mutantAlleles
				+ ", geneticBackground=" + geneticBackground + ", note=" + note + ", byDefault=" + byDefault + "]";
	}
}
