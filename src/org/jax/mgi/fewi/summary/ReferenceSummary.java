package org.jax.mgi.fewi.summary;

import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.Highlighter;

public class ReferenceSummary {
	private Reference reference;
	private String score;
	private String url = ContextLoader.getExternalUrls().getProperty("PubMed");
	private Highlighter titleHL = new Highlighter(null);
	private Highlighter abstractHL = new Highlighter(null);
	private Highlighter authorHL = new Highlighter(null);

	public ReferenceSummary(Reference reference) {
		this.reference = reference;
	}

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
			sb.append(" (" + this.reference.getIssue() + ") ");
		} else {
			sb.append(" ");
		}
		if (this.reference.getPages() != null && !"".equals(this.reference.getPages())){
			sb.append(this.reference.getPages());
		}		
		return sb.toString();
	}

	public String getId() {		
		StringBuffer sb = new StringBuffer();
		
		String pId = reference.getPubMedID();
		if(pId != null && !"".equals(pId)){
			String p = url.replace("@@@@", pId);
			sb.append(String.format("<a href=\"%s\" target=\"new\" class=\"extUrl\">%s</a><br/>", 
					p, pId));			
		}
		sb.append(reference.getJnumID());
		return sb.toString();
	}

	public String getAuthors() {
		StringBuffer sb = new StringBuffer();
		if ("BOOK".equalsIgnoreCase(this.reference.getReferenceType()) && 
				this.reference.getBookEditor() != null){
			sb.append(String.format("<br/><br/><span class=\"ital\">Editors</span>: %s", this.reference.getBookEditor()));
		}
		return authorHL.highLight(reference.getAuthors()) + sb.toString();
	}

	public String getTitle() {
		StringBuffer sb = new StringBuffer();
		if ("BOOK".equalsIgnoreCase(this.reference.getReferenceType())){
			if(this.reference.getBookTitle() != null){
				sb.append(String.format("<span class=\"ital\">Chapter</span>: %s<br/><br/>", titleHL.highLight(this.reference.getTitle())));
				sb.append(String.format("<span class=\"ital\">Book</span>: %s", titleHL.highLight(this.reference.getBookTitle())));
			} else if(this.reference.getTitle() != null) {
				sb.append(String.format("<span class=\"ital\">Book</span>: %s", titleHL.highLight(this.reference.getTitle())));
			}
		} else if(this.reference.getTitle() != null) {
			sb.append(titleHL.highLight(this.reference.getTitle()));
		}
		return sb.toString();
	}

	public String getJournal() {
		if("BOOK".equalsIgnoreCase(this.reference.getReferenceType()) && 
				this.reference.getBookPublisher() != null){
			StringBuffer sb = new StringBuffer();
			sb.append("<span class=\"ital\">Publisher</span>: " + this.reference.getBookPublisher());
			if (this.reference.getBookPlace() != null){
				sb.append(", " + this.reference.getBookPlace());
			}
			return sb.toString();
		}
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
        	sb.append(String.format(" results: <a href=\"%sexpression/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfGXDResults()));
        }
        if (reference.getCountOfGXDIndex() > 0){
        	sb.append(String.format("<li>Expression literature records: <a href=\"%sgxdlit/reference/%s\">%,d</a></li>", fewiUrl, this.reference.getJnumID(), this.reference.getCountOfGXDIndex()));
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
		if (this.reference.getAbstract() != null 
				&& !"".equals(this.reference.getAbstract())){
			return abstractHL.highLight(this.reference.getAbstract());
		} else {
			return abstractHL.highLight("this reference has no abstract");
		}		
	}

	public String getBookEdition() {
		return reference.getBookEdition();
	}

	public String getBookEditor() {
		return reference.getBookEditor();
	}

	public String getBookPlace() {
		return reference.getBookPlace();
	}

	public String getBookPublisher() {
		return reference.getBookPublisher();
	}

	public String getBookTitle() {
		return reference.getBookTitle();
	}

	public void setTitleHL(Highlighter highlighter) {
		this.titleHL = highlighter;
	}
	
	public void setAbstractHL(Highlighter highlighter) {
		this.abstractHL = highlighter;
	}
	
	public void setAuthorHL(Highlighter highlighter) {
		this.authorHL = highlighter;
	}

}
