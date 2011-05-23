package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.xnio.log.Logger;
import org.springframework.web.multipart.MultipartFile;

public class BatchQueryForm {
	private String idType = "auto";
	private Map<String, String> idTypes = new LinkedHashMap<String, String>();
	private String ids;
	private MultipartFile idFile = null;
	private String fileType = "tab";
	private Integer idColumn = new Integer(1);
	private List<String> attributes = new ArrayList<String>();
	private String association = "None";
		
	private Map<String, String> attributeList = new LinkedHashMap<String, String>();
	private Map<String, String> associationList = new LinkedHashMap<String, String>();
	
	public BatchQueryForm() {
		idTypes.put("auto", "Search all input types");
		idTypes.put("MGI", "MGI Gene/Marker ID");
		idTypes.put("current symbol", "Current Symbols Only");
		idTypes.put("nomen", "All Symbols/Synonyms/Orthologs");
		idTypes.put("Entrez Gene", "Entrez Gene ID");
		idTypes.put("Ensembl", "Ensembl ID");
		idTypes.put("VEGA", "VEGA ID");
		idTypes.put("UniGene", "UniGene ID");
		idTypes.put("miRBase", "miRBase ID");
		idTypes.put("GenBank", "GenBank/RefSeq ID");
		idTypes.put("UniProt", "UniProt ID");
		idTypes.put("Gene Ontology (GO)", "GO ID");
		idTypes.put("RefSNP", "RefSNP ID");
		idTypes.put("Affy 1.0 ST", "Affy 1.0 ST");
		idTypes.put("Affy 430 2.0", "Affy 430 2.0");
		
		attributeList.put("Nomenclature", "Nomenclature");
		attributeList.put("Location", "Genome Location");
		attributeList.put("Ensembl", "Ensembl ID");
		attributeList.put("EntrezGene", "Entrez Gene ID");
		attributeList.put("VEGA", "VEGA ID");
		
		attributes.add("Nomenclature");
		
		associationList.put("GO", "Gene Ontology (GO)");
		associationList.put("MP", "Mammalian Phenotype (MP");
		associationList.put("omim", "Human Disease (OMIM)");
		associationList.put("Alleles", "Alleles");
		associationList.put("exp", "Gene Expression");
		associationList.put("RefSNP", "RefSNP ID");
		associationList.put("GenBankRefSeq", "GenBank/RefSeq ID");
		associationList.put("UniProt", "UniProt ID");
		associationList.put("None", "None");

	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Integer getIdColumn() {
		return idColumn;
	}
	public void setIdColumn(Integer idColumn) {
		this.idColumn = idColumn;
	}
	public List<String> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	public String getAssociation() {
		return association;
	}
	public void setAssociation(String association) {
		this.association = association;
	}
	public MultipartFile getIdFile() {
		return idFile;
	}
	public void setIdFile(MultipartFile idFile) {
		this.idFile = idFile;
	}
	public boolean getHasFile(){
		if (idFile == null || idFile.isEmpty()){
			return false;
		}
		return true;
	}
	public String getFileName(){
		if (!idFile.isEmpty()){
			return idFile.getOriginalFilename();
		}
		return "";
	}
	@Override
	public String toString() {
		return "BatchQueryForm [association=" + association + ", attributes="
				+ attributes + ", fileType=" + fileType + ", idColumn="
				+ idColumn 
				+ ", idType=" + idType + ", ids=" + ids + "]";
	}
	public String toQueryString(){
		List<String> params = new ArrayList<String>();
		if (association != null && !"".equals(association)){
			params.add("association=" + association);
		}
		if (attributes != null && !attributes.isEmpty()){
			for (String attr : attributes) {
				params.add("attributes=" + attr);
			}	
		}
		if (fileType != null && !"".equals(fileType)){
			params.add("fileType=" + fileType);
		}
		if (idColumn != null && !"".equals(idColumn)){
			params.add("idColumn=" + idColumn);
		}
		if (idType != null && !"".equals(idType)){
			params.add("idType=" + idType);
		}
		if (ids != null && !"".equals(ids)){
			String s = ids.replaceAll("\r\n", " ");
			params.add("ids=" + s);
		}
		return StringUtils.join(params, "&");
	}
	
	public Map<String, String> getIdTypes() {
		return idTypes;
	}
	public void setIdTypes(Map<String, String> idTypes) {
		this.idTypes = idTypes;
	}
	public Map<String, String> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(Map<String, String> attributeList) {
		this.attributeList = attributeList;
	}
	private boolean hasAttribute(String att){
		if (attributes != null && attributes.contains(att)){
			return true;			
		}
		return false;
	}
	public boolean getNomenclature(){
		return this.hasAttribute("Nomenclature");
	}
	public boolean getLocation(){
		return this.hasAttribute("Location");
	}
	public boolean getEnsembl(){
		return this.hasAttribute("Ensembl");
	}
	public boolean getEntrez(){
		return this.hasAttribute("EntrezGene");
	}
	public boolean getVega(){
		return this.hasAttribute("VEGA");
	}
	public Map<String, String> getAssociationList() {
		return associationList;
	}
	public void setAssociationList(Map<String, String> associationList) {
		this.associationList = associationList;
	}
	private boolean hasAssociation(String ass){
		if (association != null && association.equals(ass)){
			return true;			
		}
		return false;
	}
	public boolean getGo(){
		return this.hasAssociation("GO");
	}
	public boolean getMp(){
		return this.hasAssociation("MP");
	}
	public boolean getOmim(){
		return this.hasAssociation("omim");
	}
	public boolean getAllele(){
		return this.hasAssociation("Alleles");
	}
	public boolean getExp(){
		return this.hasAssociation("exp");
	}
	public boolean getRefsnp(){
		return this.hasAssociation("RefSNP");
	}
	public boolean getRefseq(){
		return this.hasAssociation("GenBankRefSeq");
	}
	public boolean getUniprot(){
		return this.hasAssociation("UniProt");
	}
	public String getOutputOptions(){
		List<String> output = new ArrayList<String>();
		output.addAll(attributes);
		if (!"none".equalsIgnoreCase(association)){
			output.add(association);
		}
		return StringUtils.join(output, ", ");		
	}
}
