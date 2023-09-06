package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/*-------*/
/* class */
/*-------*/

public class GxdLitQueryForm {

	private List<String> age = new ArrayList<String>();

	private Map<String, String> ages = new LinkedHashMap<String, String>();
	private Integer alleleKey;
	private List<String> assayType = new ArrayList<String>();
	
    private Map<String, String> assayTypes = new LinkedHashMap<String, String>();
	private String author = "";
	private List<String> authorFilter = new ArrayList<String>();
    private String authorScope = "any";
    private List<String> curatedDataFilter = new ArrayList<String>();
    private String id = "";
    private boolean inAbstract = true;
    private boolean inTitle = true;
    private String Journal = "";
    private List<String> journalFilter = new ArrayList<String>();
    private Integer marker_key;
	private String nomen = "";
    private Integer reference_key;
    private Integer seqKey;
	private String text = "";
	private String year = "";
	private List<Integer> yearFilter = new ArrayList<Integer>();
	
	// Added these for GXD query form links
    private String markerId;
    private String vocabTerm="";
	private String termId;

	public GxdLitQueryForm() {
    	
    	ages.put("ANY", "ANY");
    	ages.put("0.5", "0.5");
    	ages.put("1", "1");
    	ages.put("1.5", "1.5");
    	ages.put("2", "2");
    	ages.put("2.5", "2.5");
    	ages.put("3", "3");
    	ages.put("3.5", "3.5");
    	ages.put("4", "4");
    	ages.put("4.5", "4.5");
    	ages.put("5", "5");
    	ages.put("5.5", "5.5");
    	ages.put("6", "6");
    	ages.put("6.5", "6.5");
    	ages.put("7", "7");
    	ages.put("7.5", "7.5");
    	ages.put("8", "8");
    	ages.put("8.5", "8.5");
    	ages.put("9", "9");
    	ages.put("9.5", "9.5");
    	ages.put("10", "10");
    	ages.put("10.5", "10.5");
    	ages.put("11", "11");
    	ages.put("11.5", "11.5");
    	ages.put("12", "12");
    	ages.put("12.5", "12.5");
    	ages.put("13", "13");
    	ages.put("13.5", "13.5");
    	ages.put("14", "14");
    	ages.put("14.5", "14.5");
    	ages.put("15", "15");
    	ages.put("15.5", "15.5");
    	ages.put("16", "16");
    	ages.put("16.5", "16.5");
    	ages.put("17", "17");
    	ages.put("17.5", "17.5");
    	ages.put("18", "18");
    	ages.put("18.5", "18.5");
    	ages.put("19", "19");
    	ages.put("19.5", "19.5");
    	ages.put("20", "20");
    	ages.put("E", "E");
    	ages.put("P", "P");
    	
    	age.add("ANY");
    	
    	assayTypes.put("ANY", "ANY");
    	assayTypes.put("Immunohistochemistry (section)", "Immunohistochemistry (section)");
    	assayTypes.put("Immunohistochemistry (whole mount)", "Immunohistochemistry (whole mount)");
    	assayTypes.put("In situ RNA (section)", "In situ RNA (section)");
    	assayTypes.put("In situ RNA (whole mount)", "In situ RNA (whole mount)");
    	assayTypes.put("In situ reporter (knock in)", "In situ reporter (knock in)");
    	assayTypes.put("Northern blot", "Northern blot");
    	assayTypes.put("Western blot", "Western blot");
    	assayTypes.put("RT-PCR", "RT-PCR");
    	assayTypes.put("cDNA clones", "cDNA clones");
    	assayTypes.put("RNase protection", "RNase protection");
    	assayTypes.put("Nuclease S1", "Nuclease S1");
    	assayTypes.put("Primer Extension", "Primer Extension");
    	
    	assayType.add("ANY");
    	
    }
    public List<String> getAge() {
        return age;
    }

    public Map<String, String> getAges() {
		return ages;
	}
    public String getAgesSelected () {
    	if (! age.contains("ANY")) {
    		
    		// append 'E' to all numeric values
    		List<String> display = new ArrayList<String>();
    		for (String ageVal : age) {
    			if (!ageVal.equalsIgnoreCase("e") &&
    					!ageVal.equalsIgnoreCase("p")
    			) {
    				ageVal = 'E' + ageVal;
    			}
    			display.add(ageVal);
    		}
    		return StringUtils.join(display, " or ");
    	}
    	return null;
    }

    public Integer getAlleleKey() {
        return alleleKey;
    }
    public List<String> getAssayType() {
        return assayType;
    }
    
    public Map<String, String> getAssayTypes() {
		return assayTypes;
	}
    public String getAssayTypesSelected() {
    	if (!assayType.contains("ANY")) {
    		return StringUtils.join(assayType, " or ");
    	}
    	return null;
    }
    public String getAuthor() {
        return author;
    }

    public List<String> getAuthorFilter() {
        return authorFilter;
    }

    public String getAuthorScope() {
        return authorScope;
    }
    
    public List<String> getCuratedDataFilter() {
        return curatedDataFilter;
    }

    public String getId() {
        return id;
    }
    public String getJournal() {
        return Journal;
    }
    public List<String> getJournalFilter() {
        return journalFilter;
    }
    public Integer getMarker_key() {
		return marker_key;
	}
    public String getNomen() {
        return nomen;
    }
    public Integer getReference_key() {
		return reference_key;
	}
    public Integer getSeqKey() {
        return seqKey;
    }
    public String getText() {
        return text;
    }

    public String getYear() {
        return year;
    }
    public List<Integer> getYearFilter() {
        return yearFilter;
    }
    public boolean isInAbstract() {
        return inAbstract;
    }
    public boolean isInTitle() {
        return inTitle;
    }
    public void setAge(List <String> age) {
        this.age = age;
    }
    public void setAges(Map<String, String> ages) {
		this.ages = ages;
	}
    public void setAlleleKey(Integer alleleKey) {
        this.alleleKey = alleleKey;
    }
    public void setAssayType(List<String> assayType) {
        this.assayType = assayType;
    }
    public void setAssayTypes(Map<String, String> assayTypes) {
		this.assayTypes = assayTypes;
	}
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setAuthorFilter(List<String> authorFilter) {
        this.authorFilter = authorFilter;
    }
    public void setAuthorScope(String authorScope) {
        this.authorScope = authorScope;
    }
    public void setCuratedDataFilter(List<String> curatedDateFilter) {
        this.curatedDataFilter = curatedDateFilter;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setInAbstract(boolean inAbstract) {
        this.inAbstract = inAbstract;
    }
    public void setInTitle(boolean inTitle) {
        this.inTitle = inTitle;
    }
    public void setJournal(String journal) {
        Journal = journal;
    }
    public void setJournalFilter(List<String> journalFilter) {
        this.journalFilter = journalFilter;
    }
    public void setMarker_key(Integer marker_key) {
		this.marker_key = marker_key;
	}
    public void setNomen(String nomen) {
        this.nomen = nomen;
    }
    public void setReference_key(Integer reference_key) {
		this.reference_key = reference_key;
	}
    public void setSeqKey(Integer seqKey) {
        this.seqKey = seqKey;
    }
    public void setText(String text) {
        this.text = text;
    }

    public void setYear(String year) {
        this.year = year;
    }
    public void setYearFilter(List<Integer> yearFilter) {
        this.yearFilter = yearFilter;
    }
	public String getMarkerId() {
		return markerId;
	}
	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}
    public String getVocabTerm() {
		return vocabTerm;
	}
	public void setVocabTerm(String vocabTerm) {
		this.vocabTerm = vocabTerm;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
    @Override
	public String toString() {
		return "GxdLitQueryForm [age=" + age + ", ages=" + ages
				+ ", alleleKey=" + alleleKey + ", assayType=" + assayType
				+ ", assayTypes=" + assayTypes + ", author=" + author
				+ ", authorFilter=" + authorFilter + ", authorScope="
				+ authorScope + ", curatedDataFilter=" + curatedDataFilter
				+ ", id=" + id + ", inAbstract=" + inAbstract + ", inTitle="
				+ inTitle + ", Journal=" + Journal + ", journalFilter="
				+ journalFilter + ", marker_key=" + marker_key + ", nomen="
				+ nomen + ", reference_key=" + reference_key + ", seqKey="
				+ seqKey + ", text=" + text + ", year=" + year
				+ ", yearFilter=" + yearFilter + "]";
	}

}
