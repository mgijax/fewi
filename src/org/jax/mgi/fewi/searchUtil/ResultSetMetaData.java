package org.jax.mgi.fewi.searchUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultSetMetaData {
    
    Map<String, Set<String>> setHighlights = new HashMap<String, Set<String>> ();
    
    
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
}
