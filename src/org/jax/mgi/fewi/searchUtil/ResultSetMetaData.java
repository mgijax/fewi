package org.jax.mgi.fewi.searchUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultSetMetaData {
    
    Map<String, Set<String>> setHighlights = new HashMap<String, Set<String>> ();
    Map<String, Integer> counts = new HashMap<String, Integer> ();
    
    public ResultSetMetaData() {};
    
    public ResultSetMetaData(Map<String, Set<String>> highlights) {
        this.setHighlights = highlights;
    };
    
    public Map<String, Set<String>> getSetHighlights() {
        return setHighlights;
    }

    public void setSetHighlights(Map<String, Set<String>> setHighlights) {
        this.setHighlights = setHighlights;
    }
 
    public String toString() {
        
        String outString = "";
        for (String key: this.setHighlights.keySet()) {
            outString += " Key: " + key + " ";
            Set<String> valueList = this.setHighlights.get(key);
            for (String value: valueList) {
                outString += "value: " + value + " ";
            }
            
        }
        return outString;
    }

	public Map<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(Map<String, Integer> counts) {
		this.counts = counts;
	}
	
	public void addCount(String countName, Integer count){
		counts.put(countName, count);
	}
	
	public Integer getCount(String countName){
		if (counts.containsKey(countName)) {
			return counts.get(countName);
		}
		else return new Integer(0);
	}
}
