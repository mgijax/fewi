package org.jax.mgi.fewi.summary;

import org.jax.mgi.fewi.config.ContextLoader;

import mgi.frontend.datamodel.Reference;

public class ReferenceSummary {
	private Reference reference;
	private String score;

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public void setScore(String score){
		this.score = score;
	}

	public String getScore() {
		return score;
	}	
	
	public String getVol(){
		StringBuffer sb = new StringBuffer();
		if (this.reference.getVol() != null && !"".equals(this.reference.getVol())){
			sb.append(this.reference.getVol());
		}
		if (this.reference.getIssue() != null && !"".equals(this.reference.getIssue())){
			sb.append("(" + this.reference.getIssue() + ")");
		} else {
			sb.append(" ");
		}
		if (this.reference.getPages() != null && !"".equals(this.reference.getPages())){
			sb.append(this.reference.getPages());
		}		
		return sb.toString();
	}

	public String getPubMedID() {
		return reference.getPubMedID();
	}

	public String getJnumID() {
		return reference.getJnumID();
	}

	public String getAuthors() {
		return reference.getAuthors();
	}

	public String getTitle() {
		return reference.getTitle();
	}

	public String getJournal() {
		return reference.getJournal();
	}

	public String getYear() {
		return reference.getYear();
	}
	
	public String getCuratedData(){
		StringBuffer sb = new StringBuffer();
		
		String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
		
        sb.append("<ul class=\"curatedData\">");
        
        int expTotal = reference.getCountOfGXDAssays() + reference.getCountOfGXDResults() + reference.getCountOfGXDStructures();
        if (expTotal > 0){
        	sb.append(String.format("<li>Expression assays: <a href=\"%sexpression/reference/%s\">%,d</a>,", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfGXDAssays()));
        	sb.append(String.format(" results: <a href=\"%sexpression/reference/%s\">%,d</a>,", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfGXDResults()));
        	sb.append(String.format(" tissues: <a href=\"%sexpression/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfGXDStructures()));
        }
        if (reference.getCountOfGXDIndex() > 0){
        	sb.append(String.format("<li>Gene expression literature content records: <a href=\"%sexpression/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfGXDIndex()));
        }
        if(reference.getCountOfMarkers() > 0){
        	sb.append(String.format("<li>Genome features: <a href=\"%smarker/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfMarkers()));
        }
        if(reference.getCountOfAlleles() > 0){
        	sb.append(String.format("<li>Phenotypic alleles: <a href=\"%sallele/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfAlleles()));
        }
        if(reference.getCountOfOrthologs() > 0){
        	sb.append(String.format("<li>Mamallian orthologs: <a href=\"%sorthalog/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfOrthologs()));
        }
        if(reference.getCountOfMappingResults() > 0){
        	sb.append(String.format("<li>Mapping data: <a href=\"%smapping/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfMappingResults()));
        }
        if(reference.getCountOfProbes() > 0){
        	sb.append(String.format("<li>Molecular probes and clones: <a href=\"%sprobe/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfProbes()));
        }
        if(reference.getCountOfSequenceResults() >0){
        	sb.append(String.format("<li>Sequences: <a href=\"%ssequence/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfSequenceResults()));  
        }
        sb.append("</ul>"); 		
		
		return sb.toString();
	}
	
	public String getAbstract(){
		if (this.reference.getAbstract() != null){
			return this.reference.getAbstract();
		} else {
			return " ";
		}
	}

}
